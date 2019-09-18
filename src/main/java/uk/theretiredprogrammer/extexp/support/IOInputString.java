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

import java.io.IOException;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.local.IO;

/**
 * An IO Descriptor which will return the content for a given name.
 *
 * The name can reference content in any of:
 *
 * a "file" in the memory based temporary filestore.
 *
 * a file located in the content folder or the shared content folder
 *
 * a parameter
 *
 * @author richard linsdale
 */
public class IOInputString extends IO<String> {

    /**
     * Constructor
     *
     * @param ee the ExecutionEnvironment
     * @param name the name referring to the input object
     * @throws IOException if problem
     */
    public IOInputString(ExecutionEnvironment ee, Optional<String> name) throws IOException {
        super(ee, name);
    }

    @Override
    protected String setup(String name, ExecutionEnvironment ee) throws IOException {
        Optional<String> tempfs = ee.tempfs.get(name);
        return tempfs.isPresent() ? tempfs.get() : getFileOrParameterValue(name, ee);
    }

    @Override
    protected void drop(String name, ExecutionEnvironment ee) throws IOException {
    }

    private String getFileOrParameterValue(String name, ExecutionEnvironment ee) throws IOException {
        FileObject fo = findFile2(ee, name, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
        return fo != null ? fo.asText() : name;
    }

    private FileObject findFile2(ExecutionEnvironment ee, String filename, FileObject... fos) {
        for (FileObject fo : fos) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return file;
                }
            }
        }
        return null;
    }
}
