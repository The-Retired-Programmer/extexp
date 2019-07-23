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
package uk.theretiredprogrammer.extexp.visualeditor;

import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.PProject;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;

/**
 * The Worker class which implement the actions of Open visual editor.
 *
 * @author richard linsdale
 */
public class VisualEditorWorker implements Runnable {

    private final PProject project;
    private final FileObject buildfile;

    /**
     * Constructor
     * 
     * @param project the Extexp project 
     * @param buildfile the build file
     */
    public VisualEditorWorker(PProject project, FileObject buildfile) {
        this.project = project;
        this.buildfile = buildfile;
    }

    @Override
    public void run() {
        long start = currentTimeMillis();
        ProjectInformation projectinfo = ProjectUtils.getInformation(project);
        InputOutput io = IOProvider.getDefault().getIO("Extexp Visual Editor - " + projectinfo.getName()+ " - "+ buildfile.getName(), false);
        io.select();
        try (OutputWriter msg = io.getOut(); OutputWriter err = io.getErr()) {
            reset(msg, err);
            int errorcount = 0;
            errorcount+=veWorker(project.getProjectDirectory(), buildfile, msg, err);
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("BUILD " + (errorcount == 0 ? "SUCCESSFUL" : "FAILED") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void reset(OutputWriter msg, OutputWriter err) {
        try {
            msg.reset();
        } catch (IOException ex) {
            err.println("Unable to reset output window: " + ex.getLocalizedMessage());
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private int veWorker(FileObject projectfolder, FileObject buildfile, OutputWriter msg, OutputWriter err) {
        msg.println("Building...");
        ExecutionEnvironment env = ExecutionEnvironment.create(projectfolder, buildfile, msg, err);
        if (env == null) {
            return 1;
        }
        PTC tc = new PTC();
        try {
            //tc.setSaveSource((jo) -> updateBuildFile(jo));
            tc.setDisplayName(project.getProjectDirectory().getName()+ " - "+ buildfile.getName());
            tc.open();
            tc.requestActive();
            tc.deserialise(env);
        } catch (Exception ex) {
            env.errln("Error: "+ ex.getLocalizedMessage());
        }
        return env.getErrorCount();
    }
}
