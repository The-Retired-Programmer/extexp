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
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.layout.LayoutFactory;
import static org.netbeans.api.visual.layout.LayoutFactory.SerialAlignment.CENTER;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.widget.EventProcessingType;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import uk.theretiredprogrammer.extexp.execution.ExtexpPinWidget;
import static uk.theretiredprogrammer.extexp.visualeditor.WidgetData.DATA_FLAVOR_WIDGETDATA;

public class ExtexpScene extends VMDGraphScene {

    private final LayerWidget layerwidget;
    private final LayerWidget connectionlayerwidget;
    private final Router connectionRouter;
    /**
     * Creates a new instance of MyScene
     */
    public ExtexpScene() {
        layerwidget = new LayerWidget(this);
        addChild(layerwidget);
        connectionlayerwidget = new LayerWidget(this);
        addChild(connectionlayerwidget);
        connectionRouter = RouterFactory.createOrthogonalSearchRouter(layerwidget, connectionlayerwidget);
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
                    insertWidget(widgetdata);
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        }));
    }

    public WidgetStartAndEnd insertWidget(WidgetData widgetdata) {
        return ExtexpWidget.create(this, layerwidget, widgetdata, connectionlayerwidget);
    }

    public ExtexpConnection insertConnection(ExtexpWidget source, ExtexpWidget target) {
        return new ExtexpConnection(this, connectionlayerwidget, source, target);
    }
    
    public ExtexpConnection connectPinToWidget(ExtexpPinWidget source, ExtexpWidget target) {
        return new ExtexpConnection(this, connectionlayerwidget, source, target);
    }
    
    public Router getRouter() {
        return connectionRouter;
    }
    
    public void layout(){
        SceneLayout  devolveLayout = LayoutFactory.createDevolveWidgetLayout (layerwidget, LayoutFactory.createVerticalFlowLayout(CENTER,40), true);
        devolveLayout.invokeLayout();
    }
}
