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
package uk.theretiredprogrammer.websiteproject.nodes;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import uk.theretiredprogrammer.websiteproject.WebsiteProject;
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
@NodeFactory.Registration(projectType = "uk-theretiredprogrammer-websiteproject", position = 10)
public class SiteNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        WebsiteProject p = project.getLookup().lookup(WebsiteProject.class);
        assert p != null;
        return new WebsiteNodeList(p);
    }

    private class WebsiteNodeList implements NodeList<Node> {

        WebsiteProject project;

        public WebsiteNodeList(WebsiteProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            FileObject siteFolder
                    = project.getProjectDirectory().getFileObject("site");
            List<Node> result = new ArrayList<>();
            if (siteFolder != null) {
                try {
                Node sitenode = DataObject.find(siteFolder).getNodeDelegate();
                result.add(new SiteNode(sitenode));
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
            }
            return result;
        }
        
        public class SiteNode extends FilterNode {
            
            @StaticResource()
            public static final String WEBSITEFOLDER_ICON = "uk/theretiredprogrammer/websiteproject/folder_page.png";


            public SiteNode(Node onode) {
                super(onode);
            }

            @Override
            public String getHtmlDisplayName() {
                return "Website Sources";
            }
            
            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(WEBSITEFOLDER_ICON);
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
