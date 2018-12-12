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
import java.io.InputStream;
import java.util.Map;
import java.util.function.BiFunction;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class BuildFile {

    public static ExecutionEnvironment initAndParse(FileObject projectfolder, OutputWriter msg, OutputWriter err) throws IOException {
        IOPaths paths = new IOPaths(
                projectfolder,
                projectfolder.getFileObject("src"),
                IoUtil.useOrCreateFolder(projectfolder, "cache"),
                IoUtil.useOrCreateFolder(projectfolder, "output"),
                IoUtil.useOrCreateFolder(projectfolder, "output", "resources"),
                "resources",
                msg,
                err
        );
        FileObject buildinstructions = paths.getProjectfolder().getFileObject("build.json");
        if (buildinstructions == null) {
            throw new IOException("Build Instructions (build.json) is missing");
        }
        JsonObject jobj;
        try (InputStream is = buildinstructions.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            jobj = rdr.readObject();
        }
        CommandSequenceStore commandsequencestore = new CommandSequenceStore();
        String parseresult = BuildFile.parse(jobj,
                (name, sequence) -> insertSequence(commandsequencestore, name, sequence));
        if (!parseresult.isEmpty()) {
            throw new IOException(parseresult);
        }
        CommandSequence commandsequence = commandsequencestore.getSequence("MAIN");
        if (commandsequence == null) {
            throw new IOException("Command sequence \"MAIN\" missing");
        }
        return ExecutionEnvironment.create(paths, commandsequencestore);
    }

    private static String insertSequence(CommandSequenceStore commandsequencestore, String name, JsonArray sequence) {
        commandsequencestore.addSequence(name, sequence);
        return "";
    }

    private static String parse(JsonObject jobj, BiFunction<String, JsonArray, String> sequencehandler) {
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
                    return "parsing - Illegal Json File content";
            }
        }
        return "";
    }
}
