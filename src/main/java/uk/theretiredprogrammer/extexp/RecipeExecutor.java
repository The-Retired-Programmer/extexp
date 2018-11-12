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
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class RecipeExecutor {

    public static void execute(FileObject content, FileObject sharedcontent,
            FileObject cache, FileObject out,
            FileObject resources, String relative,
            Map<String, String> recipestore, OutputWriter msg, OutputWriter err,
            Map<String, String> parentstrings, String recipe) throws IOException {
        //
        JsonStructure jobj;
        try (StringReader r = new StringReader(recipe);
                JsonReader rdr = Json.createReader(r)) {
            jobj = rdr.read();
        }
        Map<String, String> strings;
        String srcpath = parentstrings.get("path");
        switch (jobj.getValueType()) {
            case ARRAY:
                strings = new HashMap<>();
                BuildStepExecutor stepExecutor = new BuildStepExecutor(
                        srcpath == null ? content : content.getFileObject(srcpath),
                        srcpath == null ? sharedcontent: (sharedcontent == null? content : sharedcontent) ,
                        srcpath == null ? cache : IoUtil.useOrCreateFolder(cache, srcpath),
                        out, resources, relative, recipestore);
                for (JsonObject jobjchild : ((JsonArray)jobj).getValuesAs(JsonObject.class)) {
                    stepExecutor.extractParams(jobjchild, parentstrings).execute(msg, err);
                }
                break;
            case OBJECT:
                strings = new HashMap<>();
                new BuildStepExecutor(
                        srcpath == null ? content : content.getFileObject(srcpath),
                        srcpath == null ? sharedcontent: (sharedcontent == null? content : sharedcontent) ,
                        srcpath == null ? cache : IoUtil.useOrCreateFolder(cache, srcpath),
                        out, resources, relative,
                        recipestore)
                        .extractParams((JsonObject)jobj, parentstrings).execute(msg, err);
                break;
            default:
                throw new IOException("json object type of recipe is incorrect");
        }
    }
}
