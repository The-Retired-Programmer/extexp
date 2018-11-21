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
import org.netbeans.api.visual.widget.Widget;
import static uk.theretiredprogrammer.extexp.visualeditor.WidgetData.DATA_FLAVOR_WIDGETDATA;

public class ExtexpScene extends VMDGraphScene {


    private Map<String, WidgetData> nodemap = new HashMap<>();

    /**
     * Creates a new instance of MyScene
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ExtexpScene() {
        getActions().addAction(ActionFactory.createAcceptAction(new AcceptProvider() {

            @Override
            public ConnectorState isAcceptable(Widget widget, Point point, Transferable transferable) {
                try {
                    WidgetData i = (WidgetData) transferable.getTransferData(DATA_FLAVOR_WIDGETDATA);
                    return ConnectorState.ACCEPT;
                } catch (IOException | UnsupportedFlavorException ex) {
                    return ConnectorState.REJECT;
                }
            }

            @Override
            public void accept(Widget widget, Point point, Transferable transferable) {
                try {
                    WidgetData widgetdata = (WidgetData) transferable.getTransferData(DATA_FLAVOR_WIDGETDATA);
                    String copynode[] = Widgets.createWidget(ExtexpScene.this, widgetdata, point);
                    nodemap.put(copynode[0], widgetdata);
                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
        }));
//        String copynode[] = Widgets.createCopyExecutor(this, 100, 100);
//        String param1[] = Widgets.createParameterSource(this, 300, 100, "create recipe pagebuilder");
//        String file1[] = Widgets.createFileSource(this, 300, 400, "template.xml");
//        //
//        Widgets.createConnector(this, param1[1], copynode[1]);
//        Widgets.createConnector(this, file1[1], copynode[2]);
    }
}
