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
package uk.theretiredprogrammer.extexp.basic;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import uk.theretiredprogrammer.extexp.support.Executor;

/**
 * The COPYRESOURCE executor class.
 *
 * Copies all files from the input folder (recursive decent through the folder
 * structure) , outputting all files to the target folder (as a flat structure).
 *
 * Requires two parameters:
 *
 * 'foldername' - the name of the input folder (if not defined it defaults to
 * 'resources')
 *
 * 'to' - the required target for the copy ('cache' or 'output') (if not defined
 * it defaults to 'output')
 *
 * @author richard linsdale
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
    public String[] getPrimaryPinData() {
        return new String[]{"to", "foldername"};
    }

    @Override
    protected void executecommand() {
        String target = getParameter("to").orElse("");
        FileObject toFO;
        switch (target) {
            case "cache":
                toFO = ee.paths.getCachefolder();
                break;
            default:  // default is same as "output"
                toFO = ee.paths.getOutfolder();
                break;
        }
        String foldername = getParameter("foldername").orElse("resources");
        toFO = ee.paths.setResourcesfolder(toFO, foldername);
        FileObject fromFO = ee.paths.getContentfolder().getFileObject(foldername);
        copyresources(fromFO, toFO);
    }

    private void copyresources(FileObject fromFO, FileObject toFO) {
        for (FileObject fo : fromFO.getChildren()) {
            if (fo.isFolder()) {
                copyresources(fo, toFO);
            } else {
                try {
                    // assume isData()
                    FileUtil.copyFile(fo, toFO, fo.getName());
                } catch (IOException ex) {
                    ee.errln("Error while copying resources: " + ex.getLocalizedMessage());
                }
            }
        }
    }
}
