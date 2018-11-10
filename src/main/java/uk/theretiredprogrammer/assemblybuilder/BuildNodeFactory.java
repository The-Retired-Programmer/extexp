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
@NodeFactory.Registration(projectType = "uk-theretiredprogrammer-assemblybuilderproject", position = 40)
public class BuildNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        AssemblyBuilderProject p = project.getLookup().lookup(AssemblyBuilderProject.class);
        assert p != null;
        return new BuildNodeList(p);
    }

    private class BuildNodeList implements NodeList<Node> {

        AssemblyBuilderProject project;

        public BuildNodeList(AssemblyBuilderProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> result = new ArrayList<>();
            FileObject book = project.getProjectDirectory().getFileObject("build.json");
            if (book != null) {
                try {
                Node booknode = DataObject.find(book).getNodeDelegate();
                result.add(new BuildNode(booknode));
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
            }
            return result;
        }

        public class BuildNode extends FilterNode {

            @StaticResource()
            public static final String ASSEMBLYBUILDERFOLDER_ICON = "uk/theretiredprogrammer/assemblybuilder/wrench.png";

            public BuildNode(Node onode) {
                super(onode);
            }

            @Override
            public String getHtmlDisplayName() {
                return "Build Definition";
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
