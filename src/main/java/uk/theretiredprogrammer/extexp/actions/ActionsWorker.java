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
 * The Worker class which implement the actions of Build, Clean and Clean/Build.
 * 
 * @author richard linsdale
 */
public class ActionsWorker implements Runnable {

    private final boolean cleanrequired;
    private final boolean buildrequired;
    private final PProject project;

    /**
     * Constructor
     * 
     * @param project the project
     * @param cleanrequired true if clean required
     * @param buildrequired true if build required
     */
    public ActionsWorker(PProject project, boolean cleanrequired, boolean buildrequired) {
        this.cleanrequired = cleanrequired;
        this.buildrequired = buildrequired;
        this.project = project;
    }

    @Override
    public void run() {
        long start = currentTimeMillis();
        ProjectInformation projectinfo = ProjectUtils.getInformation(project);
        InputOutput io = IOProvider.getDefault().getIO("Assembly Builder for " + projectinfo.getName(), false);
        io.select();
        try (OutputWriter msg = io.getOut(); OutputWriter err = io.getErr()) {
            reset(msg, err);
            int errorcount = 0;
            if (cleanrequired) {
                errorcount+=cleanWorker(project.getProjectDirectory(), msg, err);
            }
            if (buildrequired) {
                errorcount+=buildWorker(project.getProjectDirectory(), msg, err);
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("BUILD COMPLETED " + (errorcount == 0 ? "" : "WITH ERRORS ") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void reset(OutputWriter msg, OutputWriter err) {
        try {
            msg.reset();
        } catch (IOException ex) {
            err.println("Unable to reset output window: " + ex.getLocalizedMessage());
        }
    }

    private int cleanWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) {
        msg.println("Cleaning...");
        int errorcount = 0;
        FileObject cachefolder = projectfolder.getFileObject("cache");
        if (cachefolder != null) {
            msg.println("   ...cache folder");
            for (FileObject f : cachefolder.getChildren()) {
                if (!deleteFile(f, err)) {
                    errorcount++;
                }
            }
        }
        FileObject outputfolder = projectfolder.getFileObject("output");
        if (outputfolder != null) {
            msg.println("   ...output folder");
            for (FileObject f : outputfolder.getChildren()) {
                if (!deleteFile(f, err)) {
                    errorcount++;
                }
            }
        }
        return errorcount;
    }

    private boolean deleteFile(FileObject fo, OutputWriter err) {
        try {
            fo.delete();
            return true;
        } catch (IOException ex) {
            err.println("Unable to delete " + fo.getNameExt() + ": " + ex.getLocalizedMessage());
            return false;
        }
    }

    private int buildWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) {
        msg.println("Building...");
        ExecutionEnvironment env = ExecutionEnvironment.create(projectfolder, msg, err);
        if (env == null) {
            return 1;
        }
        env.commandsequences.getSequence("MAIN").forEach((command) -> command.execute(env));
        return env.getErrorCount();
    }
}