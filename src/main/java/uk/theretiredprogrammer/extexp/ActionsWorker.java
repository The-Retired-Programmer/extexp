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
import javax.xml.transform.TransformerException;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.execution.BuildFile;
import uk.theretiredprogrammer.extexp.execution.Command;
import uk.theretiredprogrammer.extexp.execution.ExecutionEnvironment;

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
            try {
                msg.reset();
                if (cleanrequired) {
                    cleanWorker(project.getProjectDirectory(), msg, err);
                }
                if (buildrequired) {
                    buildWorker(project.getProjectDirectory(), msg, err);
                }
            } catch (TransformerException | IOException ex) {
                success = false;
                String m = ex.getMessage();
                if (m != null) {
                    err.println(m);
                } else {
                    err.println("Failure: exception trapped - no explanation message available");
                }
                ex.printStackTrace(err);
            }
            int elapsed = round((currentTimeMillis() - start) / 1000F);
            msg.println("BUILD " + (success ? "SUCCESSFUL" : "FAILED") + " (total time: " + Integer.toString(elapsed) + " seconds)");
        }
    }

    private void cleanWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) throws IOException {
        msg.println("Cleaning...");
        FileObject cachefolder = projectfolder.getFileObject("cache");
        if (cachefolder != null) {
            msg.println("   ...cache folder");
            for ( FileObject f : cachefolder.getChildren()){
                f.delete();
            }
        }
        FileObject outputfolder = projectfolder.getFileObject("output");
        if (outputfolder != null) {
            msg.println("   ...output folder");
            for ( FileObject f : outputfolder.getChildren()){
                f.delete();
            }
        }
    }

    private void buildWorker(FileObject projectfolder, OutputWriter msg, OutputWriter err) throws IOException, TransformerException {
        msg.println("Building...");
        ExecutionEnvironment env = BuildFile.initAndParse(projectfolder, msg, err);
        for (Command command : env.commandsequences.getSequence("MAIN")) {
            command.execute(env);
        }
    }
}
