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
package uk.theretiredprogrammer.extexp.execution;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;

/**
 *
 * @author richard
 */
public class CopyResourcesControl extends Control {

    private static final String COPYRESOURCESIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_in.png";

    @Override
    public String getWidgetImageName() {
        return COPYRESOURCESIMAGENAME;
    }

    @Override
    public String getDisplayName() {
        return "COPY RESOURCES";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new RunNode(scene, position);
    }

    private class RunNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public RunNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(COPYRESOURCESIMAGENAME));
            attachPinWidget(new PPin(scene, "Copy-resources", CopyResourcesControl.this.getParam("Copy-resources")));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Copy-resources");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        String target = getLocalParameter("Copy-resources");
        FileObject toFO;
        switch (target){
            case "output":
                toFO = ee.paths.getOutfolder();
                break;
            case "cache":
                toFO = ee.paths.getCachefolder();
                break;
            default:
                throw new IOException("Illegal parameter value in Copy-Resource Control");
        }
        toFO = ee.paths.setResourcesfolder(toFO);
        FileObject fromFO = ee.paths.getContentfolder().getFileObject("resources");
        copyresources(fromFO, toFO);
    }
    
    private void copyresources(FileObject fromFO, FileObject toFO) throws IOException {
        for (FileObject fo: fromFO.getChildren()) {
            if(fo.isFolder()) {
                copyresources(fo,toFO);
            } else { // assume isData()
                FileUtil.copyFile(fo, toFO, fo.getName());
            }
        }
    }
}
