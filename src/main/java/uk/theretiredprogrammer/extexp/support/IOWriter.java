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
package uk.theretiredprogrammer.extexp.support;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.local.IO;

/**
 * An IO Descriptor which will return a writer, which can be used to save
 * content
 *
 * The content is stored in one of:
 *
 * if name starts with '!' - a "file" in the memory based temporary filestore (a
 * StringWriter)
 *
 * if name starts with '+' - appends to a "file" in the memory based temporary
 * filestore (a StringWriter)
 *
 * a file located in the output folder(a FileWriter)
 *
 * @author richard linsdale
 */
public class IOWriter extends IO<Writer> {

    private String tempfilename;
    private boolean append;

    /**
     * Constructor
     *
     * @param ee the ExecutorEnvironment
     * @param name the filename (optionally prefixed by '!' or '+')
     * @throws IOException if problems
     */
    public IOWriter(ExecutionEnvironment ee, Optional<String> name) throws IOException {
        super(ee, name);
    }

    @Override
    protected Writer setup(String name, ExecutionEnvironment ee) throws IOException {
        Writer writer;
        if (name.startsWith("!")) {
            writer = new StringWriter();
            tempfilename = name.substring(1);
            append = false;
        } else if (name.startsWith("+")) {
            writer = new StringWriter();
            tempfilename = name.substring(1);
            append = true;
        } else {
            writer = new BufferedWriter(new OutputStreamWriter(getOutputStream(ee.paths.getOutfolder(), name)));
        }
        return writer;
    }

    @Override
    protected void drop(Writer writer, ExecutionEnvironment ee) throws IOException {
        writer.close();
        if (writer instanceof StringWriter) {
            if (append) {
                ee.tempfs.append(tempfilename, ((StringWriter) writer).toString());
            } else {
                ee.tempfs.write(tempfilename, ((StringWriter) writer).toString());
            }
        }
    }

    private OutputStream getOutputStream(FileObject todirectory, String name) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return todirectory.createAndOpen(name);
    }
}
