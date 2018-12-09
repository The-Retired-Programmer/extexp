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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.IOOutputPath;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;
import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;

/**
 *
 * @author richard
 */
public class FopExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "FOP";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new FopNode(scene, position);
    }

    private class FopNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public FopNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(EXECUTORIMAGENAME));
            attachPinWidget(new PPin(scene, "from", FopExecutor.this.getParam("fo-xsl")));
            attachPinWidget(new PPin(scene, "to", FopExecutor.this.getParam("pdf")));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Do", "fo-xsl", "pdf");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        IOOutputPath pdf = new IOOutputPath(ee, this.getLocalParameter("pdf"));
        IOInputPath foxsl = new IOInputPath(ee, this.getLocalParameter("fo-xsl"));
        //
        ProcessBuilder pb = new ProcessBuilder("/Users/richard/Applications/fop-2.3/fop/fop",
                "-fo", foxsl.get(),
                "-pdf", pdf.get());
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        try (BufferedReader fromReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = fromReader.readLine()) != null) {
                ee.paths.getMsg().println(line);
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        //
        pdf.close();
        foxsl.close();
    }
}
