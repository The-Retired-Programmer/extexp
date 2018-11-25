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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.Do;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.INPUTPATH;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.PARAMETERDESCRIPTOR;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.WRITER;
import uk.theretiredprogrammer.extexp.execution.ParameterDescriptor;

/**
 *
 * @author richard
 */
public class MarkdownAndSubstituteExecutor extends Executor {

    private final IODescriptor<String> input = new IODescriptor<>("from", INPUTPATH);
    private final IODescriptor<String> template = new IODescriptor<>("template", INPUTPATH).optional();
    private final IODescriptor<Writer> output = new IODescriptor<>("to", WRITER);
    private final IODescriptor<ParameterDescriptor> pd = new IODescriptor<>(PARAMETERDESCRIPTOR);
    
    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[] { input, template, output, pd};
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
        StringBuilder sb = new StringBuilder();
        try (BufferedReader from = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = from.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        Do.substitute(sb.toString(), pd.getValue().parameterExtractor, output.getValue());
    }
}