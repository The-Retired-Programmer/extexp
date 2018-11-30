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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class MarkdownAndSubstituteExecutor extends Executor {

    @Override
    protected void executecommand() throws IOException {
        IOWriter output = new IOWriter(ee, this.getLocalParameter("to"));
        IOInputPath input = new IOInputPath(ee, this.getLocalParameter("from"));
        IOInputPath template = new IOInputPath(ee, this.getOptionalLocalParameter("template"));
        //
        ProcessBuilder pb;
        String templatepath = template.get();
        if (templatepath == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", templatepath);
        }
        pb.redirectInput(new File(input.get()));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader from = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = from.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        substitute(sb.toString(), (name) -> getOptionalSubstitutedParameter(name), output.get());
        //
        output.close();
        input.close();
        template.close();
    }

    @Override
    public WidgetData getWidgetData() {
        return new MarkdownAndSubstituteExecutorWidgetData();
    }

    private class MarkdownAndSubstituteExecutorWidgetData extends WidgetData {
        
        private static final String EXECUTORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_switch.png";

        public MarkdownAndSubstituteExecutorWidgetData() {
            addPinDef("from", new PinDef("from", MarkdownAndSubstituteExecutor.this.getParam("from")));
            addPinDef("template", new PinDef("template", MarkdownAndSubstituteExecutor.this.getParam("template"), PinDef.OPTIONAL));
            addPinDef("to", new PinDef("to", MarkdownAndSubstituteExecutor.this.getParam("to")));
            addExtraPinDefs(MarkdownAndSubstituteExecutor.this.getParams(),"Do");
        }

        @Override
        public Image getWidgetImage() {
            return ImageUtilities.loadImage(EXECUTORIMAGENAME);
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
            return "Markdown & Substitute";
        }
    }
}
