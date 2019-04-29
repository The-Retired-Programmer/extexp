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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.RequestProcessor;
import uk.theretiredprogrammer.extexp.PProject;

/**
 * The Open Visual Editor Action class
 * 
 * @author richard linsdale
 */
public class ActionOpenVisualEditor extends AbstractAction {

    private final PProject project;

    /**
     * Constructor
     * 
     * @param project the Extexp project
     */
    public ActionOpenVisualEditor(PProject project) {
        super("Open Visual Editor");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new RequestProcessor(ActionOpenVisualEditor.class).post(new VisualEditorWorker(project));
    }
}
