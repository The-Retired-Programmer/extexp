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
package uk.theretiredprogrammer.extexp.support;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.json.JsonObject;
import org.openide.filesystems.FileObject;

/**
 * The Command is a representation of the common functionality of both Control
 * and Executors.
 *
 * This includes items such as ExecutionEnvironment, Command Parameters, link to
 * parent Command (to be able to access outer parameters)
 *
 * Basic command information for use by the Visual editor (name and image)
 *
 * Parameter access (read), both raw format and full expanded (parameter
 * substitution, file content expanded)
 *
 * @author richard linsdale
 */
public abstract class Command {

    /**
     * The Positioning of a command within a vertical sequence (for Visual
     * Editor display)
     */
    public static enum Position {

        /**
         * Position to left of centre of the vertical display
         */
        LEFT,
        /**
         * Position in the normal vertical centre line of the vertical display
         */
        NORMAL,
        /**
         * Position to right of centre of the vertical display
         */
        RIGHT
    }

    /**
     * the Execution Environment in which this Command is run
     */
    protected ExecutionEnvironment ee;

    private final Map<String, String> parameters = new HashMap<>();
    private Command parent = null;

    /**
     * Get the Image name for this command
     *
     * @return the Image name
     */
    public abstract String getWidgetImageName();

    /**
     * Get the Display name for this Command
     *
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Parse the provided JsonObject to build this command.
     *
     * @param jobj the json object representing this executor (the parameters)
     */
    public abstract void parse(JsonObject jobj);

    /**
     * Execute the command with the given ExecutionEnvironment
     *
     * @param ee the ExecutionEnvironemnt
     */
    public final void execute(ExecutionEnvironment ee) {
        this.ee = ee;
        try {
            executecommand();
        } catch (IOException ex) {
            ee.errln("Error: processing " + this.getDisplayName() + " - " + ex.getLocalizedMessage());
        }
    }

    /**
     * Execute the command
     *
     * @throws IOException if a problem
     */
    protected abstract void executecommand() throws IOException;

    /**
     * Insert a parameter value into this command parameter store
     *
     * @param pname the parameter name
     * @param pvalue the parameter value
     */
    protected void putParameter(String pname, String pvalue) {
        parameters.put(pname, pvalue);
    }

    /**
     * Get the Set of all parameter names in the full environment for this
     * command (includes all command local parameters and all parent command's
     * parameters
     *
     * @return the full set of all command parameters
     *
     */
    protected Set<String> getAllNames() {
        Set<String> all = new HashSet<>();
        if (parent != null) {
            all.addAll(parent.getAllNames());
        }
        all.addAll(parameters.keySet());
        return all;
    }

    /**
     * Get the raw parameter value for a named local parameter.
     *
     * The raw parameter is the text as presented by the putParameter method, it
     * is not processed with any form of substitution or expansion
     *
     * @param name the name of the requested parameter value
     * @return the raw parameter value
     */
    public Optional<String> getParameterText(String name) {
        return Optional.ofNullable(parameters.get(name));
    }

    /**
     * Get the expanded parameter value for a named local parameter.
     *
     * Expansion include both file content expansion and parameter substitution
     *
     * @param name the name of the requested parameter value
     * @return the expanded parameter value
     */
    public Optional<String> getLocalParameter(String name) {
        return substitute(Optional.ofNullable(parameters.get(name)), (s) -> getSubText(s));
    }

    /**
     * Set the command's parent command.
     *
     * @param parent the parent command
     */
    public void setParent(Command parent) {
        this.parent = (Command) parent;
    }

    /**
     * Get the expanded parameter value for a named parameter.
     *
     * The parameter can be either the a localfilesystem file, a local parameter
     * or an outer command's parameter. Expansion include both file content
     * expansion and parameter substitution
     *
     * @param name the name of the requested parameter value
     * @return the expanded parameter value
     */
    public Optional<String> getParameter(String name) {
        return substitute(getParameterValue(name), (s) -> getSubText(s));
    }

    private Optional<String> getParameterValue(String name) {
        Optional<String> pval = Optional.ofNullable(parameters.get(name));
        if (pval.isPresent()) {
            return pval;
        }
        if (parent == null) {
            return pval;
        }
        return parent.getParameterValue(name);
    }

    private Optional<String> getParameterValue(Optional<String> name) {
        if (name.isPresent()) {
            String pval = parameters.get(name.get());
            if (pval != null) {
                return Optional.of(pval);
            }
            if (parent == null) {
                return Optional.empty();
            }
            return parent.getParameterValue(name);
        }
        return Optional.empty();
    }

    private Optional<String> getFileValue(String name) {
        return Optional.ofNullable(
                findFile(ee, name, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder())
                        .map(fo -> getFileContent(fo, ee)).orElse(null));
    }

    private String getFileContent(FileObject fo, ExecutionEnvironment ee) {
        try {
            return fo.asText();
        } catch (IOException ex) {
            ee.errln("Error when extracting text from file: " + ex.getLocalizedMessage());
            return null;
        }
    }

    private Optional<FileObject> findFile(ExecutionEnvironment ee, String filename, FileObject... fos) {
        for (FileObject fo : fos) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return Optional.ofNullable(file);
                }
            }
        }
        return Optional.empty();
    }

    protected Optional<String> getSubText(String name) {
        // precedence; TEMPORARY FILES / FileValue / Parameter value
        return ee.tempfs.get(name)
                .or(() -> getParameterValue(name))
                .or(() -> getFileValue(name))
                .or(() -> Optional.ofNullable(name));
    }

    /**
     * Test is a given name can be resolved with a value.
     *
     * This can be either a temporaryfilestore file or a command parameter
     *
     * @param name the name to be tested for resolution
     * @return true if the name can be resolved
     */
    public boolean isParamDefined(Optional<String> name) {
        // precedence; TEMPORARY FILES, PARAMETERS (WITH FULL PARENT DESCENT);
        return (ee.tempfs.get(name)
                .or(() -> getParameterValue(name))).isPresent();
    }

    /**
     * Get a map of all local parameter names and raw values, ignoring any names
     * defined in the parameter list.
     *
     * @param ignore a series of names (keys) to be ignored in the map
     * @return the map of required parameter names and values
     */
    public final List<Map.Entry<String, String>> getFilteredParameters(String... ignore) {
        List<Map.Entry<String, String>> extras = new ArrayList<>();
        boolean match;
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            match = false;
            for (String pname : ignore) {
                if (pname.equals(param.getKey())) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                extras.add(param);
            }
        }
        return extras;
    }

    /**
     * Process an input string substituting all occurrences of substitution
     * indicators with text generated by a supplied lookup function, the
     * resulting text being written to a given Writer.
     *
     * The substitution indicator is of the form ${'substitutionkey'}. There is
     * special format of the indicator ${*} which is request for a unique ID to
     * be substituted. see
     * {@link uk.theretiredprogrammer.extexp.support.local.IDGenerator} for more
     * details of this.
     *
     * Substitution can be from various sources, the associated priority order
     * is:
     *
     * 1) localfilestore - replacement of substitution indicator with the
     * textual content of the named file in localfilestore
     *
     * 2) parameter - replacement of the substitution indicator with the
     * parameter value, seaching first the local parameters and then in turn
     * through parent command parameters
     *
     * 3) File store - two directories within the filestore are then searched.
     * The first is the current source folder, the second is the shared source
     * folder (see {@link IOPaths} for more details of these folders}). If a
     * file exists with the substitutionkey as its name then its content will
     * replace the substitution indicator.
     *
     * 4) Otherwise - the default if no previous cases are matched, then the raw
     * parameter value will replace the substitution indicator.
     *
     * Note that the processing of substitution is fully recursive so any
     * further substitution indicators found during the stages of the process
     * will themselves be substituted.
     *
     * @param in the input string
     * @param getparam the function to provide a substitution value given the
     * substitution key
     * @param out the Writer to receive the resulting string
     */
    protected void substitute(Optional<String> in, Function<String, Optional<String>> getparam, Writer out) {
        substitute(in, getparam).ifPresent(s -> writeout(s, out));
    }

    private void writeout(String s, Writer out) {
        try {
            out.write(s);
        } catch (IOException ex) {
            ee.errln("Error while writing substituted text: " + ex.getLocalizedMessage());
        }
    }

    /**
     * Process an input string substituting all occurrences of substitution
     * indicators with text generated by a supplied lookup function, return the
     * resulting string.
     *
     * The substitution indicator is of the form ${'substitutionkey'}. There is
     * special format of the indicator ${*} which is request for a unique ID to
     * be substituted. see
     * {@link uk.theretiredprogrammer.extexp.support.local.IDGenerator} for more
     * details of this.
     *
     * Substitution can be from various sources, the associated priority order
     * is:
     *
     * 1) localfilestore - replacement of substitution indicator with the
     * textual content of the named file in localfilestore
     *
     * 2) parameter - replacement of the substitution indicator with the
     * parameter value, seaching first the local parameters and then in turn
     * through parent command parameters
     *
     * 3) File store - two directories within the filestore are then searched.
     * The first is the current source folder, the second is the shared source
     * folder (see {@link IOPaths} for more details of these folders}). If a
     * file exists with the substitutionkey as its name then its content will
     * replace the substitution indicator.
     *
     * 4) Otherwise - the default if no previous cases are matched, then the raw
     * parameter value will replace the substitution indicator.
     *
     * Note that the processing of substitution is fully recursive so any
     * further substitution indicators found during the stages of the process
     * will themselves be substituted.
     *
     * @param in the input string
     * @param getparam the function to provide a substitution value given the
     * substitution key
     * @return the resulting string
     */
    protected Optional<String> substitute(Optional<String> in, Function<String, Optional<String>> getparam) {
        return in.map(i -> sub(i, getparam));
    }

    /* note this is not private, as it has been exposed to support unit testing */
    String sub(String in, Function<String, Optional<String>> getparam) {
        int p = in.indexOf("${");
        if (p == -1) {
            return in;
        }
        StringBuilder sb = new StringBuilder();
        String fragment = in.substring(0, p);
        if (!fragment.isEmpty()) {
            sb.append(fragment
            );
        }
        int q = in.indexOf("}", p + 2);
        String name = in.substring(p + 2, q);
        if (name.equals("*")) {
            sb.append(ee.idgenerator.generateID());
        } else {
            Optional<String> frag = getparam.apply(name);
            if (frag.isPresent()) {
                sb.append(sub(frag.get(), getparam));
            }
        }
        fragment = in.substring(q + 1);
        if (!fragment.isEmpty()) {
            sb.append(sub(fragment, getparam));
        }
        return sb.toString();
    }
}
