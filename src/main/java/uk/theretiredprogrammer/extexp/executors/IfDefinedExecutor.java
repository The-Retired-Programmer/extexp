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

import uk.theretiredprogrammer.extexp.execution.ParameterDescriptor;
import java.io.IOException;
import javax.json.JsonStructure;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.JSONPARAMSTRING;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.JSONSTRUCTURESIMPLEFRAME;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.PARAMETERDESCRIPTOR;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.PARAMSTRING;

/**
 *
 * @author richard
 */
public class IfDefinedExecutor extends Executor {

    private final IODescriptor<String> param = new IODescriptor<>("param", PARAMSTRING);
    private final IODescriptor<JsonStructure> thenpart = new IODescriptor<>("then", JSONPARAMSTRING).optional();
    private final IODescriptor<JsonStructure> elsepart = new IODescriptor<>("else", JSONPARAMSTRING).optional();
    private final IODescriptor<ParameterDescriptor> params = new IODescriptor<>(PARAMETERDESCRIPTOR);
    private final IODescriptor<JsonStructure> selectedpart = new IODescriptor<>(JSONSTRUCTURESIMPLEFRAME);

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{param, params, thenpart, elsepart, selectedpart};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        selectedpart.setValue(
                params.getValue().parameterExtractor.apply(param.getValue()) == null
                ? elsepart.getValue()
                : thenpart.getValue()
        );
    }
}
