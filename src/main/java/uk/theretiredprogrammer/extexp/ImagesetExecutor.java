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
package uk.theretiredprogrammer.extexp;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.extexp.IODescriptor.IOREQUIREMENT.RESOURCESDESCRIPTOR;
import static uk.theretiredprogrammer.extexp.IODescriptor.IOREQUIREMENT.WRITER;
import static uk.theretiredprogrammer.extexp.IODescriptor.IOREQUIREMENT.PARAMSTRING;

/**
 *
 * @author richard
 */
public class ImagesetExecutor extends Executor {

    private final IODescriptor<ResourcesDescriptor> rdesc = new IODescriptor<>(RESOURCESDESCRIPTOR);
    private final IODescriptor<Writer> output = new IODescriptor<>("to", WRITER);
    private final IODescriptor<String> image = new IODescriptor<>("image", PARAMSTRING);
    private final IODescriptor<String> width = new IODescriptor<>("width", PARAMSTRING);
    private  final IODescriptor<String> height = new IODescriptor<>("height", PARAMSTRING);
     

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{rdesc, output, image, width, height};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        ResourcesDescriptor rd = rdesc.getValue();
        String imagestring = image.getValue();
        int p = imagestring.lastIndexOf('.');
        String fn = imagestring.substring(0, p);
        String fext = imagestring.substring(p + 1);
        int fnsize = fn.length();
        String widthstring = width.getValue();
        String heightstring = height.getValue();
        Map<Integer, String> images = new TreeMap<>();
        String relativeresourcesfolderpath = rd.relativeResourcesPath;
        for (FileObject child : rd.resourcesFolder.getChildren()) {
            if (child.isData()) {
                String cn = child.getName();
                String cext = child.getExt();
                if (cext.equals(fext)) {
                    if (cn.equals(fn)) {
                        images.put(Integer.parseInt(widthstring), child.getNameExt());
                    } else {
                        if (cn.startsWith(fn + "-")) {
                            String postfix = cn.substring(fnsize + 1);
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
        Writer out = output.getValue();
        out.append("<img width=\"");
        out.append(widthstring);
        out.append("\" height=\"");
        out.append(heightstring);
        out.append("\"\n    src=\"");
        out.append(relativeresourcesfolderpath);
        out.append(fn);
        out.append('.');
        out.append(fext);
        out.append("\"\n    class=\"attachment-full size-full\" alt=\"\"\n    srcset=\"");
        String prefix = "";
        for (Map.Entry<Integer, String> es : images.entrySet()) {
            out.append(prefix);
            out.append(relativeresourcesfolderpath);
            out.append(es.getValue());
            out.append(" ");
            out.append(Integer.toString(es.getKey()));
            out.append('w');
            prefix = ",\n    ";
        }
        out.append("\"\n    sizes=\"(max-width: ");
        out.append(widthstring);
        out.append("px) 100vw, ");
        out.append(widthstring);
        out.append("px\" />\n");
    }
}
