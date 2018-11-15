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

import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import java.io.IOException;
import javax.json.JsonStructure;
import org.openide.windows.OutputWriter;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.JSONPARAMSTRING;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.OUTPUTRECIPE;

/**
 *
 * @author richard
 */
public class CreateRecipeExecutor extends Executor {

    private final IODescriptor<JsonStructure> definition = new IODescriptor<>("definition", JSONPARAMSTRING);
    private final IODescriptor<JsonStructure> recipe = new IODescriptor<>("recipe", OUTPUTRECIPE);

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{definition, recipe};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        recipe.setValue(definition.getValue());
    }
}
