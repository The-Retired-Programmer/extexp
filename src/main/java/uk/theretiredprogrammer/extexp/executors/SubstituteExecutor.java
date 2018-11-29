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
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.Do;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.IOInputString;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class SubstituteExecutor extends Executor {

    @Override
    public void execute(OutputWriter msg, OutputWriter err, IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        IOWriter output = new IOWriter(this.getLocalParameter("to", paths, tempfs));
        IOInputString input = new IOInputString(this.getLocalParameter("from", paths, tempfs));
        //
        Do.substitute(input.get(paths, tempfs), (name) -> getOptionalSubstitutedParameter(name, paths, tempfs), output.get(paths, tempfs));
        //
        output.close(paths, tempfs);
        input.close(paths, tempfs);
    }

    @Override
    public WidgetData getWidgetData() {
        return new SubstituteExecutorWidgetData();
    }

    private class SubstituteExecutorWidgetData extends WidgetData {

        public SubstituteExecutorWidgetData() {
            addPinDef(new PinDef("description"));
            addPinDef(new PinDef("from"));
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
            return "Substitute";
        }
    }
}
