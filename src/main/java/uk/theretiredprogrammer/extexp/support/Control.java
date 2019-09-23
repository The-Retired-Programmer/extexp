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
import java.util.Map.Entry;
import java.util.Optional;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * The Control provides the common functionality of all Controls. It extends the
 * Command functionality.
 *
 * @author richard linsdale
 */
public abstract class Control extends Command {

    /**
     * Controls may have connected Commands. ConnectionData holds relative
     * positional data for these connected commands. (for Visual Editor
     * purposes)
     */
    public class ConnectedData {

        /**
         * The name for this connection
         */
        public final String name;

        /**
         * the relative Position for this connection
         */
        public final Position position;

        /**
         * create a ConnectedData instance.
         *
         * @param name name for the connection
         * @param position relative position for the connection
         */
        public ConnectedData(String name, Position position) {
            this.position = position;
            this.name = name;
        }
    }

    /* map of connected commands */
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Get all mandatory PinData texts for this Control
     *
     * @return the array of pindata texts
     */
    public abstract String[] getPrimaryPinData();

    /**
     * Get all connection PinData texts for this Control
     *
     * @return the array of connectionData instances
     */
    public ConnectedData[] getConnectedPinData() {
        return new ConnectedData[]{};
    }

    @Override
    public void parse(JsonObject jobj) {
        for (Entry<String, JsonValue> paramdef : jobj.entrySet()) {
            JsonValue val = paramdef.getValue();
            String name = paramdef.getKey();
            switch (val.getValueType()) {
                case OBJECT:
                    Optional<? extends Command> cmd = CommandFactory.create((JsonObject) val);
                    cmd.ifPresent(c -> commands.put(paramdef.getKey(), c));
                    break;
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
                    putParameter(name, ((JsonString) val).getString());
                    break;
                case NUMBER:
                    JsonNumber num = (JsonNumber) val;
                    try {
                        long l = num.longValueExact();
                        putParameter(name, Long.toString(l));
                    } catch (ArithmeticException ex) {
                        putParameter(name, num.toString());
                    }
                    break;
                default:
                    putParameter(name, val.toString());
            }
        }
    }

    /**
     * Get a connected command
     *
     * @param name the name
     * @return the command
     */
    public Optional<Command> getCommand(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    /**
     * Execute a command in the same ExecutionEnvironment as exists for this
     * command.
     *
     * @param command the command to be executed
     */
    public void exec(Command command) {
        exec(command, ee);
    }

    /**
     * Execute a command in a new ExecutionEnvironment.
     *
     * @param command the command to be executed
     * @param newee the ExecutionEnvironment to be used
     */
    public void exec(Command command, ExecutionEnvironment newee) {
        command.setParent(this);
        command.execute(newee);
    }

    /**
     * Execute a command sequence in a new ExecutionEnvironment.
     *
     * @param seq the command sequence
     * @param newee the ExecutionEnvironment to be used
     */
    public void execseq(CommandSequence seq, ExecutionEnvironment newee) {
        seq.stream().forEach(command -> {
            command.setParent(this);
            command.execute(newee);
        });
    }
}
