/*
 * Copyright 2018-2019 richard linsdale.
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

import uk.theretiredprogrammer.extexp.support.Command.Position;

/**
 * A StartSequence Widget class
 * 
 * @author richard linsdale
 */
public class StartSequenceNode extends PNode {

    private static final String CONTROLIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/star.png";

    /**
     * Constructor
     * 
     * @param scene the visual editor scene
     * @param name the name of the widget
     * @param position the relative position of the widget
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public StartSequenceNode(PScene scene, String name, Position position) {
        super(scene,position,name,CONTROLIMAGENAME);
        scene.getWidgetLayer().addChild(this);
    }
}
