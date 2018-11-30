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
package uk.theretiredprogrammer.extexp.executors;

import java.awt.Image;
import uk.theretiredprogrammer.extexp.visualeditor.WidgetData;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class ImagesetExecutor extends Executor {

    @Override
    protected void executecommand() throws IOException {
        IOWriter output = new IOWriter(ee,this.getLocalParameter("to"));
        //
        String imagestring = getSubstitutedParameter("image");
        int p = imagestring.lastIndexOf('.');
        String fn = imagestring.substring(0, p);
        String fext = imagestring.substring(p + 1);
        int fnsize = fn.length();
        String widthstring = getSubstitutedParameter("width");
        String heightstring = getSubstitutedParameter("height");
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
        Writer out = output.get();
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
        //
        output.close();
    }

    @Override
    public WidgetData getWidgetData() {
        return new ImagesetExecutorWidgetData();
    }

    private class ImagesetExecutorWidgetData extends WidgetData {

        public ImagesetExecutorWidgetData() {
            addPinDef(new PinDef("description"));
            addPinDef(new PinDef("image"));
            addPinDef(new PinDef("width"));
            addPinDef(new PinDef("height"));
            addPinDef(new PinDef("to"));
        }

        @Override
        public Image getWidgetImage() {
            return EXECUTORIMAGE;
        }

        @Override
        public String getWidgetImageName() {
            return EXECUTORIMAGENAME;
        }

        @Override
        public CategoryChildren.CategoryType getCategoryType() {
            return CategoryChildren.CategoryType.EXECUTOR;
        }

        @Override
        public String getDisplayName() {
            return "Imageset";
        }
    }
}
