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

import uk.theretiredprogrammer.extexp.visualeditor.WidgetData;
import java.awt.Image;
import java.io.IOException;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class IfDefinedControl extends Control {

    @Override
    public void execute(IOPaths paths, CommandSequenceStore commandsequencestore, TemporaryFileStore tempfs) throws IOException {
        String ifparam = getLocalParameter("If-defined", paths, tempfs);
        if (isParamDefined(ifparam, paths, tempfs)) {
            Command thenpart = getOptionalCommand("then");
            if (thenpart != null) {
                thenpart.setParent(this);
                ProcessCommand.execute(paths, commandsequencestore, tempfs, thenpart);
            }
        } else {
            Command elsepart = getOptionalCommand("else");
            if (elsepart != null) {
                elsepart.setParent(this);
                ProcessCommand.execute(paths, commandsequencestore, tempfs, elsepart);
            }
        }
    }

    @Override
    public WidgetData getWidgetData() {
        return new IfDefinedWidgetData();
    }

    private class IfDefinedWidgetData extends WidgetData {

        public IfDefinedWidgetData() {
            addPinDef(new PinDef("description"));
            addPinDef(new PinDef("param"));
            addPinDef(new PinDef("then"));
            addPinDef(new PinDef("else"));
        }

        @Override
        public Image getWidgetImage() {
            return IFIMAGE;
        }

        @Override
        public String getWidgetImageName() {
            return IFIMAGENAME;
        }

        @Override
        public CategoryChildren.CategoryType getCategoryType() {
            return CategoryChildren.CategoryType.CONTROL;
        }

        @Override
        public String getDisplayName() {
            return "If Defined";
        }
    }

}
