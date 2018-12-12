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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import org.openide.util.ImageUtilities;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOInputPath;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PPin;
import uk.theretiredprogrammer.extexp.visualeditor.PScene;
import uk.theretiredprogrammer.extexp.visualeditor.PNode;
import uk.theretiredprogrammer.extexp.visualeditor.PNode.Position;

/**
 *
 * @author richard
 */
public class MarkdownExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "MARKDOWN";
    }

    @Override
    public PNode createNode(PScene scene, Position position) {
        return new MarkdownNode(scene, position);
    }

    private class MarkdownNode extends PNode {

        @SuppressWarnings("LeakingThisInConstructor")
        public MarkdownNode(PScene scene, Position position) {
            super(scene, position);
            setNodeName(getDisplayName());
            setNodeImage(ImageUtilities.loadImage(EXECUTORIMAGENAME));
            attachPinWidget(new PPin(scene, "from", MarkdownExecutor.this.getParam("from")));
            attachPinWidget(new PPin(scene, "template", MarkdownExecutor.this.getParam("template"), PPin.OPTIONAL));
            attachPinWidget(new PPin(scene, "to", MarkdownExecutor.this.getParam("to")));
            List<Map.Entry<String, String>> extrapins = getFilteredParameters("Do", "from", "template", "to");
            if (!extrapins.isEmpty()) {
                attachPinWidget(new PPin(scene));
                extrapins.forEach((e) -> attachPinWidget(new PPin(scene, e)));
            }
            scene.getWidgetLayer().addChild(this);
        }
    }

    @Override
    protected void executecommand() throws IOException {
        IOWriter output = new IOWriter(ee, this.getLocalParameter("to"));
        IOInputPath input = new IOInputPath(ee, this.getLocalParameter("from"));
        IOInputPath template = new IOInputPath(ee, this.getOptionalLocalParameter("template"));
        //
        ProcessBuilder pb;
        String templatepath = template.get();
        if (templatepath == null) {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids");
        } else {
            pb = new ProcessBuilder("/usr/local/bin/kramdown", "--no-auto-ids", "--template", templatepath);
        }
        pb.redirectInput(new File(input.get()));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = pb.start();
        Writer out = output.get();
        try (BufferedReader from = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = from.readLine()) != null) {
                out.append(line);
                out.append('\n');
            }
        }
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        //
        output.close();
        input.close();
        template.close();
    }
}
