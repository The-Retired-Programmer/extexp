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

import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;
import static uk.theretiredprogrammer.extexp.visualeditor.PNode.Position.LEFT;
import static uk.theretiredprogrammer.extexp.visualeditor.PNode.Position.RIGHT;

/**
 *
 * @author richard
 */
public class IfDefinedControl extends Control {

    private static final String IFIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_divide_down.png";

    @Override
    public String getWidgetImageName() {
        return IFIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "IF DEFINED";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new IfDefinedNode(scene, position);
    }

    private class IfDefinedNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public IfDefinedNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(IFIMAGENAME));
            attachPinWidget(new PPin(scene, "If Defined", IfDefinedControl.this.getParam("If-defined")));
            attachPinWidget(new PPin(scene, "then"));
            attachPinWidget(new PPin(scene, "else"));
            List<Entry<String, String>> extrapins = getFilteredParameters("If-defined", "then", "else");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }

        @Override
        public List<Widget> getConnections(PScene scene) {
            List<Widget> connections = new ArrayList<>();
            connections.addAll(processCommand(scene, "then", LEFT));
            connections.addAll(processCommand(scene, "else", RIGHT));
            return connections;
        }

        private List<Widget> processCommand(PScene scene, String name, Position position) {
            List<Widget> connections = Arrays.asList(getPin(name));
            Command command = getOptionalCommand(name);
            if (command == null) {
                return connections;
            }
            return scene.insert(command, connections, position);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        String ifparam = getLocalParameter("IF-DEFINED");
        if (isParamDefined(ifparam)) {
            Command thenpart = getOptionalCommand("then");
            if (thenpart != null) {
                thenpart.setParent(this);
                thenpart.execute(ee);
            }
        } else {
            Command elsepart = getOptionalCommand("else");
            if (elsepart != null) {
                elsepart.setParent(this);
                elsepart.execute(ee);
            }
        }
    }
}
