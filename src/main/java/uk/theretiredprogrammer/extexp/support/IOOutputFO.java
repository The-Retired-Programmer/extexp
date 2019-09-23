/*
 * Copyright 2019 richard linsdale.
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

/**
 * An IO Descriptor which will return a FileObject, which can be used to save
 * content
 *
 * The content is stored in one of:
 *
 * if name starts with '!' - a file in the memory based temporary filestore
 *
 * if name starts with '+' - appends to a file in the memory based temporary
 * filestore
 *
 * a file located in the output folder
 *
 * @author richard linsdale
 */
public class IOOutputFO extends IO<FileObject> {

    private String tempfilename;
    private boolean append;

    /**
     * Constructor
     *
     * @param ee the ExecutionEnvironment
     * @param name the name of the input source
     * @throws java.io.IOException if problem
     */
    public IOOutputFO(ExecutionEnvironment ee, Optional<String> name) throws IOException {
        super(ee, name);
    }

    @Override
    protected FileObject setup(String name, ExecutionEnvironment ee) throws IOException {
        if (name.startsWith("!")) {
            tempfilename = name.substring(1);
            append = false;
            return ee.tempfs.getFileObject(name); // TODO - ***************************************
        } else if (name.startsWith("+")) {
            tempfilename = name.substring(1);
            append = true;
            return ee.tempfs.getFileObject(name); // TODO - *************************************** 
        } else {
            return getOutputFO(ee.paths.getOutfolder(),name);
        }
    }

    @Override
    protected void drop(FileObject fo, ExecutionEnvironment ee) throws IOException {
    }
    
    private FileObject getOutputFO(FileObject todirectory, String name) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return todirectory.createData(name);
    }
}
