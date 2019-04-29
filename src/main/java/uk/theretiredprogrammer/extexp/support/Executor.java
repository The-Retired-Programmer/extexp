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

import java.util.Map;
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
            switch (val.getValueType()) {
                case OBJECT:
                case ARRAY:
                    ee.errln("Error - illegal parameter type in object\n" + jobj.toString());
                    return;
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
