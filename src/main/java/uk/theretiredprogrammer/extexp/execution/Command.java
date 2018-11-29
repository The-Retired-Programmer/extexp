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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richard
 */
public abstract class Command  {
    
    private final Map<String, String> parameters = new HashMap<>();
    private Command parent = null;
    
    void putParameter(String pname, String pvalue){
        parameters.put(pname, pvalue);
    }
    
    public String getOptionalLocalParameter(String name, IOPaths paths,  TemporaryFileStore tempfs) {
        String val = parameters.get(name);
        if (val == null) {
            return null;
        }
        return Do.substitute(val,(s)-> getSubText(s, paths, tempfs));
    }
    
    public String getLocalParameter(String name, IOPaths paths,  TemporaryFileStore tempfs) throws IOException {
        String val = parameters.get(name);
        if (val == null) {
            throw new IOException("Parameter \""+name+"\" missing");
        }
        return Do.substitute(val,(s)-> getSubText(s, paths, tempfs));
    }
    
    public boolean hasLocalParameter(String pname){
        return parameters.containsKey(pname);
    }
    
    public void setParent(Command parent) {
        this.parent = parent;
    }
    
    public String getSubstitutedParameter(String name, IOPaths paths,  TemporaryFileStore tempfs) throws IOException {
        String paramval = getSubText(name,paths, tempfs);
        if (paramval == null) {
            throw new IOException("Parameter \""+name+"\" missing");
        }
        return Do.substitute(paramval,(s)-> getSubText(s, paths, tempfs));
    }
    
    public String getOptionalSubstitutedParameter(String name, IOPaths paths,  TemporaryFileStore tempfs) {
        String paramval = getSubText(name,paths, tempfs);
        if (paramval == null) {
            return null;
        }
        return Do.substitute(paramval,(s)-> getSubText(s, paths, tempfs));
    }
    
    private String getSubText(String name, IOPaths paths,  TemporaryFileStore tempfs) {
        // precedence; TEMPORARY FILES, PARAMETERS (WITH FULL PARENT DESCENT); FILES
        //
        // TEMPORARY FILES
        String val = tempfs.get(name);
        if (val != null) {
            return val;
        }
        // PARAMETERS (WITH FULL PARENT DESCENT)
        Command cmd = this;
        while(cmd != null) {
            val = cmd.parameters.get(name);
            if (val != null){
                return val;
            }
            cmd = cmd.parent;
        }
        // FILES
        try {
            return IoUtil.findFile(name, paths.getContentfolder(), paths.getSharedcontentfolder()).asText();
        } catch (IOException ex) {
            return null;
        }
    }
    
    public boolean isParamDefined(String name, IOPaths paths,  TemporaryFileStore tempfs){
        // precedence; TEMPORARY FILES, PARAMETERS (WITH FULL PARENT DESCENT); FILES
        //
        // TEMPORARY FILES
        String val = tempfs.get(name);
        if (val != null) {
            return true;
        }
        // PARAMETERS (WITH FULL PARENT DESCENT)
        Command cmd = this;
        while(cmd != null) {
            val = cmd.parameters.get(name);
            if (val != null){
                return true;
            }
            cmd = cmd.parent;
        }
        return false;
    }
    
    public abstract WidgetData getWidgetData();
}
