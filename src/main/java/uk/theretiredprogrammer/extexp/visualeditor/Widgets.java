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

import java.awt.Image;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.LayerWidget;

/**
 *
 * @author richard
 */
public class Widgets {

    public static Map<String, String> createWidget(ExtexpScene scene, WidgetData widgetdata, Point pos) {
        Map<String, String> res = new HashMap<>();
        String nodeid = createNode(scene, pos, widgetdata.getWidgetImage(),
                widgetdata.getDisplayName(), null, Arrays.asList());
        res.put(widgetdata.getDisplayName(), nodeid);
        for (PinDef pin : widgetdata.getPinDefList()) {
            res.put(pin.getName(), createPin(scene, nodeid, pin.getName(), null));
        }
        return res;
    }

    public static String createConnector(ExtexpScene scene, String sourcePinID, String targetPinID) {
        return createEdge(scene, sourcePinID, targetPinID);
    }

    // core creators
    private static int nodeID = 1;
    private static int pinID = 1;
    private static int edgeID = 1;

    private static String createNode(ExtexpScene scene, Point pos, Image image, String name, String type, List<Image> glyphs) {
        String nodeid = "node" + nodeID++;
        VMDNodeWidget widget = (VMDNodeWidget) scene.addNode(nodeid);
        widget.setPreferredLocation(pos);
        widget.setNodeProperties(image, name, type, glyphs);
        scene.addPin(nodeid, nodeid + VMDGraphScene.PIN_ID_DEFAULT_SUFFIX);
        return nodeid;
    }

    private static String createPin(ExtexpScene scene, String nodeID, String name, String type) {
        String pinid = "pin" + pinID++;
        ((VMDPinWidget) scene.addPin(nodeID, pinid)).setProperties(name, null);
        return pinid;
    }

    private static String createEdge(ExtexpScene scene, String sourcePinID, String targetPinID) {
        String edgeid = "edge" + edgeID++;
        scene.addEdge(edgeid);
        scene.setEdgeSource(edgeid, sourcePinID);
        scene.setEdgeTarget(edgeid, targetPinID);
        return edgeid;
    }
}
