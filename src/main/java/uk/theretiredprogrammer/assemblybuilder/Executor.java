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
package uk.theretiredprogrammer.assemblybuilder;

import java.io.IOException;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 *
 */
public abstract class Executor {
    
    public abstract IODescriptor[] getIODescriptors();
    
    public abstract void execute(OutputWriter msg, OutputWriter err) throws IOException;
}
