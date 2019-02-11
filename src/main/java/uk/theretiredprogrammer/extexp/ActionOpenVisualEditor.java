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

import uk.theretiredprogrammer.extexp.support.BuildFile;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;
import uk.theretiredprogrammer.extexp.visualeditor.PTC;

/**
 *
 * @author richard
 */
public class ActionOpenVisualEditor extends AbstractAction {

    private final PProject project;

    public ActionOpenVisualEditor(PProject project) {
        super("Open Visual Editor");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PTC tc = new PTC();
        try {
            ExecutionEnvironment env = BuildFile.initAndParse(project.getProjectDirectory(), null, null);
            //tc.setSaveSource((jo) -> updateBuildFile(jo));
            tc.setDisplayName(project.getProjectDirectory().getName());
            tc.open();
            tc.requestActive();
            tc.deserialise(env);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

//    private boolean updateBuildFile(JsonObject jobj) {
//        try {
//            FileObject out = project.getProjectDirectory().getFileObject("build.json");
//            if (out != null) {
//
//                FileObject bkup = project.getProjectDirectory().getFileObject("build.json.bkup");
//                if (bkup != null) {
//                    bkup.delete();
//                }
//                FileLock fl = out.lock();
//                out.rename(fl, "build.json", "bkup");
//                fl.releaseLock();
//            }
//            InputStream is;
//            try (OutputStream os = project.getProjectDirectory().createAndOpen("build.json")) {
//                is = new ByteArrayInputStream(jobj.toString().getBytes());
//                FileUtil.copy(is, os);
//            }
//            is.close();
//            return true;
//        } catch (IOException ex) {
//            return false;
//        }
//    }
}
