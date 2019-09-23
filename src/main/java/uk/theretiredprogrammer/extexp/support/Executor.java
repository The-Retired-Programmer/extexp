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

import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * The Executor provides the common functionality of all Executors. It extends
 * the Command functionality.
 *
 * @author richard linsdale
 */
public abstract class Executor extends Command {

    /**
     * The Common Image for all executors - as used in the Visual Editor
     */
    public static final String EXECUTORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_switch.png";

    /**
     *  Get the Image name
     * 
     * @return the image name
     */
    @Override
    public String getWidgetImageName() {
        return EXECUTORIMAGENAME;
    }

    /**
     * Get all mandatory PinData texts for this Executor
     * 
     * @return the array of pindata texts
     */
    public abstract String[] getPrimaryPinData();
    
    @Override
    public final void parse(JsonObject jobj) {
        for (Map.Entry<String, JsonValue> paramdef : jobj.entrySet()) {
            JsonValue val = paramdef.getValue();
            String name = paramdef.getKey();
            switch (val.getValueType()) {
                case OBJECT:
                        ee.errln("Error - JsonObject not allowed at this point\n" + jobj.toString());
                        return;
                case ARRAY:
                    Map<String, String> group = new HashMap<>();
                    ((JsonArray) val).forEach(item -> {
                        if (item.getValueType() != JsonValue.ValueType.STRING) {
                            ee.errln("Error - illegal value in Array\n" + item.toString());
                            return;
                        }
                        String itemstr = ((JsonString) item).getString();
                        int pos = itemstr.indexOf("->");
                        if (pos == -1) {
                            ee.errln("Error - badly formated value (-> missing) in Array\n" + item.toString());
                            return;
                        }
                        String filename = itemstr.substring(0, pos);
                        String key = itemstr.substring(pos + 2);
                        group.put(key, filename);
                    });
                    setFileGroup(name, group);
                    break;
                case STRING:
                    putParameter(paramdef.getKey(), ((JsonString) val).getString());
                    break;
                case NUMBER:
                    JsonNumber num = (JsonNumber) val;
                    try {
                        long l = num.longValueExact();
                        putParameter(paramdef.getKey(), Long.toString(l));
                    } catch (ArithmeticException ex) {
                        putParameter(paramdef.getKey(), num.toString());
                    }
                    break;
                default:
                    putParameter(paramdef.getKey(), val.toString());
            }
        }
    }
}
