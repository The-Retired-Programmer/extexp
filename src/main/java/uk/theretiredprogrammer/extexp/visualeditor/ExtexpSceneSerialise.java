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

import java.awt.Point;
import java.io.IOException;
import java.util.function.Function;
import javax.json.JsonObject;
import uk.theretiredprogrammer.extexp.execution.Command;
import uk.theretiredprogrammer.extexp.execution.CommandSequence;
import uk.theretiredprogrammer.extexp.execution.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.execution.NamedCommandSequence;

/**
 *
 * @author richard
 */
public class ExtexpSceneSerialise {

    private Function<JsonObject, Boolean> outputfunction;

    public void setOutputFunction(Function<JsonObject, Boolean> outputfunction) {
        this.outputfunction = outputfunction;
    }

    // call in AWT to serialize scene
    public void serialize(ExtexpScene scene) {
    }

    private int col;
    private int row;

    // call in AWT to deserialize scene
    public void deserialize(ExtexpScene scene, ExecutionEnvironment env) throws IOException {
        col = 0;
        for (NamedCommandSequence ncs : env.commandsequences.getNamedSequences()) {
            putSequence(scene, ncs.name, ncs.commandsequence);
            col++;
        }
    }

    private static final int ROWSTEP = 120;
    private static final int COLSTEP = 200;

    private void putSequence(ExtexpScene scene, String name, CommandSequence commandsequence) {
        row = 0;
        ExtexpWidget previouswidget = putWidget(scene, new SequenceWidgetData(name));
        for (Command command : commandsequence ) {
            ExtexpWidget currentwidget = putWidget(scene, command.getWidgetData());
            scene.insertConnection(previouswidget, currentwidget);
            previouswidget = currentwidget;
        }
    }

    private ExtexpWidget putWidget(ExtexpScene scene, WidgetData widgetdata) {
        int x = COLSTEP * col;
        int y = ROWSTEP * row++;
        return scene.insertWidget(widgetdata, new Point(x, y));
    }
}
