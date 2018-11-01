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
package uk.theretiredprogrammer.websitebuilder;

import java.io.IOException;
import javax.json.JsonObject;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public class BuildCopyFile extends Build {

    private final FileObject file;
    private final Usings usings;

    public BuildCopyFile(JsonObject jobj) throws IOException {
        String filename = jobj.getString("file", "");
        if (filename.equals("")) {
            throw new IOException("Missing file entry");
        }
        file = IoUtil.findFile(filename);
        JsonObject jusing = jobj.getJsonObject("using");
        if (jusing == null) {
            usings = new Usings();
        } else {
            usings = Build.buildUse(jusing);
        }
    }

    @Override
    public String getContentString(Usings parentusings) throws IOException {
        Usings combined = new Usings(parentusings);
        combined.putAll(usings);
        return substitute(file.asText(), combined);
    }

    private String substitute(String text, Usings usings) throws IOException {
        while (true) {
            int p = text.indexOf("${");
            if (p == -1) {
                return text;
            }
            int q = text.indexOf("}", p + 2);
            String name = text.substring(p + 2, q);
            Build build = usings.get(name);
            if (build == null) {
                throw new IOException("No matching named content");
            }
            text = text.substring(0, p) + build.getContentString(usings) + text.substring(q + 1);
        }
    }
} 
