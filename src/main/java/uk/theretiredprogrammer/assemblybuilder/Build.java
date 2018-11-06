/*
 * Copyright 2018 richard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.theretiredprogrammer.assemblybuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.NUMBER;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
/**
 *
 * @author richard
 */
public class Build {

    private FileObject[] fos;
    private FileObject resourcefolder;
    private String resourcefolderpath;

    public Build setResourseFolder(FileObject resourcefolder, String resourcefolderpath) {
        this.resourcefolder = resourcefolder;
        this.resourcefolderpath = resourcefolderpath;
        return this;
    }

    public Build setFolderSeachOrder(FileObject... fos) {
        this.fos = fos;
        return this;
    }

    public String getContent(JsonObject... jobjs) throws IOException {
        String copyfile = jobjs[0].getString("copy-file", "");
        if (!copyfile.isEmpty()) {
            return copyFileContent(copyfile, jobjs);
        }
        String markdownfile = jobjs[0].getString("markdown", "");
        if (!markdownfile.isEmpty()) {
            return markdownContent(markdownfile, jobjs);
        }
        String imagesetfile = jobjs[0].getString("create-imageset", "");
        if (!imagesetfile.isEmpty()) {
            return imageSetContent(imagesetfile, jobjs);
        }
        throw new IOException("No action paramater defined (one of copy-file, markdown, or create-imageset)");
    }

    private String copyFileContent(String filename, JsonObject... jobjs) throws IOException {
        FileObject file = IoUtil.findFile(filename, fos);
        return substitute(file.asText(), jobjs);
    }

    private String markdownContent(String filename, JsonObject... jobjs) throws IOException {
        FileObject file = IoUtil.findFile(filename, fos);
        StringWriter toWriter = new StringWriter();
        FileObject mdtemplatefile = IoUtil.findFile("mdtemplate.xml", fos);
        ProcessBuilder pb;
        if (mdtemplatefile == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", mdtemplatefile.getPath());
        }
        pb.redirectInput(FileUtil.toFile(file));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = fromReader.readLine()) != null) {
                toWriter.append(line);
                toWriter.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException("Interrupt Exception caught and rethrown");
        }
        return toWriter.toString();
    }

    private String imageSetContent(String filename, JsonObject... jobjs) throws IOException {
        FileObject rootimagefile = resourcefolder.getFileObject(filename);
        if (rootimagefile == null) {
            throw new IOException("Missing image file");
        }
        String fn = rootimagefile.getName();
        int fnsize = fn.length();
        String fext = rootimagefile.getExt();
        int width = getJsonInt("width", jobjs);
        int height = getJsonInt("height", jobjs);
        Map<Integer, String> images = new TreeMap<>();
        for (FileObject child : resourcefolder.getChildren()) {
            if (child.isData()) {
                String cn = child.getName();
                String cext = child.getExt();
                if (cext.equals(fext)) {
                    if (cn.equals(fn)) {
                        images.put(width, child.getNameExt());
                    } else {
                        if (cn.startsWith(fn+"-")) {
                            String postfix = cn.substring(fnsize+1);
                            for (int i = 0; i < postfix.length(); i++) {
                                char c = postfix.charAt(i);
                                if (c >= '0' && c <= '9') {
                                    int q = postfix.indexOf('x', i);
                                    int iwidth = Integer.parseInt(postfix.substring(i, q));
                                    images.put(iwidth, child.getNameExt());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<img width=\"");
        sb.append(width);
        sb.append("\" height=\"");
        sb.append(height);
        sb.append("\"\n    src=\"");
        sb.append(resourcefolderpath);
        sb.append(rootimagefile.getNameExt());
        sb.append("\"\n    class=\"attachment-full size-full\" alt=\"\"\n    srcset=\"");
        String prefix = "";
        for (Map.Entry<Integer, String> es : images.entrySet()) {
            sb.append(prefix);
            sb.append(resourcefolderpath);
            sb.append(es.getValue());
            sb.append(" ");
            sb.append(es.getKey());
            sb.append('w');
            prefix = ",\n    ";
        }
        sb.append("\"\n    sizes=\"(max-width: ");
        sb.append(width);
        sb.append("px) 100vw, ");
        sb.append(width);
        sb.append("px\" />\n");
        return sb.toString();
    }

    private int getJsonInt(String name, JsonObject... jobjs) throws IOException {
        JsonValue jval = getJsonValue(name, jobjs);
        if (jval == null){
            return 0;
        }
        if (jval.getValueType() == NUMBER) {
            return ((JsonNumber) jval).intValue();
        }
        throw new IOException("parameter is not an integer");
    }

    private JsonValue getJsonValue(String name, JsonObject... jobjs){
        for (JsonObject jobj : jobjs) {
            JsonValue jval = jobj.get(name);
            if (jval != null) {
                return jval;
            }
        }
        return null;
    }

    private String substitute(String text, JsonObject... jobjs) throws IOException {
        while (true) {
            int p = text.indexOf("${");
            if (p == -1) {
                return text;
            }
            int q = text.indexOf("}", p + 2);
            String name = text.substring(p + 2, q);
            text = text.substring(0, p) + extractContent(name, jobjs) + text.substring(q + 1);
        }
    }

    private String extractContent(String name, JsonObject... jobjs) throws IOException {
        JsonValue jval = getJsonValue(name, jobjs);
        if (jval == null) {
            return "";
        }
        switch (jval.getValueType()) {
            case OBJECT:
                JsonObject[] next = new JsonObject[jobjs.length + 1];
                next[0] = (JsonObject) jval;
                System.arraycopy(jobjs, 0, next, 1, jobjs.length);
                return getContent(next);
            case ARRAY:
                throw new IOException("Array content not allowed");
            case STRING:
                return ((JsonString) jval).getString();
            case NUMBER:
                JsonNumber jnum = (JsonNumber) jval;
                if (jnum.isIntegral()) {
                    return Integer.toString(jnum.intValueExact());
                } else {
                    return jnum.toString();
                }
            default:
                return jval.toString();
        }
    }
}
