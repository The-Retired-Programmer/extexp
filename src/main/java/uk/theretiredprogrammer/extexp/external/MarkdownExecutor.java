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
package uk.theretiredprogrammer.extexp.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import org.openide.util.NbPreferences;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputPath;
import uk.theretiredprogrammer.extexp.support.IOReader;
import uk.theretiredprogrammer.extexp.support.IOWriter;

/**
 * The MARKDOWN executor class.
 *
 * Process a markdown file (a named IOInputPath) creating an equivalent html
 * segment file (a named IOWriter). Optionally the generated html segment may be
 * inserted into a defined template file (a named IOInputPath).
 *
 * Requires two/three parameters:
 *
 * 'from' - the name of the markdown file
 *
 * 'template' - the name of the template file (optional)
 *
 * 'to' - the name of the output file
 *
 * Note that the path to the markdown executable must be defined in the markdown
 * options panel before executing this command. The expected markdown processor
 * to be used is kramdown.
 *
 * @author richard linsdale
 */
public class MarkdownExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "MARKDOWN";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"from", "template", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (
                IOWriter output = new IOWriter(ee, getParameter("to"));
                IOReader input = new IOReader(ee, getParameter("from"));
                BufferedReader breader = new BufferedReader(input.get());
                PrintWriter bwriter = new PrintWriter(output.get())) {
            String kramdownpath = NbPreferences.forModule(MarkDownPanel.class).get("kramdownPath", "kramdown");
            ProcessExecutor pexec;
            try {
                try (IOInputPath template = new IOInputPath(ee, getParameter("template"))) {
                    pexec = new ProcessExecutor(kramdownpath, "--no-auto-ids", "--template", template.get());
                }
            } catch (IOException ex) {
                pexec = new ProcessExecutor(kramdownpath, "--no-auto-ids");
            }
            pexec.setDisplayName("MARKDOWN");
            pexec.setErrorLineFunction(s -> ee.errln(s));
            pexec.setInputLineFunction(() -> readLine(breader));
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
