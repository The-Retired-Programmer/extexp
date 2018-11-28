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
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * @author richard
 */
public class CommandSequenceStore {
    
    private Map<String,CommandSequence> commandsequences = new HashMap<>();
    
    public String addSequence(String name, JsonArray sequence){
        try {
            commandsequences.put(name, getCommandSequence(sequence));
            return "";
        } catch (IOException ex) {
            return ex.getLocalizedMessage();
        }
    }
    
    private CommandSequence getCommandSequence(JsonArray sequence) throws IOException{
        CommandSequence res = new CommandSequence();
        for (JsonObject jobj : sequence.getValuesAs(JsonObject.class)) {
            res.add(CommandFactory.create(jobj));
        }
        return res;
    }
    
    public CommandSequence getSequence(String name) {
        return commandsequences.get(name);
    }
}
