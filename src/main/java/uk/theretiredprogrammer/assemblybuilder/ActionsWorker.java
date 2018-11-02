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
package uk.theretiredprogrammer.assemblybuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class ActionsWorker implements Runnable {

    private final boolean cleanrequired;
    private final boolean buildrequired;
    private final AssemblyBuilderProject project;
    private int pagecount;
    //
    private FileObject projectfolder;
    private FileObject srccontentfolder;
    private FileObject pagefolder;
    private FileObject syscontentfolder;
    private FileObject generatedfolder;
    private FileObject generatedcontentfolder;
    private FileObject generatedpagefolder;
    //
    private OutputWriter msg;
    private OutputWriter err;

    public ActionsWorker(AssemblyBuilderProject project, boolean cleanrequired, boolean buildrequired) {
        this.cleanrequired = cleanrequired;
        this.buildrequired = buildrequired;
        this.project = project;
    }

    @Override
    public void run() {
        boolean success = true;
        pagecount = 0;
        long start = currentTimeMillis();
        ProjectInformation projectinfo = ProjectUtils.getInformation(project);
        InputOutput io = IOProvider.getDefault().getIO("Assembly Builder for " + projectinfo.getName(), false);
        io.select();
        msg = io.getOut();
        err = io.getErr();
        try {
            msg.reset();
            projectfolder = project.getProjectDirectory();
            if (cleanrequired) {
                cleanWorker();
            }
            if (buildrequired) {
                buildWorker();
            }
        } catch (IOException ex) {
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
        msg.close();
        err.close();
    }

    private void cleanWorker() throws IOException {
        msg.println("Cleaning generated directory");
        generatedfolder = projectfolder.getFileObject("generated");
        if (generatedfolder != null) {
            generatedfolder.delete();
        }
    }

    private void buildWorker() throws IOException {
        msg.println("Building all required output");
        generatedfolder = IoUtil.useOrCreateFolder(projectfolder, "generated");
        generatedcontentfolder = IoUtil.useOrCreateFolder(generatedfolder, "content");
        srccontentfolder = projectfolder.getFileObject("src/content");
        syscontentfolder = projectfolder.getFileObject("src/shared-content");
        for (FileObject child : srccontentfolder.getChildren()) {
            if (child.isFolder()) {
                pagefolder = child;
                generatedpagefolder = IoUtil.useOrCreateFolder(generatedcontentfolder, child.getName());
                processPage();
                pagecount++;
            }
        }
        msg.println(pagecount+" documents built");
    }

    private void processPage() throws IOException {
        FileObject assemblyinstructions = pagefolder.getFileObject("assembly.json");
        if (assemblyinstructions == null) {
            throw new IOException("Assembly Instructions (assembly.json) is missing");
        }
        try (InputStream is = assemblyinstructions.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            try (PrintWriter outputwriter = new PrintWriter(generatedpagefolder.createAndOpen(obj.getString("as")))) {
                Build.setFolderSeachOrder(syscontentfolder, pagefolder);
                JsonObject jbuild = obj.getJsonObject("build");
                Build bld = Build.buildAction(jbuild);
                outputwriter.println(bld.getContentString(null));
            }
        }
    }
}
