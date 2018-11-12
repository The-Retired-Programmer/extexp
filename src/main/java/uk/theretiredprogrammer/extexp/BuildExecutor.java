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

    public static void execute(FileObject project, FileObject content,
            FileObject cache, FileObject out,
            FileObject resources, String relative, OutputWriter msg, OutputWriter err) throws IOException {
        //
        Map<String, String> recipestore = new HashMap<>();
        FileObject buildinstructions = project.getFileObject("build.json");
        if (buildinstructions == null) {
            throw new IOException("Build Instructions (build.json) is missing");
        }
        JsonObject jobj;
        try (InputStream is = buildinstructions.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            jobj = rdr.readObject();
        }
        JsonValue jval = jobj.get("action");
        if (jval == null) {
            throw new IOException("no action label found at top level of build.json");
        }
        Map<String, String> strings;
        String srcpath;
        switch (jval.getValueType()) {
            case ARRAY:
                strings = new HashMap<>();
                BuildStepExecutor.extractParameters(jobj, strings);
                srcpath = strings.get("path");
                BuildStepExecutor stepExecutor = new BuildStepExecutor(
                        srcpath == null ? content : content.getFileObject(srcpath),
                        srcpath == null ? null : content,
                        srcpath == null ? cache : IoUtil.useOrCreateFolder(cache, srcpath),
                        out, resources, relative,
                        recipestore);
                for (JsonObject jobjchild : jobj.getJsonArray("action").getValuesAs(JsonObject.class)) {
                    stepExecutor.extractParams(jobjchild, strings).execute(msg, err);
                }
                break;
            case OBJECT:
                strings = new HashMap<>();
                BuildStepExecutor.extractParameters(jobj, strings);
                srcpath = strings.get("path");
                new BuildStepExecutor(
                        srcpath == null ? content : content.getFileObject(srcpath),
                        srcpath == null ? null : content,
                        srcpath == null ? cache : IoUtil.useOrCreateFolder(cache, srcpath),
                        out, resources, relative,
                        recipestore)
                        .extractParams(jobj.getJsonObject("action"), strings).execute(msg, err);
                break;
            case STRING:
                new BuildStepExecutor(content, null, cache, out, resources, relative,recipestore)
                        .extractParams(jobj).execute(msg, err);
                break;
            default:
                throw new IOException("action type must be either Array or String in build.json");
        }
    }
}
