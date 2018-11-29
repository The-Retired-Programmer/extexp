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
import java.util.Map.Entry;
import java.util.function.Function;
import javax.json.JsonArray;
import javax.json.JsonObject;
import uk.theretiredprogrammer.extexp.execution.BuildFile;
import uk.theretiredprogrammer.extexp.execution.Command;
import uk.theretiredprogrammer.extexp.execution.CommandSequence;
import uk.theretiredprogrammer.extexp.execution.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.execution.NamedCommandSequence;
import uk.theretiredprogrammer.extexp.execution.ProcessCommand;

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

    // call in AWT to deserialize scene
    public void deserialize(ExtexpScene scene, ExecutionEnvironment env) throws IOException {
        col = 1;
        for (NamedCommandSequence ncs : env.commandsequences.getNamedSequences()) {
            putSerial(scene, col++, ncs.name, ncs.commandsequence);
        }
    }

    private static final int ROWSTEP = 120;
    private static final int COLSTEP = 200;

    private void putSerial(ExtexpScene scene, int col, String name, CommandSequence commandsequence) {
        int row = putWidget(scene, col, 0, new SequenceWidgetData(name));
        for (Command command : commandsequence ) {
            row = putWidget(scene, col, row, command.getWidgetData());
        }
    }

    private int putWidget(ExtexpScene scene, int col, int row, WidgetData widgetdata) {
        int x;
        int y;
//            int x = j.getInt("x", -1);
//            int y = j.getInt("y", -1);
//            if (x == -1) {
        x = COLSTEP * col;
//            }
//            if (y == -1) {
        y = ROWSTEP * row++;
//            }
        scene.insertWidget(widgetdata, new Point(x, y));
        return row;
    }
}
