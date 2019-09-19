/*
 * Copyright 2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.freemarker;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.netbeans.api.templates.CreateDescriptor;
import org.netbeans.api.templates.FileBuilder;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputFO;

/**
 * The FREEMARKER2 executor class.
 *
 * Execute a FreeMarker Template (IOInputPath) outputting to the named
 * IOOutputPath, with the set of all parameters and their expanded values
 * presented as template processing parameters.
 *
 * Requires two parameters:
 *
 * 'template' - the name of the template
 *
 * 'to' - the name of the output
 *
 * @author richard linsdale
 */
public class FreeMarker2Executor extends Executor {

    @Override
    public String getDisplayName() {
        return "FREEMARKER2";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"template", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
            Optional<String> outputfn = getParameter("to");
            if (!outputfn.isPresent()){
                ee.errln("missing parameter value(s) for Freemarker2 output");
                throw new IOException("missing parameter value(s) for Freemarker2 output");
            }
        
        try (IOInputFO input = new IOInputFO(ee, getParameter("template"))) {
            freemarkerrun(input.get(), outputfn.get());
        }
    }

    public void freemarkerrun(FileObject templatefo, String outputfn) throws IOException {
           templatefo.setAttribute("template", Boolean.TRUE);
           templatefo.setAttribute("javax.script.ScriptEngine", "freemarker");
        Map<String,Object> attributes = new HashMap<>();
        attributes.put(CreateDescriptor.FREE_FILE_EXTENSION, Boolean.TRUE);
        addAttributes(attributes);
        FileBuilder.createFromTemplate(templatefo, ee.paths.getOutfolder(), outputfn,
                attributes, FileBuilder.Mode.FORMAT);
    }

    private void addAttributes(Map<String,Object> attributes) {
        Set<String> names = new HashSet<>(ee.tempfs.allnames());
        names.forEach((name) -> {
            try {
                Optional<String> p = ee.tempfs.read(name);
                if (p.isPresent()) {
                    attributes.put(name, p.get());
                }
            } catch (IOException ex) {
                ee.errln("Error when creating the Freemarker map - "+ ex.getLocalizedMessage());
            }
        });
        names = new HashSet<>(getAllNames());
        names.forEach((name) -> {
            Optional<String> p = this.getParameter(name);
            if (p.isPresent()) {
                p = this.getSubText(p.get());
                if (p.isPresent()) {
                    attributes.put(name, p.get());
                }
            }
        });
    }
}
