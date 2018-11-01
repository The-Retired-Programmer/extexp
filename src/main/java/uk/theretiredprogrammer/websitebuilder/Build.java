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
import java.util.ArrayList;
import java.util.List;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public abstract class Build {

    public abstract String getContentString() throws IOException;

    static List<Using> buildUse(JsonObject jusings) throws IOException {
        List<Using> res = new ArrayList<>();
        for (String name : jusings.keySet()) {
            JsonValue jval = jusings.getOrDefault(name, null);
            if (jval == null) {
                throw new IOException("Json key lookup problem - syserr");
            }
            res.add(new Using(name, buildAction(jval)));
        }
        return res;
    }

    static Build buildAction(JsonValue jval) throws IOException {
        if (jval.getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject jobj = ((JsonObject) jval);
            String action = jobj.getString("action", "copy-file");
            if (action.equals("copy-file")) {
                return new BuildCopyFile(jobj);
            } else {
                throw new IOException("Unknown action defined");
            }
        } else {
            String value = ((JsonString) jval).getString();
            return new BuildString(value);
        }
    }

    static void setFolderSeachOrder(FileObject... fos) {
        Build.fos = fos;
    }

    public static FileObject[] getFos() {
        return fos;
    }

    private static FileObject[] fos;
}
