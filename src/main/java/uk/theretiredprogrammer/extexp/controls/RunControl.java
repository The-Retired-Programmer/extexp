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

import uk.theretiredprogrammer.extexp.support.Control;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.support.IOPaths;

/**
 * The RUN control class.
 *
 * Executes a command sequence with a new ExecutionEnvironment which includes:
 *
 * A new empty TemporaryFilestore
 *
 * A new IOPaths - if path is defined then both input, cache and output paths
 * are updated with additional folder in the paths (as defined by the path) - if
 * inputpath is defined then input path is updated with additional folder in the
 * path (as defined by the inputpath) - otherwise the IOPaths is not updated
 *
 * Requires two or more parameters:
 *
 * 'Run' - the name of the command sequence to execute
 *
 * 'path' - the command to run if name is defined (optional).
 *
 * OR
 *
 * 'inputpath' - the command to run if name is undefined (optional).
 *
 * Other parameters - to create an environment for the sequence execution
 *
 * @author richard linsdale
 */
public class RunControl extends Control {

    private static final String RUNIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_right.png";

    @Override
    public String getWidgetImageName() {
        return RUNIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "RUN";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"Run", "path", "inputpath"};
    }

    @Override
    protected void executecommand() {
        IOPaths newpaths = getLocalParameter("path")
                .map(p -> ee.paths.updateBothPath(p))
                .orElse(
                        getLocalParameter("inputpath")
                                .map(p -> ee.paths.updatePath(p))
                                .orElse(ee.paths)
                );
        ExecutionEnvironment newee = ee.cloneWithNewTFS(newpaths);
        execseq(ee.commandsequences.getSequence(getParameter("Run")), newee);
    }

}
