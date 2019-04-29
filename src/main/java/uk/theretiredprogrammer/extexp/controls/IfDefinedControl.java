/*
 * Copyright 2018-2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.controls;

import java.util.Optional;
import static uk.theretiredprogrammer.extexp.support.Command.Position.RIGHT;
import uk.theretiredprogrammer.extexp.support.Control;

/**
 * The IFDEFINED control class.
 * 
 * Tests if a name (temporary file or parameter) is defined and runs a choice of Commands based on the test result.
 * 
 * Requires up to three parameters:
 * 
 * 'If-defined' - the name to be tested.
 * 
 * 'then' - the command to run if name is defined (optional).
 * 
 * 'else' - the command to run if name is undefined (optional).
 * 
 * @author richard linsdale
 */
public class IfDefinedControl extends Control {

    private static final String IFIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_divide_down.png";

    @Override
    public String getWidgetImageName() {
        return IFIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "IF DEFINED";
    }
    
    @Override
    public String[] getPrimaryPinData(){
        return new String[] {"If-defined", "then", "else"};
    }
    
    @Override
    public ConnectedData[] getConnectedPinData(){
        return new ConnectedData[] {
            new ConnectedData("then", RIGHT),
            new ConnectedData("else", RIGHT)};
    }

    @Override
    protected void executecommand() {
        Optional<String> ifparam = getParameter("If-defined");
        if (isParamDefined(ifparam)) {
            getCommand("then").ifPresent(cmd -> exec(cmd));
        } else {
            getCommand("else").ifPresent(cmd -> exec(cmd));
        }
    }
}
