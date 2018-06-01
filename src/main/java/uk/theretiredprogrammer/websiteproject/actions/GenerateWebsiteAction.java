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
package uk.theretiredprogrammer.websiteproject.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import javax.swing.AbstractAction;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.websiteproject.WebsiteProject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class GenerateWebsiteAction extends AbstractAction {

    private final WebsiteProject project;
    private int copycounter = 0;
    private int generatedcounter = 0;

    public GenerateWebsiteAction(WebsiteProject project) {
        super("Generate Website");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new RequestProcessor(GenerateWebsiteAction.class).post(new WebsiteGenerator());
    }

    private class WebsiteGenerator implements Runnable {

        @Override
        public void run() {
            boolean success = true;
            long start = currentTimeMillis();
            ProjectInformation projectinfo = ProjectUtils.getInformation(project);
            InputOutput io = IOProvider.getDefault().getIO("Website Generator for " + projectinfo.getName(), false);
            io.select();
            try (OutputWriter msg = io.getOut(); OutputWriter err = io.getErr()) {
                try {
                    msg.reset();
                    msg.println();
                    copycounter = 0;
                    generatedcounter = 0;
                    //
                    FileObject projectdirectory = project.getProjectDirectory();
                    FileObject generatedFolder = projectdirectory.getFileObject("generated");
                    if (generatedFolder != null) {
                        generatedFolder.delete(); // clear previously generated/copied materials
                    }
                    generatedFolder = projectdirectory.createFolder("generated");
                    processFolder(projectdirectory.getFileObject("site"),
                            generatedFolder);
                    msg.println("Completed: " + copycounter + " files copied; " + generatedcounter + " files generated");
                } catch (InterruptedException | IOException ex) {
                    success = false;
                    String m = ex.getMessage();
                    if (m != null) {
                        err.println(ex.getMessage());
                    } else {
                        err.println("Failure: exception trapped - no explanation message available");
                        ex.printStackTrace(err);
                    }
                }
                int elapsed = round((currentTimeMillis() - start) / 1000F);
                msg.println("BUILD " + (success ? "SUCCESSFUL" : "FAILED") + " (total time: " + Integer.toString(elapsed) + " seconds)");
            }
        }

        private void processFolder(FileObject fromfolder, FileObject tofolder) throws IOException, InterruptedException {
            for (FileObject child : fromfolder.getChildren()) {
                if (child.isFolder()) {
                    processFolder(child, tofolder.createFolder(child.getName()));
                } else {
                    switch (child.getExt()) {
                        case "md": // markdown files are processed into html which is inserted
                            FileObject template = fromfolder.getFileObject("template", "html");
                            if (template == null) {
                                template = project.getProjectDirectory().getFileObject("site").getFileObject("template", "html");
                                if (template == null) {
                                    throw new IOException("Missing Template File");
                                }
                            }
                            processMarkdown(child, tofolder.createAndOpen(child.getName() + ".html"),
                                    template.getPath());
                            generatedcounter++;
                            break;
                        case "properties": // skip properties files
                            break;
                        default: // default is to copy file
                            child.copy(tofolder, child.getName(), child.getExt());
                            copycounter++;
                    }
                }
            }
        }

        private void processMarkdown(FileObject in, OutputStream out, String templatepath) throws IOException, InterruptedException {
            ProcessBuilder pb = new ProcessBuilder("/usr/local/bin/kramdown", "--template", templatepath);
//        String filepath = mdfile.getAbsolutePath();
//        int dotpos = filepath.lastIndexOf(".");
//        int slashpos = filepath.lastIndexOf("/");
//        String outfilename = filepath.substring(slashpos + 1, dotpos + 1) + "html";
//        String propertiespath = filepath.substring(0, dotpos + 1) + "properties";
//        System.err.println("Creating " + outfilename);
//        SubstitutionProperties props = new SubstitutionProperties();
//        props.putAll(envprops);
//        props.addFromPropertiesFile(new File(propertiespath));
//        props.extractProperties(mdfile);
            //
            pb.redirectInput(FileUtil.toFile(in));
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    PrintWriter toWriter = new PrintWriter(new OutputStreamWriter(out))) {
                String line;
                while ((line = fromReader.readLine()) != null) {
                    toWriter.println(line);
                }
            }
            process.waitFor();
        }
    }
}
