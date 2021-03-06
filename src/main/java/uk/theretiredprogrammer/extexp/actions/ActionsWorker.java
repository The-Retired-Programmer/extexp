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
import uk.theretiredprogrammer.extexp.support.CommandFactory;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;

/**
 * The Worker class which implement the actions of Build, Clean and Clean/Build.
 *
 * @author richard linsdale
 */
public class ActionsWorker implements Runnable {

    private final boolean debugrequired;
    private final PProject project;
    private final FileObject buildfile;

    /**
     * Constructor
     *
     * @param project the project
     * @param buildfile the build file
     * @param debugrequired true if debug required
     */
    public ActionsWorker(PProject project, FileObject buildfile, boolean debugrequired) {
        this.debugrequired = debugrequired;
        this.project = project;
        this.buildfile = buildfile;
    }

    @Override
    public void run() {
        boolean errflag = false;
        long start = currentTimeMillis();
        ProjectInformation projectinfo = ProjectUtils.getInformation(project);
        InputOutput io = IOProvider.getDefault().getIO("Extexp - " + projectinfo.getName() + " - " + buildfile.getName(), false);
        io.select();
        try ( OutputWriter msg = io.getOut();  OutputWriter err = io.getErr()) {
            try {
                reset(msg, err);
                int errorcount = 0;
                CommandFactory.init();
                ExecutionEnvironment env = new ExecutionEnvironment(project.getProjectDirectory(), buildfile, msg, err, debugrequired);
                errorcount += cleanWorker(env);
                errorcount += buildWorker(env);
                if (errorcount > 0) {
                    errflag = true;
                }
            } catch (IOException ex) {
                errflag = true;
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("BUILD COMPLETED" + (errflag ? " WITH ERRORS" : "") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void reset(OutputWriter msg, OutputWriter err) {
        try {
            msg.reset();
        } catch (IOException ex) {
            err.println("Unable to reset output window: " + ex.getLocalizedMessage());
        }
    }

    private int cleanWorker(ExecutionEnvironment env) {
        int errorcount = 0;
        FileObject cachefolder = env.paths.getCachefolder();
        if (cachefolder != null) {
            for (FileObject f : cachefolder.getChildren()) {
                if (!deleteFile(f, env)) {
                    errorcount++;
                }
            }
        }
        FileObject outputfolder = env.paths.getOutfolder();
        if (outputfolder != null) {
            for (FileObject f : outputfolder.getChildren()) {
                if (!deleteFile(f, env)) {
                    errorcount++;
                }
            }
        }
        return errorcount;
    }

    private boolean deleteFile(FileObject fo, ExecutionEnvironment env) {
        try {
            fo.delete();
            return true;
        } catch (IOException ex) {
            env.errln("Unable to delete " + fo.getNameExt() + ": " + ex.getLocalizedMessage());
            return false;
        }
    }

    private int buildWorker(ExecutionEnvironment env) {
        env.println("Building...");
        env.commandsequences.getSequence("MAIN").forEach((command) -> command.execute(env));
        return env.getErrorCount();
    }
}
