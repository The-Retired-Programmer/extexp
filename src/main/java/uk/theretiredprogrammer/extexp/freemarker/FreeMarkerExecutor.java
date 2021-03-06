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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.netbeans.api.templates.CreateDescriptor;
import org.netbeans.api.templates.FileBuilder;
import org.openide.filesystems.FileObject;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOFactory;
import uk.theretiredprogrammer.extexp.support.IOFactory.OutputDescriptor;

/**
 * The FREEMARKER executor class.
 *
 * Execute a FreeMarker Template outputting to the named output, with the set of
 * all parameters (expanded values) presented as template processing parameters,
 * additional files may be presented as processing parameters, if explicitly
 * declared.
 *
 * Requires two parameters:
 *
 * 'template' - the name of the template
 *
 * 'to' - the name of the output
 *
 * 'uses' - a file group which is be be added to the processing
 * parameters(optional)
 *
 * @author richard linsdale
 */
public class FreeMarkerExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "FREEMARKER";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"template", "to", "uses-files", "uses-data-files"};
    }

    @Override
    protected void executecommand() throws IOException {
        Optional<String> outputfn = getParameter("to");
        if (!outputfn.isPresent()) {
            throw new IOException("missing parameter value(s) for Freemarker output");
        }
        Map<String, String> filesgroup = null;
        Optional<String> filesp = getParameter("uses-files");
        if (filesp.isPresent()) {
            filesgroup = getFileGroup(filesp.get());
        }
        Map<String, String> datafilesgroup = null;
        Optional<String> datafilesp = getParameter("uses-data-files");
        if (datafilesp.isPresent()) {
            datafilesgroup = getFileGroup(datafilesp.get());
        }
        FileObject input = IOFactory.getInputFO(ee, getParameter("template"));
        freemarkerrun(input, outputfn.get(), filesgroup, datafilesgroup);
    }

    private void freemarkerrun(FileObject templatefo, String outputfn,
            Map<String, String> filesgroup, Map<String, String> datafilesgroup) throws IOException {
        templatefo.setAttribute("template", Boolean.TRUE);
        templatefo.setAttribute("javax.script.ScriptEngine", "freemarker");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CreateDescriptor.FREE_FILE_EXTENSION, Boolean.TRUE);
        addParameterAttributes(attributes);
        if (filesgroup != null) {
            addFileAttributes(attributes, filesgroup);
        }
        if (datafilesgroup != null) {
            addDataFileAttributes(attributes, datafilesgroup);
        }
        OutputDescriptor iod = IOFactory.getOutputDescriptor(ee, outputfn);
        FileBuilder.createFromTemplate(templatefo, iod.folder, iod.filename,
                attributes, FileBuilder.Mode.FORMAT);
    }

    private void addParameterAttributes(Map<String, Object> attributes) {
        Set<String> names = new HashSet<>(getAllNames());
        names.forEach((name) -> {
            Optional<String> p = this.getParameter(name);
            if (p.isPresent()) {
                attributes.put(name, p.get());
            }
        });
    }

    private void addFileAttributes(Map<String, Object> attributes, Map<String, String> filesgroup) throws IOException {
        for (Map.Entry<String, String> fileentry : filesgroup.entrySet()) {
            String filename = substitute(fileentry.getValue());
            String key = substitute(fileentry.getKey());
            FileObject fo = IOFactory.getInputFO(ee, filename);
            if (fo == null) {
                throw new IOException("Filename in uses-file group is missing(" + filename + ")");
            }
            attributes.put(key, fo.asText());
        }
    }

    private void addDataFileAttributes(Map<String, Object> attributes, Map<String, String> datafilesgroup) throws IOException {
        for (Map.Entry<String, String> datafiles : datafilesgroup.entrySet()) {
            String datafilename = substitute(datafiles.getValue());
            String key = substitute(datafiles.getKey());
            FileObject fo = IOFactory.getInputFO(ee, datafilename);
            if (fo == null) {
                throw new IOException("Filename in uses-file group is missing(" + datafilename + ")");
            }
            List<String> alllines = fo.asLines();
            String[] hdrcolumns = alllines.get(0).split(",");
            for (int i = 0; i < hdrcolumns.length; i++) {
                hdrcolumns[i] = hdrcolumns[i].trim();
            }
            attributes.put(key,
                    alllines.subList(1, alllines.size()).stream()
                            .map((line) -> line2map(hdrcolumns, line))
                            .collect(Collectors.toList())
            );
        }
    }

    private Map<String, Object> line2map(String[] hdrcolumns, String line) {
        Map<String, Object> result = new HashMap<>();
        String[] datacolumns = line.split(",");
        for (int col = 0; col < hdrcolumns.length; col++) {
            String colval = col < datacolumns.length
                    ? datacolumns[col].trim()
                    : "";
            result.put(hdrcolumns[col], colval);
        }
        return result;
    }
}
