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
package uk.theretiredprogrammer.assemblybuilder;

import java.io.IOException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public abstract class Build {

    public abstract String getContentString(Usings parentusings) throws IOException;

    static Usings buildUse(JsonObject jusings) throws IOException {
        Usings res = new Usings();
        for (String name : jusings.keySet()) {
            JsonValue jval = jusings.getOrDefault(name, null);
            if (jval == null) {
                throw new IOException("Json key lookup problem - syserr");
            }
            res.put(name, buildAction(jval));
        }
        return res;
    }

    static Build buildAction(JsonValue jval) throws IOException {
        switch (jval.getValueType()) {
            case OBJECT:
                JsonObject jobj = ((JsonObject) jval);
                String action = jobj.getString("action", "copy-file");
                if (action.equals("copy-file")) {
                    return new BuildCopyFile(jobj);
                }
                if (action.equals("nothing")) {
                    return new BuildEmpty();
                }
                if (action.equals("markdown")) {
                    return new BuildMarkDownFile(jobj);
                }
                if (action.equals("imageset")) {
                    return new BuildImageSet(jobj, resourcefolder, resourcefolderpath);
                }
                throw new IOException("Unknown action defined");
            case ARRAY:
                throw new IOException("Array content not allowed");
            case STRING:
                String value = ((JsonString) jval).getString();
                return new BuildString(value);
            case NUMBER:
                JsonNumber jnum = (JsonNumber) jval;
                if (jnum.isIntegral()) {
                    return new BuildString(Integer.toString(jnum.intValueExact()));
                } else {
                    return new BuildString(jnum.toString());
                }
            default:
                return new BuildString(jval.toString());
        }
    }

    static void setResourseFolder(FileObject resourcefolder, String resourcefolderpath) {
        Build.resourcefolder = resourcefolder;
        Build.resourcefolderpath = resourcefolderpath;
    }

    static void setFolderSeachOrder(FileObject... fos) {
        Build.fos = fos;
    }

    public static FileObject[] getFos() {
        return fos;
    }

    private static FileObject[] fos;
    private static FileObject resourcefolder;
    private static String resourcefolderpath;
}