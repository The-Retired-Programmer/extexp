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
public class RunWidgetData extends WidgetData {
    
    public RunWidgetData(String recipe) {
        addPinDef(new PinDef("description"));
        addPinDef(new PinDef(recipe));
    }
    
     public RunWidgetData() {
         this("*recipe*");
     }
    
    @Override
    public Image getWidgetImage() {
        return CONTROLIMAGE;
    }
    
    @Override
    public String getWidgetImageName() {
        return CONTROLIMAGENAME;
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.CONTROL;
    }

    @Override
    public String getDisplayName() {
        return "Run";
    }
}   