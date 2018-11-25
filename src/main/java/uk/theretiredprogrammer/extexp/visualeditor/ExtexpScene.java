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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import static uk.theretiredprogrammer.extexp.visualeditor.WidgetData.DATA_FLAVOR_WIDGETDATA;

public class ExtexpScene extends VMDGraphScene {

    private Map<String, WidgetData> nodetodata = new HashMap<>();
    private Map<String, Map<String, String>> nodetonamesandids = new HashMap<>();
    private final LayerWidget layerwidget;
    private final LayerWidget connectionlayerwidget;
    /**
     * Creates a new instance of MyScene
     */
            
    public ExtexpScene() {
        layerwidget = new LayerWidget(this);
        addChild(layerwidget);
        connectionlayerwidget = new LayerWidget(this);
        addChild(connectionlayerwidget);
        //
        setKeyEventProcessingType(EventProcessingType.FOCUSED_WIDGET_AND_ITS_CHILDREN);
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {

            @Override
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                    return ConnectorState.ACCEPT;
            }

            @Override
            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    WidgetData widgetdata = (WidgetData) transferable.getTransferData(DATA_FLAVOR_WIDGETDATA);
                    insertWidget(widgetdata, point);
//                    Map<String, String> namesandIDs = Widgets.createWidget(layerwidget, widgetdata, point);
//                    String nodeid = getNodeId(namesandIDs);
//                    nodetodata.put(nodeid, widgetdata);
//                    nodetonamesandids.put(nodeid, namesandIDs);
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        }));
    }
    
    public void insertWidget(WidgetData widgetdata, Point point) {
        ExtexpWidget w = new ExtexpWidget(ExtexpScene.this, widgetdata, point, connectionlayerwidget);
        layerwidget.addChild(w);
    }
    
    private String getNodeId(Map<String, String> namesandIDs) {
        for(String value : namesandIDs.values()){
            if (value.startsWith("node")) {
                return value;
            }
        }
        return null;
    }
}
