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

import uk.theretiredprogrammer.extexp.execution.impl.Command;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;
import uk.theretiredprogrammer.extexp.execution.PNode.Position;
import static uk.theretiredprogrammer.extexp.execution.PNode.Position.LEFT;
import static uk.theretiredprogrammer.extexp.execution.PNode.Position.NORMAL;
import static uk.theretiredprogrammer.extexp.execution.PNode.Position.RIGHT;
import uk.theretiredprogrammer.extexp.execution.impl.CommandSequence;
import uk.theretiredprogrammer.extexp.visualeditor.PConnection;
import uk.theretiredprogrammer.extexp.visualeditor.StartSequenceNode;

public class PScene extends VMDGraphScene {

    public static final DataFlavor DATA_FLAVOR_COMMAND = new DataFlavor(Command.class, "command");

    private final LayerWidget widgetlayer;
    private final LayerWidget connectionlayer;
    private final Router connectionRouter;
    private final Scene.SceneListener pslistener = new PSceneListener();

    public PScene() {
        this.addSceneListener(pslistener);
        widgetlayer = new LayerWidget(this);
        addChild(widgetlayer);
        connectionlayer = new LayerWidget(this);
        addChild(connectionlayer);
        connectionRouter = RouterFactory.createOrthogonalSearchRouter(widgetlayer, connectionlayer);
        setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {

            @Override
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                return ConnectorState.ACCEPT;
            }

            @Override
            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    Command command = (Command) transferable.getTransferData(DATA_FLAVOR_COMMAND);
                    command.createNode(PScene.this);
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        }));
    }

    public final PNode insertStart(String name) {
        return new StartSequenceNode(this, name, NORMAL);
    }

    public final List<Widget> insert(Command command, List<Widget> previous) {
        return insert(command, previous, NORMAL);
    }

    public final List<Widget> insert(Command command, List<Widget> previous, Position position) {
        PNode w = command.createNode(this, position);
        previous.stream().forEach(n -> new PConnection(this, n, w));
        return w.getConnections(this);
    }

    public final List<Widget> insertSequence(CommandSequence commandsequence, Widget previous) {
        return insertSequence(commandsequence, previous, NORMAL);
    }

    public final List<Widget> insertSequence(CommandSequence commandsequence, Widget previous, Position position) {
        List<Widget> connections = Arrays.asList(previous);
        for (Command command : commandsequence) {
            connections = insert(command, connections, position);
        }
        return connections;
    }

    public Router getRouter() {
        return connectionRouter;
    }

    public LayerWidget getConnectionLayer() {
        return connectionlayer;
    }

    public LayerWidget getWidgetLayer() {
        return widgetlayer;
    }

    public void layout() {
        SceneLayout layout = LayoutFactory.createDevolveWidgetLayout(widgetlayer,
                LayoutFactory.createAbsoluteLayout(), true);
        layout.invokeLayout();
    }

    private boolean layouthasoccurred = false;

    private class PSceneListener implements Scene.SceneListener {

        @Override
        public void sceneRepaint() {
        }

        @Override
        public void sceneValidating() {
        }

        @Override
        public void sceneValidated() {
            if (!layouthasoccurred) {
                List<PNode> widgets = widgetlayer.getChildren().stream()
                        .filter(w -> w instanceof PNode).map(w -> (PNode) w)
                        .collect(Collectors.toList());
                if (canLayoutBeAttempted(widgets)) {
                    try {
                        positionNodes(widgets, getMaxWidth(widgets));
                        layouthasoccurred = true;
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    private boolean canLayoutBeAttempted(List<PNode> nodes) {
        return nodes.stream().anyMatch((w) -> (w.getBounds() != null));
    }

    private int[] getMaxWidth(List<PNode> nodes) throws IOException {
        int[] max = new int[]{0, 0, 0};
        for (PNode node : nodes) {
            Rectangle b = node.getBounds();
            if (b == null) {
                throw new IOException("Bounds are null when positioning Node");
            }
            int width = b.width;
            switch (node.getPosition()) {
                case LEFT:
                    max[0] = max[0] > width ? max[0] : width;
                    break;
                case RIGHT:
                    max[2] = max[2] > width ? max[2] : width;
                    break;
                case NORMAL:
                    max[1] = max[1] > width ? max[1] : width;
            }
        }
        return max;
    }

    private int vgap = 70;
    private int hgap = 50;

    private void positionNodes(List<PNode> nodes, int[] maxwidth) throws IOException {
        int vpos = vgap;
        int vposbottom = vpos;
        int leftcentre = maxwidth[0] / 2 + hgap;
        int hcentre = maxwidth[0] == 0 ? hgap + maxwidth[1] / 2 : leftcentre * 2 + maxwidth[1] / 2;
        int rightcentre = hcentre + maxwidth[1] / 2 + hgap + maxwidth[2] / 2;
        for (PNode node : nodes) {
            Rectangle b = node.getBounds();
            if (b == null) {
                throw new IOException("Bounds are null when positioning Node");
            }
            int newvpos;
            switch (node.getPosition()) {
                case LEFT:
                    node.setPreferredLocation(new Point(leftcentre - b.width / 2, vposbottom));
                    newvpos = vposbottom + b.height + vgap;
                    vpos = vpos > newvpos ? vpos : newvpos;
                    break;
                case RIGHT:
                    node.setPreferredLocation(new Point(rightcentre - b.width / 2, vposbottom));
                    newvpos = vposbottom + b.height + vgap;
                    vpos = vpos > newvpos ? vpos : newvpos;
                    break;
                default:
                    node.setPreferredLocation(new Point(hcentre - b.width / 2, vpos));
                    vposbottom = vpos + b.height;
                    vpos = vposbottom + vgap;
            }
        }
    }
}
