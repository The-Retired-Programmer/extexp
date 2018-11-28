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

import java.util.Map;
import java.util.function.BiFunction;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author richard
 */
public class BuildFile {

    public static String parse(JsonObject jobj,BiFunction<String, JsonArray, String> sequencehandler) {
        for (Map.Entry<String, JsonValue> es : jobj.entrySet()) {
            String name = es.getKey();
            JsonValue content = es.getValue();
            switch (content.getValueType()) {
                case ARRAY:
                    String r = sequencehandler.apply(name, (JsonArray) content);
                    if (!r.isEmpty()) {
                        return r;
                    }
                    break;
                default:
                    return "parsing - Illegal Json File content (3)";
            }
        }
        return "";
    }
}
