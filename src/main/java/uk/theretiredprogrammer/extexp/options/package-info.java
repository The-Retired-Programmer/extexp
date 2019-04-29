/*
 * Copyright 2019 richard linsdale.
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

/**
 <p>Registration of an additional Options Panel for Extexp</p>
 <p> ... more later ...</p>
 */
@OptionsPanelController.ContainerRegistration(id = "ExTexP", 
        position=800,
        categoryName = "#OptionsCategory_Name_ExTexP",
        iconBase = "uk/theretiredprogrammer/extexp/options/arrow_switch.png",
        keywords = "#OptionsCategory_Keywords_ExTexP", keywordsCategory = "ExTexP"
        )
@NbBundle.Messages(value = {"OptionsCategory_Name_ExTexP=ExTexP", "OptionsCategory_Keywords_ExTexP=Extendable Text Processor"})
package uk.theretiredprogrammer.extexp.options;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
