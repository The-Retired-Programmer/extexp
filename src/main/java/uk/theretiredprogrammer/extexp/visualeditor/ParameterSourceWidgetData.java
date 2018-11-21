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
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren.CategoryType;

/**
 *
 * @author richard
 */
public class ParameterSourceWidgetData extends WidgetData {
    
    public ParameterSourceWidgetData() {
        addPinDef(new PinDef("the parameter value"));
    }
    
    @Override
    public Image getWidgetImage() {
        return PARAMETERSOURCEIMAGE;
    }
    
    @Override
    public String getWidgetImageName() {
        return PARAMETERSOURCEIMAGENAME;
    }

    @Override
    public CategoryType getCategory() {
        return CategoryType.SOURCE;
    }

    @Override
    public String getDisplayName() {
        return "Parameter";
    }
}
