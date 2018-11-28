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
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;
import uk.theretiredprogrammer.extexp.execution.IOWriter;

/**
 *
 * @author richard
 */
public class MarkdownExecutor extends Executor {

    @Override
    public void execute(OutputWriter msg, OutputWriter err, IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        IOWriter output = new IOWriter(this.getLocalParameter("to", paths, tempfs));
        IOInputPath input = new IOInputPath(this.getLocalParameter("from", paths, tempfs));
        IOInputPath template = new IOInputPath(this.getOptionalLocalParameter("template", paths, tempfs));
        //
        ProcessBuilder pb;
        String templatepath = template.get(paths, tempfs);
        if (templatepath == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", templatepath);
        }
        pb.redirectInput(new File(input.get(paths, tempfs)));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        Writer out = output.get(paths, tempfs);
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
        //
        output.close(paths, tempfs);
        input.close(paths, tempfs);
        template.close(paths, tempfs);
    }
}
