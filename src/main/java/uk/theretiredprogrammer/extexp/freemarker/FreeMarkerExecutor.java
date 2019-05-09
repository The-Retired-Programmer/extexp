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

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputPath;
import uk.theretiredprogrammer.extexp.support.IOWriter;

/**
 * The FREEMARKER executor class.
 *
 * Execute a FreeMarker Template (IOInputPath) outputting to the named IOWriter,
 * with the set of all parameters and their expanded values presented as
 * template processing parameters.
 *
 * Requires two parameters:
 *
 * 'template' - the name of the template
 *
 * 'to' - the name of the output
 *
 * Note that the path to the Freemarker template root must be defined in the
 * FreeMarker options panel before executing this command.
 *
 * @author richard linsdale
 */
public class FreeMarkerExecutor extends Executor {

    private static Configuration cfg = null;
    private final String templateroot;

    public FreeMarkerExecutor(String templateroot) {
        super();
        this.templateroot = templateroot;
        if (cfg == null) {
            buildConfig();
        }
    }

    // for test purposes
    public FreeMarkerExecutor(String test, String templateroot) {
        this.templateroot = templateroot;
        if (cfg == null) {
            buildConfig();
        }
    }

    private void buildConfig() {
        try {
            cfg = new Configuration();
            cfg.setDirectoryForTemplateLoading(new File(templateroot));
            cfg.setDefaultEncoding("UTF-8");
        } catch (IOException ex) {
            cfg = null;
        }
    }

    @Override
    public String getDisplayName() {
        return "FREEMARKER";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"template", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (
                IOWriter output = new IOWriter(ee, getParameter("to"));
                IOInputPath input = new IOInputPath(ee, getParameter("template"))) {
            freemarkerrun((s) -> ee.errln(s), input.get(), output.get(), getFreemarkerMap());
        }
    }

    public void freemarkerrun(Consumer<String> reporter, String templatename, Writer writer, Map<String, String> model) {
        if (cfg == null) {
            reporter.accept("Error FreeMarker: configuration not created");
            return;
        }
        try {
            if (!templatename.startsWith(templateroot)) {
                reporter.accept("Error FreeMarker: requested template does not exist "
                        + "in current template root\n"
                        + "root=" + templateroot
                        + "template=" + templatename);
                return;
            }
            cfg.getTemplate(templatename.substring(templateroot.length())).process(model, writer);
        } catch (TemplateException | IOException ex) {
            reporter.accept("Error FreeMarker: " + ex.getLocalizedMessage());
        }
    }

    private Map<String, String> getFreemarkerMap() {
        Set<String> names = new HashSet<>(ee.tempfs.getAllNames());
        names.addAll(getAllNames());
        Map<String, String> res = new HashMap<>();
        names.forEach((name) -> {
            Optional<String> p = this.getParameter(name);
            if (p.isPresent()) {
                p = this.getSubText(p.get());
                if (p.isPresent()) {
                    res.put(name, p.get());
                }
            }
        });
        return res;
    }
}
