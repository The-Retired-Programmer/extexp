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

import uk.theretiredprogrammer.extexp.execution.Executor;
import java.io.IOException;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.IOInputString;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;
import uk.theretiredprogrammer.extexp.execution.IOWriter;

/**
 *
 * @author richard
 */
public class CopyExecutor extends Executor {

    @Override
    public void execute(OutputWriter msg, OutputWriter err, IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        IOWriter output = new IOWriter(this.getLocalParameter("to", paths, tempfs));
        IOInputString input = new IOInputString(this.getLocalParameter("from", paths, tempfs));
        //
        output.get(paths, tempfs).write(input.get(paths, tempfs));
        //
        output.close(paths, tempfs);
        input.close(paths, tempfs);
    }
}
