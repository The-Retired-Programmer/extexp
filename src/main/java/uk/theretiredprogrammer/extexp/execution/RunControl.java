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
package uk.theretiredprogrammer.extexp.execution;

import java.io.IOException;
import uk.theretiredprogrammer.extexp.execution.Command;
import uk.theretiredprogrammer.extexp.execution.CommandSequenceStore;
import uk.theretiredprogrammer.extexp.execution.Control;
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.ProcessCommand;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;

/**
 *
 * @author richard
 */
public class RunControl extends Control {

    @Override
    public void execute(IOPaths paths, CommandSequenceStore commandsequencestore,
            TemporaryFileStore tempfs) throws IOException {
        String pval = getOptionalLocalParameter("path", paths, tempfs);
        IOPaths newpaths = pval == null ? paths : paths.updatePath(pval);
        String runval = getLocalParameter("Run", paths, tempfs);
        TemporaryFileStore newTFS = new TemporaryFileStore();
        for (Command child : commandsequencestore.getSequence(runval)) {
            child.setParent(this);
            ProcessCommand.execute(newpaths, commandsequencestore, newTFS, child);
        }
    }
}
