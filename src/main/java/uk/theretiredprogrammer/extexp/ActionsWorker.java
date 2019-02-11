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
package uk.theretiredprogrammer.extexp;

import java.io.IOException;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.support.BuildFile;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;

/**
 *
 * @author richard
 */
public class ActionsWorker implements Runnable {

    private final boolean cleanrequired;
    private final boolean buildrequired;
    private final PProject project;

    public ActionsWorker(PProject project, boolean cleanrequired, boolean buildrequired) {
        this.cleanrequired = cleanrequired;
        this.buildrequired = buildrequired;
        this.project = project;
    }

    @Override
    public void run() {
        boolean success = true;
        long start = currentTimeMillis();
        ProjectInformation projectinfo = ProjectUtils.getInformation(project);
        InputOutput io = IOProvider.getDefault().getIO("Assembly Builder for " + projectinfo.getName(), false);
        io.select();
        try (OutputWriter msg = io.getOut(); OutputWriter err = io.getErr()) {
            reset(msg, err);
            if (cleanrequired) {
                cleanWorker(project.getProjectDirectory(), msg, err);
            }
            if (buildrequired) {
                buildWorker(project.getProjectDirectory(), msg, err);
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("BUILD " + (success ? "SUCCESSFUL" : "FAILED") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void reset(OutputWriter msg, OutputWriter err) {
        try {
            msg.reset();
        } catch (IOException ex) {
            err.println("Unable to reset output window: " + ex.getLocalizedMessage());
        }
    }

    private void cleanWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) {
        msg.println("Cleaning...");
        FileObject cachefolder = projectfolder.getFileObject("cache");
        if (cachefolder != null) {
            msg.println("   ...cache folder");
            for (FileObject f : cachefolder.getChildren()) {
                deleteFile(f, err);
            }
        }
        FileObject outputfolder = projectfolder.getFileObject("output");
        if (outputfolder != null) {
            msg.println("   ...output folder");
            for (FileObject f : outputfolder.getChildren()) {
                deleteFile(f, err);
            }
        }
    }

    private void deleteFile(FileObject fo, OutputWriter err) {
        try {
            fo.delete();
        } catch (IOException ex) {
            err.println("Unable to delete " + fo.getNameExt() + ": " + ex.getLocalizedMessage());
        }
    }

    private void buildWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) {
        msg.println("Building...");
        ExecutionEnvironment env = BuildFile.initAndParse(projectfolder, msg, err);
        env.commandsequences.getSequence("MAIN").forEach((command) -> {
            command.execute(env);
        });
    }
}
