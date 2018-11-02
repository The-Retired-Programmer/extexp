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

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author richard
 */
@NodeFactory.Registration(projectType = "uk-theretiredprogrammer-assemblybuilderproject", position = 20)
public class SharedSourceNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        AssemblyBuilderProject p = project.getLookup().lookup(AssemblyBuilderProject.class);
        assert p != null;
        return new SharedSourceNodeList(p);
    }

    private class SharedSourceNodeList implements NodeList<Node> {

        AssemblyBuilderProject project;

        public SharedSourceNodeList(AssemblyBuilderProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject srcFolder
                    = project.getProjectDirectory().getFileObject("src/shared-content");
            List<Node> result = new ArrayList<>();
            if (srcFolder != null) {
                try {
                    Node sitenode = DataObject.find(srcFolder).getNodeDelegate();
                    result.add(new SharedSourceNode(sitenode));
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return result;
        }

        public class SharedSourceNode extends FilterNode {

            @StaticResource()
            public static final String ASSEMBLYBUILDERFOLDER_ICON = "uk/theretiredprogrammer/assemblybuilder/folder_page.png";

            public SharedSourceNode(Node onode) {
                super(onode);
            }

            @Override
            public String getHtmlDisplayName() {
                return "Shared Sources";
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(ASSEMBLYBUILDERFOLDER_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
