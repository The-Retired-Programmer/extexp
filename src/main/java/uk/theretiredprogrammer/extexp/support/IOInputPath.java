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

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.extexp.support.local.IO;

/**
 * An IO Descriptor which will return an input path for given filename.
 *
 * The file will be located in the content folder or the shared content folder
 * if it exists in sthe tandard filestore (as defined in the current
 * {@link ExecutionEnvironment}s {@link IOPaths})
 *
 * If the name resolves to a temporary file object then this content will be
 * saved to a file in the cache folder and a path to that file returned by this
 * class.
 *
 * @author richard linsdale
 */
public class IOInputPath extends IO<String> {

    /**
     * Constructor
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the required filename
     * @throws java.io.IOException if problems
     */
    public IOInputPath(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        super(ee, parametervalue);
    }

    @Override
    protected String setup(String parametervalue, ExecutionEnvironment ee) throws IOException {
        Optional<String> fs = ee.tempfs.get(parametervalue);
        FileObject fo = fs.isPresent()
                ? stringToFile(ee.paths.getCachefolder(), parametervalue, fs.get(), ee)
                : findFile(ee, parametervalue, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
        return FileUtil.getFileDisplayName(fo);
    }

    @Override
    protected void drop(String path, ExecutionEnvironment ee) throws IOException {
    }

    /**
     * Get the filename (including ext) from the Input filepath
     *
     * @return the filename
     */
    public String getFileExt() {
        File f = new File(get());
        return f.getName();
    }

    /**
     * Get the path of the folder containing the file in the Input filepath
     *
     * @return the folder path
     */
    public File getFolder() {
        File f = new File(get());
        return f.getParentFile();
    }
}
