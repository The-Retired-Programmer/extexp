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
import java.util.List;
import java.util.Map;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOInputString;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;

/**
 *
 * @author richard
 */
public class SubstituteExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "SUBSTITUTE";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new SubstituteNode(scene, position);
    }

    private class SubstituteNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public SubstituteNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(EXECUTORIMAGENAME));
            attachPinWidget(new PPin(scene, "from", SubstituteExecutor.this.getParam("from")));
            attachPinWidget(new PPin(scene, "to", SubstituteExecutor.this.getParam("to")));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Do", "from", "to");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        IOWriter output = new IOWriter(ee, this.getLocalParameter("to"));
        IOInputString input = new IOInputString(ee, this.getLocalParameter("from"));
        //
        substitute(input.get(), (name) -> getOptionalSubstitutedParameter(name), output.get());
        //
        output.close();
        input.close();
    }
}
