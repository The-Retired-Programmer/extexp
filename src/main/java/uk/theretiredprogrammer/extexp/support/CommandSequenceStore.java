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
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;

/**
 * CommandSequenceStore - A store of a set of {@link CommandSequence}s, each
 * indexed by a unique sequence name.
 *
 * @author richard linsdale
 */
public class CommandSequenceStore {

    private final Map<String, CommandSequence> commandsequences = new LinkedHashMap<>();

    /**
     * Create a initial Execution Environment for the project Build Instructions
     *
     * @param buildfile the build file
     * @param errout A Consumer to process any error message
     * @throws IOException if bad build file structure
     */
    public CommandSequenceStore(FileObject buildfile, Consumer<String> errout) throws IOException {
        if (!loadbuildfile(buildfile, errout)) {
            throw new IOException("error - when attempting to load build file");
        }
        if (!commandsequences.containsKey("MAIN")) {
            errout.accept("Command sequence \"MAIN\" missing");
            throw new IOException("Command sequence \"MAIN\" missing");
        }
    }

    private boolean loadbuildfile(FileObject buildfile, Consumer<String> errout) {
        if (buildfile == null) {
            errout.accept("Build File missing");
            return false;
        }
        JsonObject jobj;
        try (InputStream is = buildfile.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            jobj = rdr.readObject();
        } catch (IOException ex) {
            errout.accept("Error while reading Build Instructions (" + buildfile.getName() + "): " + ex.getLocalizedMessage());
            return false;
        }
        FileObject buildfolder = buildfile.getParent();
        int errorcount = 0;
        for (Map.Entry<String, JsonValue> es : jobj.entrySet()) {
            String name = es.getKey();
            JsonValue content = es.getValue();
            switch (content.getValueType()) {
                case ARRAY:
                    errorcount += addSequence(name, (JsonArray) content, errout);
                    break;
                case STRING:
                    if ("Include".equals(name)) {
                        String fn = "_" + ((JsonString) content).getString() + ".json";
                        if (!loadbuildfile(buildfolder.getFileObject(fn), errout)) {
                            errorcount++;
                        }
                    } else {
                        errout.accept("Bad Statement:" + name + ": " + content.toString());
                        errorcount++;
                    }
                    break;
                default:
                    errout.accept("Bad Statement:" + name + ": " + content.toString());
                    errorcount++;
            }
        }
        return errorcount == 0;
    }

    private int addSequence(String name, JsonArray sequence, Consumer<String> errout) {
        int errorcount = 0;
        CommandSequence res = new CommandSequence();
        for (JsonObject jobj : sequence.getValuesAs(JsonObject.class)) {
            if (!insertCommand(CommandFactory.create(jobj), res, errout, jobj)) {
                errorcount++;
            }
        }
        commandsequences.put(name, res);
        return errorcount;
    }

    private boolean insertCommand(Optional<? extends Command> command, CommandSequence seq,
            Consumer<String> errout, JsonObject jobj) {
        if (command.isPresent()) {
            seq.add(command.get());
            return true;
        } else {
            errout.accept("Bad Command: " + jobj.toString());
            return false;
        }
    }

    /**
     * Get the named command sequence
     *
     * @param name the command sequence name
     * @return the commandsequence
     */
    public CommandSequence getSequence(Optional<String> name) {
        return name.map(n -> commandsequences.get(n)).orElse(null);
    }

    /**
     * Get the named command sequence.
     *
     * @param name the command sequence name
     * @return the commandsequence
     */
    public CommandSequence getSequence(String name) {
        return commandsequences.get(name);
    }

    /**
     * Apply the supplied function to all command sequences.
     *
     * @param commandsequenceprocessor the function to apply (Consumer of Named
     * CommandSequence)
     */
    public void executeEach(Consumer<Entry<String, CommandSequence>> commandsequenceprocessor) {
        commandsequences.entrySet().stream().forEach((es) -> commandsequenceprocessor.accept(es));
    }
}
