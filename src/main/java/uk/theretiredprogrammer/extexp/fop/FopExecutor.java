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
package uk.theretiredprogrammer.extexp.fop;

import java.io.IOException;
import org.openide.util.NbPreferences;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputPath;
import uk.theretiredprogrammer.extexp.support.IOOutputPath;

/**
 * The FOP executor class.
 *
 * Execute a FOP process from the named IOInputPath to the named IOOutputPath.
 *
 * Requires two parameters:
 *
 * 'fo-xsl' - the name of the input (fo-xsl file)
 *
 * 'pdf' - the name of the output (pdf file)
 *
 * Note that the path to the FOP executable must be defined in the FOP options
 * panel before executing this command.
 *
 * @author richard linsdale
 */
public class FopExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "FOP";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"fo-xsl", "pdf"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (
                IOOutputPath pdf = new IOOutputPath(ee, getParameter("pdf"));
                IOInputPath foxsl = new IOInputPath(ee, getParameter("fo-xsl"))) {
            String fopPath = NbPreferences.forModule(FOPPanel.class).get("FOPPath", "fop");
            ProcessExecutor pexec = new ProcessExecutor(fopPath,
                    "-fo", foxsl.get(),
                    "-pdf", pdf.get());
            pexec.setDisplayName("FOP");
            pexec.setErrorLineFunction(s -> ee.errln(s));
            pexec.execute();
        }
    }
}
