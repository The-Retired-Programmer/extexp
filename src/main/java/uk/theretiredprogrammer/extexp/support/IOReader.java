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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.local.IO;

/**
 * An IO Descriptor which will return a reader, which can be used to obtain the content
 *
 * The name can reference content in any of:
 *
 * a "file" in the memory based temporary filestore  (a StringReader)
 *
 * a file located in the content folder or the shared content folder (a FileReader)
 *
 * a parameter (a StringReader)
 * 
 * @author richard linsdale
 */
public class IOReader extends IO<Reader> {

    /**
     * Constructor
     * 
     * @param ee the ExecutionEnvironment
     * @param name the name of the input source
     */
    public IOReader(ExecutionEnvironment ee, Optional<String> name) {
        super(ee, name);
    }

    /**
     * Create the Reader.
     * 
     * @param name the nameof the input source
     * @param ee the ExecutionEnvironment
     * @return the Reader
     */
    @Override
    protected Optional<Reader> setup(String name, ExecutionEnvironment ee) {
        Optional<String> fs = ee.tempfs.get(name);
        return fs.isPresent() ? Optional.of(new StringReader(fs.get())) : getfilereader(name, ee);
    }

    private Optional<Reader> getfilereader(String name, ExecutionEnvironment ee) {
        Optional<FileObject> fo = findFile(ee, name, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
        return fo.isPresent() ? createReader(fo.get(), ee) : Optional.empty();
    }

    private Optional<Reader> createReader(FileObject fo, ExecutionEnvironment ee) {
        try {
            return Optional.ofNullable(new BufferedReader(new InputStreamReader(fo.getInputStream())));
        } catch (FileNotFoundException ex) {
            ee.errln("Error creating a file reader: " + ex.getLocalizedMessage());
            return Optional.empty();
        }
    }

    /**
     * Closing actions for a reader IO object.
     * 
     * @param reader the Reader
     * @param ee the ExecutionEnvironment
     * @return true if closing action completed
     */
    @Override
    protected boolean drop(Reader reader, ExecutionEnvironment ee) {
        try {
            reader.close();
        } catch (IOException ex) {
            ee.errln("Error closing a reader: " + ex.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private Optional<FileObject> findFile(ExecutionEnvironment ee, String filename, FileObject... fos) {
        for (FileObject fo : fos) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return Optional.ofNullable(file);
                }
            }
        }
        return Optional.empty();
    }
}
