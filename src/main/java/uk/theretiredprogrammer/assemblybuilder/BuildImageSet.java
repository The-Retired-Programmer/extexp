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

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import javax.json.JsonObject;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public class BuildImageSet extends Build {

    private final FileObject rootimagefile;
    private final Usings usings;
    private final FileObject resourcefolder;
    private final String resourcefolderpath;

    public BuildImageSet(JsonObject jobj, FileObject resourcefolder, String resourcefolderpath) throws IOException {
        this.resourcefolder = resourcefolder;
        this.resourcefolderpath = resourcefolderpath;
        String filename = jobj.getString("file", "");
        if (filename.equals("")) {
            throw new IOException("Missing file entry");
        }
        rootimagefile = resourcefolder.getFileObject(filename);
        if (rootimagefile == null) {
            throw new IOException("Missing image file");
        }
        JsonObject jusing = jobj.getJsonObject("using");
        if (jusing == null) {
            usings = new Usings();
        } else {
            usings = Build.buildUse(jusing);
        }
    }

    @Override
    public String getContentString(Usings parentusings) throws IOException {
        Usings combined = new Usings(parentusings);
        combined.putAll(usings);
        return buildimageset(combined);
    }

    private String buildimageset(Usings params) throws IOException {
        String fn = rootimagefile.getName();
        int fnsize = fn.length();
        String fext = rootimagefile.getExt();
        int width = Integer.parseInt(params.get("width").getContentString(usings));
        int height = Integer.parseInt(params.get("height").getContentString(usings));
        Map<Integer, String> images = new TreeMap<>();
        for (FileObject child : resourcefolder.getChildren()) {
            if (child.isData()) {
                String cn = child.getName();
                String cext = child.getExt();
                if (cext.equals(fext)) {
                    if (cn.equals(fn)) {
                        images.put(width, child.getNameExt());
                    } else {
                        if (cn.startsWith(fn)) {
                            String postfix = cn.substring(fnsize);
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
}
