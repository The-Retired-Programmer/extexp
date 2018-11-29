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
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.IOOutputPath;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class FopExecutor extends Executor {

    @Override
    public void execute(OutputWriter msg, OutputWriter err, IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        IOOutputPath pdf = new IOOutputPath(this.getLocalParameter("pdf", paths, tempfs));
        IOInputPath foxsl = new IOInputPath(this.getLocalParameter("fo-xsl", paths, tempfs));
        //
        ProcessBuilder pb = new ProcessBuilder("/Users/richard/Applications/fop-2.3/fop/fop",
                "-fo", foxsl.get(paths, tempfs),
                "-pdf", pdf.get(paths, tempfs));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = fromReader.readLine()) != null) {
                msg.println(line);
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        //
        pdf.close(paths, tempfs);
        foxsl.close(paths, tempfs);
    }

    @Override
    public WidgetData getWidgetData() {
        return new FopExecutorWidgetData();
    }

    private class FopExecutorWidgetData extends WidgetData {

        public FopExecutorWidgetData() {
            addPinDef(new PinDef("description"));
            addPinDef(new PinDef("fo-xsl"));
            addPinDef(new PinDef("pdf"));
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
            return "FOP";
        }
    }
}
