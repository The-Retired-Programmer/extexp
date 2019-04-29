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
 * * An IO Descriptor which will return a writer, which can be used to save
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
     */
    public IOWriter(ExecutionEnvironment ee, Optional<String> name) {
        super(ee, name);
    }

    /**
     * Create the writer for use with this IO Writer.
     *
     * @param name the filename (optionally prefixed by '!' or '+')
     * @param ee the ExecutorEnvironment
     * @return the Writer
     */
    @Override
    protected Optional<Writer> setup(String name, ExecutionEnvironment ee) {
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
            try {
                writer = new BufferedWriter(new OutputStreamWriter(
                        getOutputStream(ee.paths.getOutfolder(), name))
                );
            } catch (IOException ex) {
                ee.errln("Error while opening a file writer: " + ex.getLocalizedMessage());
                return Optional.empty();
            }
        }
        return Optional.ofNullable(writer);
    }

    /**
     * Closing actions for this IO Writer.
     *
     * @param writer the IO writer
     * @param ee the ExecutorEnvironment
     * @return true if closing actions completed without errors
     */
    @Override
    protected boolean drop(Writer writer, ExecutionEnvironment ee) {
        try {
            writer.close();
        } catch (IOException ex) {
            ee.errln("Error closing a writer: " + ex.getLocalizedMessage());
            return false;
        }
        if (writer instanceof StringWriter) {
            Optional<String> previous = ee.tempfs.get(tempfilename);
            if (append && previous.isPresent()) {
                ee.tempfs.put(tempfilename, previous.get() + ((StringWriter) writer).toString());
                return true;
            }
            ee.tempfs.put(tempfilename, ((StringWriter) writer).toString());
        }
        return true;
    }

    private OutputStream getOutputStream(FileObject todirectory, String name) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return todirectory.createAndOpen(name);
    }
}
