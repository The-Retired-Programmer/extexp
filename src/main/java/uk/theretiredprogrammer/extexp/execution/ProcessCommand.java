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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 * @author richard
 */
public class ProcessCommand {

    public static void execute(IOPaths paths, CommandSequenceStore commandsequencestore,
            TemporaryFileStore tempfs, Command command) throws IOException {
        String description = command.getOptionalLocalParameter("description", paths, tempfs);
        if (description != null) {
            paths.getMsg().println("    ..." + description);
        }
        if (command instanceof Control) {
            ((Control)command).execute(paths, commandsequencestore, tempfs);
        } else if (command instanceof Executor) {
            ((Executor) command).execute(paths.getMsg(), paths.getErr(), paths, tempfs);
        }
    }
}
