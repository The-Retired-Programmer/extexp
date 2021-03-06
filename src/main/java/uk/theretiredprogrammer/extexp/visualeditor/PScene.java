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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import uk.theretiredprogrammer.extexp.support.Command;
import uk.theretiredprogrammer.extexp.support.Command.Position;
import static uk.theretiredprogrammer.extexp.support.Command.Position.NORMAL;
import uk.theretiredprogrammer.extexp.support.CommandSequence;
import uk.theretiredprogrammer.extexp.support.Control;
import uk.theretiredprogrammer.extexp.support.Control.ConnectedData;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.support.Executor;

/**
 *  the Scene class for the visual editor
 * 
 * @author richard linsdale
 */
public class PScene extends VMDGraphScene {

    /**
     * the data flavor for any command widget
     */
    public static final DataFlavor DATA_FLAVOR_COMMAND = new DataFlavor(Command.class, "command");

    private final LayerWidget widgetlayer;
    private final LayerWidget connectionlayer;
    private final Router connectionRouter;
    private final Scene.SceneListener pslistener = new PSceneListener();
    private final ExecutionEnvironment ee;

    /**
     * Constructor
     * 
     * @param ee the ExecutionEnvironment
     */
    public PScene(ExecutionEnvironment ee) {
        this.ee = ee;
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
                    if (command instanceof Control) {
                        createControlNode((Control) command, NORMAL);
                    }
                    if (command instanceof Executor) {
                        createExecutorNode((Executor) command, NORMAL);
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ee.errln("Error - failed transferrable accept: " + ex.getLocalizedMessage());
                }
            }
        }));
    }

    /**
     * Insert the Start Command Sequence widget into the scene
     * 
     * @param name the name of the command sequence
     * @return the start sequence widget
     */
    public final PNode insertStart(String name) {
        return new StartSequenceNode(this, name, NORMAL);
    }

    /**
     * Insert a command widget into the scene
     * 
     * @param command the command to insert
     * @param previous the previous widget(s) which must be connected to this new command widget
     * @return a list of widgets
     */
    public final List<Widget> insert(Command command, List<Widget> previous) {
        return insert(command, previous, NORMAL);
    }

    /**
     * Insert a command widget into the scene with a relative position
     * 
     * @param command the command to insert
     * @param previous the previous widget(s) which must be connected to this new command widget
     * @param position the relative position of the widget
     * @return a list of widgets 
     */
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public final List<Widget> insert(Command command, List<Widget> previous, Position position) {
        PNode w;
        if (command instanceof Control) {
            w = createControlNode((Control) command, position);
            if (w == null) {
                return Arrays.asList();
            }
            previous.stream().forEach(p -> {
                String name = "Unknown";
                if ( p instanceof VMDNodeWidget) {
                    name = "Node " +((VMDNodeWidget)p).getNodeName();
                }
                if ( p instanceof VMDPinWidget) {
                    name = "Pin " +((VMDPinWidget)p).getPinName();
                }
                ee.paths.getMsg().println("Debug: inserting connection to " + command.getDisplayName()+" from " + name);
                new PConnection(this, p, w);
                    });
            return getConnectionPoints((Control) command, w);
        }
        if (command instanceof Executor) {
            w = createExecutorNode((Executor) command, position);
            if (w == null) {
                return Arrays.asList();
            }
            previous.stream().forEach(p -> {
                String name = "Unknown";
                if ( p instanceof VMDNodeWidget) {
                    name = "Node " +((VMDNodeWidget)p).getNodeName();
                }
                if ( p instanceof VMDPinWidget) {
                    name = "Pin " +((VMDPinWidget)p).getPinName();
                }
                ee.paths.getMsg().println("Debug: inserting connection to " + command.getDisplayName()+" from " + name);
                new PConnection(this, p, w);
                    });
            return Arrays.asList(w);
        }
        return Arrays.asList();
    }

    private PNode createExecutorNode(Executor executor, Position position) {
        String[] ppnames = executor.getPrimaryPinData();
        int l = ppnames.length;
        PNode pnode = new PNode(this, position, executor.getDisplayName(), executor.getWidgetImageName());
        for (String name : ppnames) {
            executor.getParameterText(name).ifPresent(
                    (p) -> pnode.attachPinWidget(new PPin(this, name, p))
            );
        }
        String[] filternames = Arrays.copyOf(ppnames, l + 1);
        filternames[l] = "Do";
        List<Map.Entry<String, String>> extrapins = executor.getFilteredParameters(filternames);
        if (!extrapins.isEmpty()) {
            pnode.attachPinWidget(new PPin(this));
            extrapins.forEach((e) -> pnode.attachPinWidget(new PPin(this, e.getKey(), e.getValue())));
        }
        this.getWidgetLayer().addChild(pnode);
        return pnode;
    }

    private PNode createControlNode(Control control, Position position) {
        String[] ppnames = control.getPrimaryPinData();
        int l = ppnames.length;
        PNode pnode = new PNode(this, position, control.getDisplayName(), control.getWidgetImageName());
        for (String name : ppnames) {
            Optional<String> ptext = control.getParameterText(name);
            if (ptext.isPresent()) {
                pnode.attachPinWidget(new PPin(this, name, ptext.get()));
            } else {
                pnode.attachPinWidget(new PPin(this, name));
            }
        }
        List<Map.Entry<String, String>> extrapins = control.getFilteredParameters(ppnames);
        if (!extrapins.isEmpty()) {
            pnode.attachPinWidget(new PPin(this));
            extrapins.forEach((e) -> pnode.attachPinWidget(new PPin(this, e.getKey(), e.getValue())));
        }
        this.getWidgetLayer().addChild(pnode);
        return pnode;
    }

    private List<Widget> getConnectionPoints(Control control, PNode n) {
        ConnectedData[] cdata = control.getConnectedPinData();
        if (cdata.length == 0) {
            return Arrays.asList(n);
        }
        List<Widget> connections = new ArrayList<>();
        for (ConnectedData cd : cdata) {
            connections.addAll(processCommand(this, control, n, cd.name, cd.position));
        }
        return connections;
    }

    private List<Widget> processCommand(PScene scene, Control control, PNode n, String name, Position position) {
        PPin pin = n.getPin(name);
        Optional<Command> command = control.getCommand(name);
        return command.isPresent()? scene.insert(command.get(), Arrays.asList(pin), position): Arrays.asList(pin);
    }

    /**
     *  Insert a sequence of commands into the scene
     * 
     * @param commandsequence the command sequence to insert
     * @param previous the previous widget(s) which must be connected to this new command widget
     * @return a list of widgets 
     */
    public final List<Widget> insertSequence(CommandSequence commandsequence, Widget previous) {
        return insertSequence(commandsequence, previous, NORMAL);
    }

    /**
     *  Insert a sequence of commands into the scene
     * 
     * @param commandsequence the command sequence to insert
     * @param previous the previous widget(s) which must be connected to this new command widget
     * @param position the relative position of the widget
     * @return a list of widgets 
     */
    public final List<Widget> insertSequence(CommandSequence commandsequence, Widget previous, Position position) {
        List<Widget> connections = Arrays.asList(previous);
        for (Command command : commandsequence) {
            connections = insert(command, connections, position);
        }
        return connections;
    }

    /**
     * Get the current connection router
     * 
     * @return the router 
     */
    public Router getRouter() {
        return connectionRouter;
    }

    /**
     * Get the connection layer
     * 
     * @return the connection layer
     */
    public LayerWidget getConnectionLayer() {
        return connectionlayer;
    }

    /**
     * Get the widget layer
     * 
     * @return the widget layer 
     */
    public LayerWidget getWidgetLayer() {
        return widgetlayer;
    }

    /**
     * Create the layout
     */
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
                        ee.errln("Error detected during scene validation: " + ex.getLocalizedMessage());
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

//    private int vgap = 70;
//    private int hgap = 50;
//
//    private void positionNodes(List<PNode> nodes, int[] maxwidth) throws IOException {
//        int vpos = vgap;
//        int leftcentre = maxwidth[0] / 2 + hgap;
//        int hcentre = maxwidth[0] == 0 ? hgap + maxwidth[1] / 2 : leftcentre * 2 + maxwidth[1] / 2;
//        int rightcentre = hcentre + maxwidth[1] / 2 + hgap + maxwidth[2] / 2;
//        for (PNode node : nodes) {
//            Rectangle b = node.getBounds();
//            if (b == null) {
//                throw new IOException("Bounds are null when positioning Node");
//            }
//            switch (node.getPosition()) {
//                case LEFT:
//                    node.setPreferredLocation(new Point(leftcentre - b.width / 2, vpos));
//                    break;
//                case RIGHT:
//                    node.setPreferredLocation(new Point(rightcentre - b.width / 2, vpos));
//                    break;
//                default:
//                    node.setPreferredLocation(new Point(hcentre - b.width / 2, vpos));
//            }
//            vpos += b.height + vgap;
//        }
//    }
    
    private int vgap = 70;
    private int hgap = 20;

    private void positionNodes(List<PNode> nodes, int[] maxwidth) throws IOException {
        int vpos = vgap;
        int leftcentre = maxwidth[0] / 2 + hgap;
        int hcentre = maxwidth[0] == 0 ? hgap + maxwidth[1] / 2 : leftcentre * 2 + maxwidth[1] / 2;
//        int rightcentre = hcentre + maxwidth[1] / 2 + hgap + maxwidth[2] / 2;
        for (PNode node : nodes) {
            Rectangle b = node.getBounds();
            if (b == null) {
                throw new IOException("Bounds are null when positioning Node");
            }
            switch (node.getPosition()) {
                case LEFT:
                    node.setPreferredLocation(new Point(hgap, vpos));
                    break;
                case RIGHT:
                    node.setPreferredLocation(new Point(maxwidth[1]+2*hgap, vpos));
                    break;
                default:
                    node.setPreferredLocation(new Point(hgap, vpos));
            }
            vpos += b.height + vgap;
        }
    }
}
