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
import java.io.StringWriter;
import javax.json.JsonObject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author richard
 */
public class BuildMarkDownFile extends Build {

    private final FileObject file;

    public BuildMarkDownFile(JsonObject jobj) throws IOException {
        String filename = jobj.getString("file", "");
        if (filename.equals("")) {
            throw new IOException("Missing file entry");
        }
        file = IoUtil.findFile(filename);
    }

    @Override
    public String getContentString(Usings parentusings) throws IOException {
        StringWriter toWriter = new StringWriter();
        FileObject mdtemplatefile = IoUtil.findFile("mdtemplate.xml");
        ProcessBuilder pb;
        if  (mdtemplatefile == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", mdtemplatefile.getPath());
        }
        pb.redirectInput(FileUtil.toFile(file));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = fromReader.readLine()) != null) {
                toWriter.append(line);
                toWriter.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException("Interrupt Exception caught");
        }
        return toWriter.toString();
    }
}
