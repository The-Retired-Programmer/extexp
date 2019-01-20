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
package uk.theretiredprogrammer.extexp.execution.impl;

import uk.theretiredprogrammer.extexp.execution.IOPaths;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Command;
import uk.theretiredprogrammer.extexp.execution.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.execution.PNode;
import uk.theretiredprogrammer.extexp.execution.PNode.Position;
import uk.theretiredprogrammer.extexp.execution.PPin;
import uk.theretiredprogrammer.extexp.execution.PScene;

/**
 *
 * @author richard
 */
public class RunControl extends Control {

    private static final String RUNIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_right.png";

    @Override
    public String getWidgetImageName() {
        return RUNIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "RUN";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new RunNode(scene, position);
    }

    private class RunNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public RunNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(RUNIMAGENAME));
            attachPinWidget(new PPin(scene, "Run", RunControl.this.getParam("Run")));
            attachPinWidget(new PPin(scene, "path", RunControl.this.getParam("path"), PPin.OPTIONAL));
            attachPinWidget(new PPin(scene, "inputpath", RunControl.this.getParam("inputpath"), PPin.OPTIONAL));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Run", "path","inputpath");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        IOPaths newpaths;
        String pval = getOptionalLocalParameter("path", null);
        if (pval != null) {
            newpaths = ee.paths.updateBothPath(pval);
        } else {
             String ipval = getOptionalLocalParameter("inputpath", null);
             newpaths = ipval == null ? ee.paths : ee.paths.updatePath(ipval);
        }
        String runval = getLocalParameter("Run");
        ExecutionEnvironment newee = ee.cloneWithNewTFS(newpaths);
        for (Command child : ee.commandsequences.getSequence(runval)) {
            child.setParent(this);
            child.execute(newee);
        }
    }
}
