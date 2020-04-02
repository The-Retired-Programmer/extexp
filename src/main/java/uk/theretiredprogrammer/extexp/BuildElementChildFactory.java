/*
 * Copyright 2019-2020 richard linsdale.
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

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;
import uk.theretiredprogrammer.extexp.actions.ActionBuild;
import uk.theretiredprogrammer.extexp.actions.ActionDebugBuild;
import uk.theretiredprogrammer.extexp.options.ExtexpPanel;
import uk.theretiredprogrammer.extexp.visualeditor.ActionOpenVisualEditor;

/**
 * A Children Factory to provide BuildElementNodes (the xxx,json files).
 *
 * @author richard linsdale
 */
public class BuildElementChildFactory extends ChildFactory<FileObject> {

    private final FileObject buildfolder;
    private final PProject project;

    public BuildElementChildFactory(PProject project, FileObject buildfolder) {
        this.buildfolder = buildfolder;
        this.project = project;
    }

    @Override
    protected boolean createKeys(List<FileObject> toPopulate) {
        for (FileObject fo : buildfolder.getChildren()) {
            if (fo.isData() && fo.hasExt("json")) {
                toPopulate.add(fo);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(FileObject fo) {
        Node node;
        try {
            node = DataObject.find(fo).getNodeDelegate();
            return new BuildElementNode(node, fo);
        } catch (DataObjectNotFoundException ex) {
            return null;
        }
    }

    public class BuildElementNode extends FilterNode {

        private final FileObject fo;

        @StaticResource()
        public static final String EXTEXPBUILDELEMENT_ICON = "uk/theretiredprogrammer/extexp/script_edit.png";

        public BuildElementNode(Node onode, FileObject fo) {
            super(onode);
            this.fo = fo;
        }

        @Override
        public String getHtmlDisplayName() {
            String fn = fo.getName();
            return fn.startsWith("_")
                    ? fn.substring(1)
                    : fn;
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(EXTEXPBUILDELEMENT_ICON);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Action[] getActions(boolean arg0) {
            int actioncount = 1;
            boolean enableDebug = NbPreferences.forModule(ExtexpPanel.class).getBoolean("EnableDebug", false);
            boolean enableVisualEditor = NbPreferences.forModule(ExtexpPanel.class).getBoolean("EnableVisualEditor", false);
            if (enableDebug) {
                actioncount++;
            }
            if (enableVisualEditor) {
                actioncount++;
            }
            Action[] actions = new Action[actioncount];
            int p = 0;
            actions[p++] = new ActionBuild(project, fo);
            if (enableDebug) {
                actions[p++] = new ActionDebugBuild(project, fo);
            }
            if (enableVisualEditor) {
                actions[p++] = new ActionOpenVisualEditor(project, fo);
            }
            return fo.getName().startsWith("_")
                    ? new Action[]{}
                    : actions;
        }
    }
}
