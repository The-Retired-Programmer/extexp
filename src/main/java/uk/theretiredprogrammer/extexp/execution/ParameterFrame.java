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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.ARRAY;
import static javax.json.JsonValue.ValueType.NUMBER;
import static javax.json.JsonValue.ValueType.OBJECT;
import static javax.json.JsonValue.ValueType.STRING;

/**
 *
 * @author richard
 */
public class ParameterFrame {

    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, JsonStructure> jsonstructures = new HashMap<>();
    private final Map<String, String> stringfiles = new HashMap<>();
    protected final ParameterFrame previous;

    public ParameterFrame(JsonObject jobj) {
        this(jobj, null);
    }

    public ParameterFrame(JsonObject jobj, ParameterFrame parent){
        previous = parent;
        for (String key : jobj.keySet()) {
            JsonValue jval = jobj.get(key);
            switch (jval.getValueType()) {
                case ARRAY:
                case OBJECT:
                    jsonstructures.put(key, (JsonStructure) jval);
                    break;
                case STRING:
                    if (previous == null) {
                        
                    } else {
                        parameters.put(key, Do.substitute(((JsonString) jobj.get(key)).getString(), (param)-> previous.getParameter(param)));
                    }
                    break;
                case NUMBER:
                    JsonNumber jnum = (JsonNumber) jval;
                    if (jnum.isIntegral()) {
                        parameters.put(key, Integer.toString(jnum.intValueExact()));
                    } else {
                        parameters.put(key, jnum.toString());
                    }
                    break;
                default:
                    parameters.put(key, jval.toString());
            }
        }
    }
    
    public void clearStringFileParameters() {
        stringfiles.clear();
    }

    public void setStringFileParameter(String name, String val) throws IOException {
        if (previous == null) {
            throw new IOException("cant save an outer level To: in  string file (ie a ! file)");
        }
            previous.stringfiles.put(name, val);
    }

    public String getParameter(String name) {
        String val = stringfiles.get(name);
        if (val != null) {
            return val;
        }
        val = parameters.get(name);
        if (val != null) {
            return val;
        }
        return previous == null ? null : previous.getParameter(name);
    }
    
    public String getFrameParameter(String name) {
        String val = stringfiles.get(name);
        if (val != null) {
            return val;
        }
        val = parameters.get(name);
        if (val != null) {
            return val;
        }
        return null;
    }
    
    public JsonStructure getJsonParameter(String name) {
        return jsonstructures.get(name);
    }

    public boolean containsKey(String key) {
        if (stringfiles.containsKey(key)) {
            return true;
        }
        if (parameters.containsKey(key)) {
            return true;
        }
        if (jsonstructures.containsKey(key)) {
            return true;
        }
        return previous == null ? false : previous.containsKey(key);
    }
}
