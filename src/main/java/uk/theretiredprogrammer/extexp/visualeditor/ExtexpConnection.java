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

import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import uk.theretiredprogrammer.extexp.execution.ExtexpPinWidget;
/**
 *
 * @author richard
 */
public class ExtexpConnection extends VMDConnectionWidget {

    @SuppressWarnings("LeakingThisInConstructor")
    public ExtexpConnection(final ExtexpScene scene, LayerWidget connectionlayer, ExtexpWidget source, ExtexpWidget target) {
        super(scene, scene.getRouter());
        setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        setTargetAnchor(AnchorFactory.createRectangularAnchor(target));
        setSourceAnchor(AnchorFactory.createRectangularAnchor(source));
        connectionlayer.addChild(this);
    }
    
    @SuppressWarnings("LeakingThisInConstructor")
    public ExtexpConnection(final ExtexpScene scene, LayerWidget connectionlayer, ExtexpPinWidget source, ExtexpWidget target) {
        super(scene, scene.getRouter());
        setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        setTargetAnchor(AnchorFactory.createRectangularAnchor(target));
        setSourceAnchor(AnchorFactory.createDirectionalAnchor(source, AnchorFactory.DirectionalAnchorKind.HORIZONTAL));
        connectionlayer.addChild(this);
    }
}