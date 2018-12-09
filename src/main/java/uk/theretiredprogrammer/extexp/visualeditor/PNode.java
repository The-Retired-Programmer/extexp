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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author richard
 */
public class PNode extends VMDNodeWidget {

    
    public static enum Position {
        LEFT, NORMAL, RIGHT
    }
    
    private final Position position;
    

    public PNode(final PScene scene, Position position){
        super(scene);
        this.position = position;
        //
        getActions().addAction(ActionFactory.createExtendedConnectAction(
                null,
                scene.getConnectionLayer(),
                new ExtexpConnectProvider(),
                MouseEvent.SHIFT_MASK
        )
        );
        getActions().addAction(ActionFactory.createMoveAction());
        getActions().addAction(new KeyboardMoveAction());
        getActions().addAction(ActionFactory.createSelectAction(new SelectProvider() {
            @Override
            public boolean isAimingAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }

            @Override
            public boolean isSelectionAllowed(Widget widget, Point localLocation, boolean invertSelection) {
                return true;
            }

            @Override
            public void select(Widget widget, Point localLocation, boolean invertSelection) {
                scene.setFocusedWidget(widget);
            }
        }));
    }

    private final class KeyboardMoveAction extends WidgetAction.Adapter {

        private final MoveProvider provider;

        private KeyboardMoveAction() {
            this.provider = ActionFactory.createDefaultMoveProvider();
        }

        @Override
        public WidgetAction.State keyPressed(Widget widget, WidgetAction.WidgetKeyEvent event) {
            Point originalSceneLocation = provider.getOriginalLocation(widget);
            int newY = originalSceneLocation.y;
            int newX = originalSceneLocation.x;
            switch (event.getKeyCode()) {
                case KeyEvent.VK_UP:
                    newY = newY - 20;
                    break;
                case KeyEvent.VK_DOWN:
                    newY = newY + 20;
                    break;
                case KeyEvent.VK_RIGHT:
                    newX = newX + 20;
                    break;
                case KeyEvent.VK_LEFT:
                    newX = newX - 20;
                    break;
                default:
                    break;
            }
            provider.movementStarted(widget);
            provider.setNewLocation(widget, new Point(newX, newY));
            return WidgetAction.State.CONSUMED;
        }

        @Override
        public WidgetAction.State keyReleased(Widget widget, WidgetAction.WidgetKeyEvent event) {
            provider.movementFinished(widget);
            return WidgetAction.State.REJECTED;
        }
    }

    private class ExtexpConnectProvider implements ConnectProvider {

        @Override
        public boolean isSourceWidget(Widget source) {
            return source != null && source instanceof PNode;
        }

        @Override
        public ConnectorState isTargetWidget(Widget src, Widget trg) {
            return src != trg && trg instanceof PNode
                    ? ConnectorState.ACCEPT : ConnectorState.REJECT;
        }

        @Override
        public boolean hasCustomTargetWidgetResolver(Scene arg0) {
            return false;
        }

        @Override
        public Widget resolveTargetWidget(Scene arg0, Point arg1) {
            return null;
        }

        @Override
        @SuppressWarnings("ResultOfObjectAllocationIgnored")
        public void createConnection(Widget source, Widget target) {
            new PConnection((PScene) getScene(), (PNode)source, (PNode)target);
        }
    }
    
    public List<Widget> getConnections(PScene scene) {
        return Arrays.asList(this);
    }
    
    public PPin getPin(String pinname) {
        for (Widget child : getChildren()) {
            if (child instanceof PPin && ((PPin) child).getPinName().equals(pinname)) {
                return ((PPin) child);
            }
        }
        return null;
    }
    
    public Position getPosition(){
        return position;
    }
}
