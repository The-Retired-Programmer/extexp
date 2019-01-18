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
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.PNode;
import uk.theretiredprogrammer.extexp.execution.PNode.Position;

/**
 *
 * @author richard
 */
public class StartSequenceNode extends PNode {

    private static final String CONTROLIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/star.png";

    @SuppressWarnings("LeakingThisInConstructor")
    public StartSequenceNode(PScene scene, String name, Position position) {
        super(scene,position);
        setNodeName(name);
        setNodeImage(ImageUtilities.loadImage(CONTROLIMAGENAME));
        scene.getWidgetLayer().addChild(this);
    }
}
