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
package uk.theretiredprogrammer.extexp.controls;

import org.openide.filesystems.FileObject;
import static uk.theretiredprogrammer.extexp.support.Command.Position.RIGHT;
import uk.theretiredprogrammer.extexp.support.Control;

/**
 * The FOR control class.
 *
 * Executes a command repeatatively for each file match the defined extension:
 *
 * Requires two or more parameters:
 *
 * 'For' - the file extension to match
 *
 * 'do' - the command to execute
 *
 * Other parameters - to create an environment for the command execution
 * 
 * Sets a special parameter __FILENAME__ as the filename (not ext) of the matched file
 *
 * @author richard linsdale
 */
public class ForControl extends Control {

    private static final String FORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_right.png";

    @Override
    public String getWidgetImageName() {
        return FORIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "FOR";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"For", "do"};
    }
    
    @Override
    public ConnectedData[] getConnectedPinData(){
        return new ConnectedData[] {
            new ConnectedData("do", RIGHT)};
    }

    @Override
    protected void executecommand() {
        String ext = this.getParameter("For").orElseThrow();
        for (FileObject f :  ee.paths.getContentfolder().getChildren()) {
            if ( f.getExt().equals(ext)) {
                putParameter("__FILENAME__", f.getName());
                getCommand("do").ifPresent(cmd -> exec(cmd));
            }
        }
    }
}
