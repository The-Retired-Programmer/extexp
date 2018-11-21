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
package uk.theretiredprogrammer.extexp.executors;

import java.awt.datatransfer.DataFlavor;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import java.io.IOException;
import java.io.Writer;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.INPUTSTRING;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.WRITER;

/**
 *
 * @author richard
 */
public class CopyExecutor extends Executor {
    
    private final IODescriptor<String> input = new IODescriptor<>("from", INPUTSTRING);
    private final IODescriptor<Writer> output = new IODescriptor<>("from", WRITER);
    

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{input, output};
    }
    
    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        output.getValue().write(input.getValue());
    }
}
