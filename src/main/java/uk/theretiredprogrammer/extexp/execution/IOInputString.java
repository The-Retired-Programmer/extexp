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
package uk.theretiredprogrammer.extexp.execution;

import java.io.IOException;

/**
 *
 * @author richard
 */
public class IOInputString extends IO<String> {
    
    public IOInputString(String parametervalue){
        super(parametervalue);
    }

    @Override
    String setup(IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        String content = tempfs.get(parametervalue);
        return content == null
                ? IoUtil.findFile(parametervalue, paths.getContentfolder(), paths.getSharedcontentfolder()).asText()
                : content;
    }
}
