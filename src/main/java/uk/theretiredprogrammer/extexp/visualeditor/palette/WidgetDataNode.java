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
package uk.theretiredprogrammer.extexp.visualeditor.palette;

/*
 * ShapeNode.java
 *
 * Created on September 21, 2006, 9:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * To understand this class, see https://platform.netbeans.org/tutorials/nbm-nodesapi3.html
 */


import uk.theretiredprogrammer.extexp.visualeditor.WidgetData;
import java.awt.datatransfer.DataFlavor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Geertjan Wielenga
 */
public class WidgetDataNode extends AbstractNode {
    
    
    private final WidgetData item;
    
    /** Creates a new instance of InstrumentNode */
    public WidgetDataNode(WidgetData key) {
        super(Children.LEAF, Lookups.fixed( new Object[] {key} ) );
        this.item = key;
        this.
        setIconBaseWithExtension(key.getWidgetImageName());
        this.setDisplayName(key.getDisplayName());
    }
    
    public WidgetData getWidgetData() {
        return item;
    }
    
}
