/*
 * Copyright 2018-2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.html;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOFactory;

/**
 * The IMAGESET executor class.
 *
 * Create an imageset Html tag. Scans the resources folder for all images
 * matching the named image and so are assumed to be all part of the imageset.
 * The output is writer to the named IOWriter.
 *
 * Requires parameters:
 *
 * 'image' - the image name for the 'core image'
 *
 * 'width' - the width of the image
 *
 * 'height' - the height of the image
 *
 * 'class' - the css class name to be added to the imageset tag
 *
 * 'to' - the name of the IOWriter to which the html tag is written.
 *
 * @author richard linsdale
 */
public class ImagesetExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "IMAGESET";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"image", "width", "height", "class", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (Writer output = IOFactory.createWriter(ee, this.getParameter("to"))) {
            String imagestring = getParameter("image").orElseThrow();
            int p = imagestring.lastIndexOf('.');
            String fn = imagestring.substring(0, p);
            String fext = imagestring.substring(p + 1);
            int fnsize = fn.length();
            String widthstring = getParameter("width").orElseThrow();
            String heightstring = getParameter("height").orElseThrow();
            String classstring = getParameter("class").orElseThrow();
            Map<Integer, String> images = new TreeMap<>();
            String relativeresourcesfolderpath = ee.paths.getRelativepath();
            for (FileObject child : ee.paths.getResourcesfolder().getChildren()) {
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
            output.append("<img width=\"");
            output.append(widthstring);
            output.append("\" height=\"");
            output.append(heightstring);
            output.append("\"\n    src=\"");
            output.append(relativeresourcesfolderpath);
            output.append(fn);
            output.append('.');
            output.append(fext);
            output.append("\"\n    class=\"");
            output.append(classstring);
            output.append("\" alt=\"\"\n    srcset=\"");
            String prefix = "";
            for (Map.Entry<Integer, String> es : images.entrySet()) {
                output.append(prefix);
                output.append(relativeresourcesfolderpath);
                output.append(es.getValue());
                output.append(" ");
                output.append(Integer.toString(es.getKey()));
                output.append('w');
                prefix = ",\n    ";
            }
            output.append("\"\n    sizes=\"(max-width: ");
            output.append(widthstring);
            output.append("px) 100vw, ");
            output.append(widthstring);
            output.append("px\" />\n");
        }
    }
}
