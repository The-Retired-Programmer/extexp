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

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.AbstractAction;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import uk.theretiredprogrammer.extexp.visualeditor.VisualEditorTC;

/**
 *
 * @author richard
 */
public class ActionOpenVisualEditor extends AbstractAction {

    private final ExTexPProject project;

    public ActionOpenVisualEditor(ExTexPProject project) {
        super("Open Visual Editor");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisualEditorTC tc = new VisualEditorTC();
        FileObject buildinstructions = project.getProjectDirectory().getFileObject("build_new.json");
        JsonObject jobj;
        try {
            try (InputStream is = buildinstructions.getInputStream();
                    JsonReader rdr = Json.createReader(is)) {
                jobj = rdr.readObject();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        try {
            tc.deserialise(jobj);
            tc.setSaveSource((jo) -> updateBuildFile(jo));
            tc.setDisplayName(project.getProjectDirectory().getName());
            tc.open();
            tc.requestActive();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private boolean updateBuildFile(JsonObject jobj) {
        try {
            FileObject out = project.getProjectDirectory().getFileObject("build_new.json");
            if (out != null) {

                FileObject bkup = project.getProjectDirectory().getFileObject("build_new.json.bkup");
                if (bkup != null) {
                    bkup.delete();
                }
                FileLock fl = out.lock();
                out.rename(fl, "build_new.json", "bkup");
                fl.releaseLock();
            }
            InputStream is;
            try (OutputStream os = project.getProjectDirectory().createAndOpen("build_new.json")) {
                is = new ByteArrayInputStream(jobj.toString().getBytes());
                FileUtil.copy(is, os);
            }
            is.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
