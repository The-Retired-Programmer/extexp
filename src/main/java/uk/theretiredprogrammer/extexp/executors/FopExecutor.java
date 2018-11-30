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
import java.io.IOException;
import java.io.InputStreamReader;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.IOOutputPath;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class FopExecutor extends Executor {

    @Override
    protected void executecommand() throws IOException {
        IOOutputPath pdf = new IOOutputPath(ee, this.getLocalParameter("pdf"));
        IOInputPath foxsl = new IOInputPath(ee, this.getLocalParameter("fo-xsl"));
        //
        ProcessBuilder pb = new ProcessBuilder("/Users/richard/Applications/fop-2.3/fop/fop",
                "-fo", foxsl.get(),
                "-pdf", pdf.get());
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = fromReader.readLine()) != null) {
                ee.paths.getMsg().println(line);
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        //
        pdf.close();
        foxsl.close();
    }

    @Override
    public WidgetData getWidgetData() {
        return new FopExecutorWidgetData();
    }

    private class FopExecutorWidgetData extends WidgetData {

        private static final String EXECUTORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_switch.png";

        public FopExecutorWidgetData() {
            addPinDef("fo-xsl", new PinDef("fo-xsl", FopExecutor.this.getParam("fo-xsl")));
            addPinDef("pdf", new PinDef("pdf", FopExecutor.this.getParam("pdf")));
            addExtraPinDefs(FopExecutor.this.getParams(),"Do");
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
            return "FOP";
        }
    }
}
