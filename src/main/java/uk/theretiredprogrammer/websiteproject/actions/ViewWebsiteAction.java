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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import javax.swing.AbstractAction;
import uk.theretiredprogrammer.websiteproject.WebsiteProject;

/**
 *
 * @author richard
 */
public class ViewWebsiteAction extends AbstractAction {

    private final WebsiteProject project;

    public ViewWebsiteAction(WebsiteProject project) {
        super("View Website");
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String encodedpath = project.getProjectDirectory().getPath().replace(" ", "%20");
            String url = "file://" + encodedpath + "/generated/index.html";
            URI uri = new URL(url).toURI();
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri);
            }
        } catch (IOException | URISyntaxException ex) {
        }
    }

}
