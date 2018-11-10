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
package uk.theretiredprogrammer.extexp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class BuildExecutor {

    private final Map<String, String> strings = new HashMap<>();
    private final boolean isSimpleExecution;
    private final JsonObject jobj;
    private final BuildStepExecutor stepExecutor;

    public BuildExecutor(FileObject project, FileObject content, FileObject shared, FileObject cache,
            FileObject out, FileObject resources, String relative) throws IOException {
        //
        stepExecutor = new BuildStepExecutor(content, shared, cache, out, resources, relative);
        FileObject buildinstructions = project.getFileObject("build.json");
        if (buildinstructions == null) {
            throw new IOException("Build Instructions (build.json) is missing");
        }
        try (InputStream is = buildinstructions.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            jobj = rdr.readObject();
        }
        JsonValue jval = jobj.get("action");
        if (jval == null) {
            throw new IOException("no action label found at top level of build.json");
        }
        switch (jval.getValueType()) {
            case ARRAY:
                BuildStepExecutor.extractParameters(jobj, strings);
                isSimpleExecution = false;
                break;
            case STRING:
                isSimpleExecution = true;
                break;
            default:
                throw new IOException("action type must be either Array or String in build.json");
        }
    }

    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        if (isSimpleExecution) {
            stepExecutor.extractParams(jobj).execute(msg, err);
        } else {
            for (JsonObject jobjchild : jobj.getJsonArray("action").getValuesAs(JsonObject.class)) {
                stepExecutor.extractParams(jobjchild, strings).execute(msg, err);
            }
        }
    }
}
