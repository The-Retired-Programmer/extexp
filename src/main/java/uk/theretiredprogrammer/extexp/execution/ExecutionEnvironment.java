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

import uk.theretiredprogrammer.extexp.execution.impl.CommandSequenceStore;
import uk.theretiredprogrammer.extexp.execution.impl.IDGenerator;
import uk.theretiredprogrammer.extexp.execution.impl.IOPaths;
import uk.theretiredprogrammer.extexp.execution.impl.TemporaryFileStore;


/**
 *
 * @author richard
 */
public class ExecutionEnvironment {
    
    public final IOPaths paths;
    public final TemporaryFileStore tempfs;
    public final CommandSequenceStore commandsequences;
    public final IDGenerator idgenerator;
    
    public ExecutionEnvironment(IOPaths paths, CommandSequenceStore commandsequences) {
        this(paths, new TemporaryFileStore(), commandsequences, new IDGenerator());
    }
    
    public final ExecutionEnvironment clone(IOPaths paths) {
        return new ExecutionEnvironment(paths, this.tempfs, this.commandsequences, this.idgenerator);
    }
    
    public final ExecutionEnvironment cloneWithNewTFS(IOPaths paths) {
        return new ExecutionEnvironment(paths, new TemporaryFileStore(), this.commandsequences, this.idgenerator);
    }

    private ExecutionEnvironment(IOPaths paths, TemporaryFileStore tempfs, CommandSequenceStore commandsequences,
            IDGenerator idgenerator) {
        this.paths = paths;
        this.tempfs = tempfs;
        this.commandsequences = commandsequences;
        this.idgenerator = idgenerator;
    }
}
