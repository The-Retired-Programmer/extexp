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
 */
/**
 *
 * @author Richard
 */
public abstract class WidgetData {

    public static final DataFlavor DATA_FLAVOR_WIDGETDATA = new DataFlavor(WidgetData.class, "widgetdata");

    private final List<PinDef> pinlist = new ArrayList<>();
    
    protected static final String FILESOURCEIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/script.png";
    protected static final Image FILESOURCEIMAGE = ImageUtilities.loadImage(FILESOURCEIMAGENAME); // NOI18N
    protected static final String FILETARGETIMAGENAME ="uk/theretiredprogrammer/extexp/visualeditor/disk.png";
    protected static final Image FILETARGETIMAGE = ImageUtilities.loadImage(FILETARGETIMAGENAME); // NOI18N
    protected static final String PARAMETERSOURCEIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/comment.png";
    protected static final Image PARAMETERSOURCEIMAGE = ImageUtilities.loadImage(PARAMETERSOURCEIMAGENAME); // N  OI18N
    
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

    protected final void addPinDef(PinDef pindef) {
        pinlist.add(pindef);
    }
}
