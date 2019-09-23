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

/**
 * An IO Descriptor which will return an output path for given filename.
 *
 * The file will be located in the output folder.
 *
 * @author richard linsdale
 */
public class IOOutputPath extends IO<String> {

    /**
     * Constructor
     *
     * @param ee the ExecutionEnvironment
     * @param filename the filename
     * @throws IOException if problem
     */
    public IOOutputPath(ExecutionEnvironment ee, Optional<String> filename) throws IOException {
        super(ee, filename);
    }

    @Override
    protected String setup(String filename, ExecutionEnvironment ee) throws IOException {
        if (filename.startsWith("!")) {
            throw new IOException("Cannot use temporary filestore  for output path , please use a real filestore object");
        }
        return ee.paths.getOutPath() + "/" + filename;
    }

    @Override
    protected void drop(String path, ExecutionEnvironment ee) throws IOException {
    }
}
