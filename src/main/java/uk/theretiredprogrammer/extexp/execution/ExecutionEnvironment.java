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

/**
 *
 * @author richard
 */
public class ExecutionEnvironment {

    public final IOPaths paths;
    public final TemporaryFileStore tempfs;
    public final CommandSequenceStore commandsequences;
    
    public static final ExecutionEnvironment create(IOPaths paths, CommandSequenceStore commandsequences) {
        return new ExecutionEnvironment(paths, new TemporaryFileStore(), commandsequences);
    }
    
    public final ExecutionEnvironment clone(IOPaths paths) {
        return new ExecutionEnvironment(paths, this.tempfs, this.commandsequences);
    }
    
    public final ExecutionEnvironment cloneWithNewTFS(IOPaths paths) {
        return new ExecutionEnvironment(paths, new TemporaryFileStore(), this.commandsequences);
    }

    private ExecutionEnvironment(IOPaths paths, TemporaryFileStore tempfs, CommandSequenceStore commandsequences) {
        this.paths = paths;
        this.tempfs = tempfs;
        this.commandsequences = commandsequences;
    }
}
