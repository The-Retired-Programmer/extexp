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
package uk.theretiredprogrammer.extexp.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.filesystems.FileObject;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.extexp.PProject;

/**
 * The Build Action class
 * 
 * @author richard linsdale
 */
public class ActionBuild extends AbstractAction {

    private final PProject project;
    private final FileObject buildfile;

    /**
     * Constructor
     * 
     * @param project the Extexp project
     * @param buildfile the build file
     */
    public ActionBuild(PProject project, FileObject buildfile) {
        super("Build");
        this.project = project;
        this.buildfile = buildfile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new RequestProcessor(ActionBuild.class).post(new ActionsWorker(project, buildfile, false, true));
    }
}
