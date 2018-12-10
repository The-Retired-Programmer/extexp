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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;

/**
 *
 * @author richard
 */
public class UseControl extends Control {

    private static final String USEIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_right.png";

    @Override
    public String getWidgetImageName() {
        return USEIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "USE";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new UseNode(scene, position);
    }

    private class UseNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public UseNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(USEIMAGENAME));
            attachPinWidget(new PPin(scene, "Use", UseControl.this.getParam("Use")));
            attachPinWidget(new PPin(scene, "path", UseControl.this.getParam("path"), PPin.OPTIONAL));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Use", "path");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        String pval = getOptionalLocalParameter("path");
        IOPaths newpaths = pval == null ? ee.paths : ee.paths.updatePath(pval);
        String useval = getLocalParameter("Use");
        ExecutionEnvironment newee = ee.clone(newpaths);
        for (Command child : ee.commandsequences.getSequence(useval)) {
            child.setParent(this);
            child.execute(newee);
        }
    }
}
