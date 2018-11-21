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
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren.CategoryType;

/*
 * Item.java
 *
 * Created on September 21, 2006, 9:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * To understand this class, see https://platform.netbeans.org/tutorials/nbm-nodesapi3.html
 */
/**
 *
 * @author Geertjan Wielenga
 */
public abstract class WidgetData {

    public static final DataFlavor DATA_FLAVOR_WIDGETDATA = new DataFlavor(WidgetData.class, "widgetdata");

    private final List<PinDef> pinlist = new ArrayList<>();
    
    static final String EXECUTORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_switch.png";
    static final Image EXECUTORIMAGE = ImageUtilities.loadImage(EXECUTORIMAGENAME); // NOI18N
    static final String CONTROLIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/star.png";
    static final Image CONTROLIMAGE = ImageUtilities.loadImage(CONTROLIMAGENAME); // NOI18N
    static final String FILESOURCEIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/script.png";
    static final Image FILESOURCEIMAGE = ImageUtilities.loadImage(FILESOURCEIMAGENAME); // NOI18N
    static final String FILETARGETIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/disk.png";
    static final Image FILETARGETIMAGE = ImageUtilities.loadImage(FILETARGETIMAGENAME); // NOI18N
    static final String PARAMETERSOURCEIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/comment.png";
    static final Image PARAMETERSOURCEIMAGE = ImageUtilities.loadImage(PARAMETERSOURCEIMAGENAME); // N  OI18N
    static final String IFIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/arrow_divide.png";
    static final Image IFIMAGE = ImageUtilities.loadImage(IFIMAGENAME); // NOI18N

    public abstract CategoryType getCategoryType();

    public abstract Image getWidgetImage();
    
    public abstract String getWidgetImageName();

    public abstract String getDisplayName();

    public DataFlavor getDataFlavor() {
        return DATA_FLAVOR_WIDGETDATA;
    }

    public List<PinDef> getPinDefList() {
        return pinlist;
    }

    final void addPinDef(PinDef pindef) {
        pinlist.add(pindef);
    }
}
