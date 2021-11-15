/*
 * Copyright 2019 -2021 richard linsdale.
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

import static uk.theretiredprogrammer.extexp.support.Command.Position.RIGHT;
import uk.theretiredprogrammer.extexp.support.Control;

/**
 * The FORLIST control class.
 *
 * Executes a command repeatatively for each value in a list:
 *
 * Requires two or more parameters:
 *
 * 'ForList' - the list of values
 *
 * 'do' - the command to execute
 *
 * Other parameters - to create an environment for the command execution
 *
 * Sets a special parameter __ELEMENT__ as the list element for the iteration
 *
 * @author richard linsdale
 */
public class ForListControl extends Control {

    private static final String FORLISTIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_right.png";

    @Override
    public String getWidgetImageName() {
        return FORLISTIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "FORLIST";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"ForList", "do"};
    }

    @Override
    public ConnectedData[] getConnectedPinData() {
        return new ConnectedData[]{
            new ConnectedData("do", RIGHT)};
    }

    @Override
    protected void executecommand() {
        String list = this.getParameter("ForList").orElseThrow();
        for (String element : list.split("\\|")) {
            putParameter("__ELEMENT__", element);
            getCommand("do").ifPresent(cmd -> exec(cmd));
        }
    }
}
