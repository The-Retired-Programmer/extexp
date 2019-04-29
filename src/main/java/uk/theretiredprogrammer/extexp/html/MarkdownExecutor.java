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
package uk.theretiredprogrammer.extexp.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import org.openide.util.NbPreferences;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputPath;
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
    protected void executecommand() {
        IOWriter output = new IOWriter(ee, getParameter("to"));
        if (!output.isOpen()) {
            return;
        }
        IOInputPath input = new IOInputPath(ee, getParameter("from"));
        if (!input.isOpen()) {
            return;
        }
        IOInputPath template = new IOInputPath(ee, getParameter("template"));
        String kramdownpath = NbPreferences.forModule(MarkDownPanel.class).get("kramdownPath", "kramdown");
        //
        ProcessBuilder pb;
        if (template.isOpen()) {
            pb = new ProcessBuilder(kramdownpath, "--no-auto-ids", "--template", template.get());
        } else {
            pb = new ProcessBuilder(kramdownpath, "--no-auto-ids");
        }
        pb.redirectInput(new File(input.get()));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        try {
            Process process = pb.start();
            Writer out = output.get();
            try (BufferedReader from = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = from.readLine()) != null) {
                    out.append(line);
                    out.append('\n');
                }
            }
            process.waitFor();
        } catch (InterruptedException | IOException ex) {
            ee.errln("Error Markdown: " + ex.getLocalizedMessage());
        }
        output.close();
        input.close();
        template.close();
    }
}
