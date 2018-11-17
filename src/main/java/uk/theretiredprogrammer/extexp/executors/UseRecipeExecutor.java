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

import java.io.IOException;
import javax.json.JsonStructure;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.INPUTRECIPE;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.JSONSTRUCTUREFRAME;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.PARAMETERDESCRIPTOR;
import uk.theretiredprogrammer.extexp.execution.ParameterDescriptor;

/**
 *
 * @author richard
 */
public class UseRecipeExecutor extends Executor {

    private final IODescriptor<JsonStructure> recipe = new IODescriptor<>("recipe", INPUTRECIPE);
    private final IODescriptor<ParameterDescriptor> pd = new IODescriptor<>(PARAMETERDESCRIPTOR);
    private final IODescriptor<JsonStructure> jsonstructure = new IODescriptor<>(JSONSTRUCTUREFRAME);

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{recipe, pd, jsonstructure};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        jsonstructure.setValue(recipe.getValue());
    }
}
