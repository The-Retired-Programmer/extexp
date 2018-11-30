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

import uk.theretiredprogrammer.extexp.visualeditor.WidgetData;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author richard
 */
public abstract class Command {

    protected ExecutionEnvironment ee;

    private final Map<String, String> parameters = new HashMap<>();
    private Command parent = null;

    
    public final void execute(ExecutionEnvironment ee) throws IOException {
        this.ee = ee;
        executecommand();
    }

    protected abstract void executecommand() throws IOException ;
    
    void putParameter(String pname, String pvalue) {
        parameters.put(pname, pvalue);
    }
    
    protected String getParam(String name) {
        return parameters.get(name);
    }
    
    protected Map<String,String> getParams() {
        return parameters;
    }

    public String getOptionalLocalParameter(String name) {
        String val = parameters.get(name);
        if (val == null) {
            return null;
        }
        return substitute(val, (s) -> getSubText(s));
    }

    public String getLocalParameter(String name) throws IOException {
        String val = parameters.get(name);
        if (val == null) {
            throw new IOException("Parameter \"" + name + "\" missing");
        }
        return substitute(val, (s) -> getSubText(s));
    }

    public boolean hasLocalParameter(String pname) {
        return parameters.containsKey(pname);
    }

    public void setParent(Command parent) {
        this.parent = parent;
    }

    public String getSubstitutedParameter(String name) throws IOException {
        String paramval = getSubText(name);
        if (paramval == null) {
            throw new IOException("Parameter \"" + name + "\" missing");
        }
        return substitute(paramval, (s) -> getSubText(s));
    }

    public String getOptionalSubstitutedParameter(String name) {
        String paramval = getSubText(name);
        if (paramval == null) {
            return null;
        }
        return substitute(paramval, (s) -> getSubText(s));
    }

    private String getSubText(String name) {
        // precedence; TEMPORARY FILES, PARAMETERS (WITH FULL PARENT DESCENT); FILES
        //
        // TEMPORARY FILES
        String val = ee.tempfs.get(name);
        if (val != null) {
            return val;
        }
        // PARAMETERS (WITH FULL PARENT DESCENT)
        Command cmd = this;
        while (cmd != null) {
            val = cmd.parameters.get(name);
            if (val != null) {
                return val;
            }
            cmd = cmd.parent;
        }
        // FILES
        try {
            return IoUtil.findFile(name, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder()).asText();
        } catch (IOException ex) {
            return null;
        }
    }

    public boolean isParamDefined(String name) {
        // precedence; TEMPORARY FILES, PARAMETERS (WITH FULL PARENT DESCENT); FILES
        //
        // TEMPORARY FILES
        String val = ee.tempfs.get(name);
        if (val != null) {
            return true;
        }
        // PARAMETERS (WITH FULL PARENT DESCENT)
        Command cmd = this;
        while (cmd != null) {
            val = cmd.parameters.get(name);
            if (val != null) {
                return true;
            }
            cmd = cmd.parent;
        }
        return false;
    }

    public abstract WidgetData getWidgetData();
    
    protected void substitute(String in, Function<String, String> getoptionalparam, Writer out) throws IOException {
        out.write(substitute(in, getoptionalparam));
    }
    
    protected String substitute(String in, Function<String, String> getoptionalparam) {
      StringBuilder sb = new StringBuilder();
      substitute(in, getoptionalparam, sb);
      return sb.toString();
    }
    
    private void substitute(String in, Function<String, String> getoptionalparam, StringBuilder out) {
        int p = in.indexOf("${");
        if (p == -1) {
            out.append(in);
            return;
        }
        String fragment = in.substring(0, p);
        if (fragment != null && !fragment.isEmpty()) {
            out.append(fragment);
        }
        int q = in.indexOf("}", p + 2);
        String name = in.substring(p + 2, q);

        fragment = getoptionalparam.apply(name);
        if (fragment != null && !fragment.isEmpty()) {
            substitute(fragment, getoptionalparam, out);
        }

        fragment = in.substring(q + 1);
        if (fragment != null && !fragment.isEmpty()) {
            substitute(fragment, getoptionalparam, out);
        }
    }  
}
