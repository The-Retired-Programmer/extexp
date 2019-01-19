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
package uk.theretiredprogrammer.extexp.executors;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.PNode;
import uk.theretiredprogrammer.extexp.execution.PNode.Position;
import uk.theretiredprogrammer.extexp.execution.PPin;
import uk.theretiredprogrammer.extexp.execution.PScene;

/**
 *
 * @author richard
 */
public class CopyResourcesExecutor extends Executor {

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
        return new CopyResourceNode(scene, position);
    }

    private class CopyResourceNode extends PNode{

        @SuppressWarnings("LeakingThisInConstructor")
        public CopyResourceNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(COPYRESOURCESIMAGENAME));
            attachPinWidget(new PPin(scene, "to", CopyResourcesExecutor.this.getParam("to")));
            attachPinWidget(new PPin(scene, "foldername", CopyResourcesExecutor.this.getParam("foldername"), PPin.OPTIONAL));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Do", "to", "foldername");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        String target = getLocalParameter("to");
        FileObject toFO;
        switch (target){
            case "output":
                toFO = ee.paths.getOutfolder();
                break;
            case "cache":
                toFO = ee.paths.getCachefolder();
                break;
            default:
                throw new IOException("Illegal \"to\" parameter value in copy-resource");
        }
        String foldername = getOptionalLocalParameter("foldername", "resources");
        toFO = ee.paths.setResourcesfolder(toFO, foldername);
        FileObject fromFO = ee.paths.getContentfolder().getFileObject(foldername);
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
