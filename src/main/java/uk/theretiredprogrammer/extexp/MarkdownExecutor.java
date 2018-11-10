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
package uk.theretiredprogrammer.extexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.extexp.IODescriptor.IOREQUIREMENT.INPUTPATH;
import static uk.theretiredprogrammer.extexp.IODescriptor.IOREQUIREMENT.WRITER;

/**
 *
 * @author richard
 */
public class MarkdownExecutor extends Executor {

    private final IODescriptor<String> input = new IODescriptor<>("from", INPUTPATH);
    private final IODescriptor<String> template = new IODescriptor<>("template", INPUTPATH, true);
    private final IODescriptor<Writer> output = new IODescriptor<>("to", WRITER);
    
    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[] { input, template, output };
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        ProcessBuilder pb;
        String templatepath = template.getValue();
        if (templatepath == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", templatepath);
        }
        pb.redirectInput(new File(input.getValue()));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        Writer out = output.getValue();
        try (BufferedReader from = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = from.readLine()) != null) {
                out.append(line);
                out.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }
}
