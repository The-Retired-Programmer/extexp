/*
 * Copyright 2019 richard.
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
package uk.theretiredprogrammer.extexp.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Optional;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOFactory;

/**
 * The EXTERNAL executor class.
 *
 * Execute a OS process from the named IOReader to the named IOWriter.
 *
 * Requires four parameters:
 *
 * 'command' - the OS command
 *
 * 'parameters' - the parameters which are added to the command line
 *
 * 'from' - the name which generates the STDIN passed to the process
 *
 * 'to' - the name which accepts the STDOUT generated by the process
 *
 * @author richard linsdale
 */
public class ExternalExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "EXTERNAL";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"command", "parameters", "from", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        Optional<String> command = getParameter("command");
        if (!command.isPresent()) {
            throw new IOException("command parameter missing in External Executor");
        }
        Optional<String> parameters = getParameter("parameters");
        if (!parameters.isPresent()) {
            throw new IOException("parameters parameter missing in External Executor");
        }
        try ( BufferedReader reader = IOFactory.createReader(ee, getParameter("from"));
                Writer writer = IOFactory.createWriter(ee, getParameter("to"));
                PrintWriter bwriter = new PrintWriter(writer)) {
            ProcessExecutor pexec = new ProcessExecutor(command.get(), parameters.get());
            pexec.setDisplayName(command.get());
            pexec.setErrorLineFunction(s -> ee.errln(s));
            pexec.setInputLineFunction(() -> readLine(reader));
            pexec.setOutputLineFunction(s -> bwriter.println(s));
            pexec.execute();
        }
    }

    private String readLine(BufferedReader breader) {
        try {
            return breader.readLine();
        } catch (IOException ex) {
            ee.errln("Error when reading data: " + ex.getLocalizedMessage());
            ee.errln("Input terminated");
            return null;
        }
    }
}
