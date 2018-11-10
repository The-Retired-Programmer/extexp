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
package uk.theretiredprogrammer.assemblybuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.assemblybuilder.IODescriptor.IOREQUIREMENT.INPUTPATH;
import static uk.theretiredprogrammer.assemblybuilder.IODescriptor.IOREQUIREMENT.OUTPUTPATH;

/**
 *
 * @author richard
 */
public class FopExecutor extends Executor {

    private final IODescriptor<String> foxsl = new IODescriptor<>("from", INPUTPATH);
    private final IODescriptor<String> pdf = new IODescriptor<>("to", OUTPUTPATH);

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{foxsl, pdf};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("/Users/richard/Applications/fop-2.3/fop/fop",
                "-fo", foxsl.getValue(),
                "-pdf", pdf.getValue());
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
    }
}
