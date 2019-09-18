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

import java.io.IOException;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.local.IO;

/**
 * An IO Descriptor which will return a FileObject, which can be used to obtain
 * the content
 *
 * The name can reference content in any of:
 *
 * a "file" in the memory based temporary filestore (a StringReader)
 *
 * a file located in the content folder or the shared content folder (a
 * FileReader)
 *
 * a parameter (a StringReader)
 *
 * @author richard linsdale
 */
public class IOInputFO extends IO<FileObject> {

    /**
     * Constructor
     *
     * @param ee the ExecutionEnvironment
     * @param name the name of the input source
     * @throws java.io.IOException if problem
     */
    public IOInputFO(ExecutionEnvironment ee, Optional<String> name) throws IOException {
        super(ee, name);
    }

    @Override
    protected FileObject setup(String name, ExecutionEnvironment ee) throws IOException {
        Optional<String> fs = ee.tempfs.get(name);
        return fs.isPresent() ? stringToFile(ee.paths.getCachefolder(), name, fs.get(), ee)
                : findFile(ee, name, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
    }

    @Override
    protected void drop(FileObject fo, ExecutionEnvironment ee) throws IOException {
    }
}
