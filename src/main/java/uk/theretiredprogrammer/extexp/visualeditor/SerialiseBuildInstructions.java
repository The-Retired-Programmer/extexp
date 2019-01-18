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
package uk.theretiredprogrammer.extexp.visualeditor;

import uk.theretiredprogrammer.extexp.execution.PScene;
import java.io.IOException;
import java.util.function.Function;
import javax.json.JsonObject;
import uk.theretiredprogrammer.extexp.execution.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.execution.PNode;
import uk.theretiredprogrammer.extexp.execution.impl.CommandSequence;
import uk.theretiredprogrammer.extexp.execution.impl.NamedCommandSequence;

/**
 *
 * @author richard
 */
public class SerialiseBuildInstructions {

    private Function<JsonObject, Boolean> outputfunction;

    public void setOutputFunction(Function<JsonObject, Boolean> outputfunction) {
        this.outputfunction = outputfunction;
    }

    // call in AWT to serialize scene
    public void serialize(PScene scene) {
    }

    // call in AWT to deserialize scene
    public void deserialize(PScene scene, ExecutionEnvironment env, int sequenceindex) throws IOException {
        NamedCommandSequence nseq = env.commandsequences.getNamedSequences().get(sequenceindex);
        putSequence(scene, nseq.name, nseq.commandsequence);
    }

    private void putSequence(PScene scene, String name, CommandSequence commandsequence) {
        PNode previous = scene.insertStart(name);
        scene.insertSequence(commandsequence, previous);
    }
}
