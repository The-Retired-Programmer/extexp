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
import javax.json.JsonObject;

/**
 *
 * @author richard
 */
public class ControlFactory {

    public static final Control create(JsonObject jobj) throws IOException {
        String runname = jobj.getString("Run", "");
        if (!runname.isEmpty()) {
            RunControl rc = new RunControl();
            rc.parse(jobj);
            return rc;
        }
        String usename = jobj.getString("Use", "");
        if (!usename.isEmpty()) {
            UseControl uc = new UseControl();
            uc.parse(jobj);
            return uc;
        }
        String copyresourcesname = jobj.getString("Copy-resources", "");
        if (!copyresourcesname.isEmpty()) {
            CopyResourcesControl crc = new CopyResourcesControl();
            crc.parse(jobj);
            return crc;
        }
        String ifdefinedname = jobj.getString("If-defined", "");
        if (!ifdefinedname.isEmpty()) {
            IfDefinedControl idc = new IfDefinedControl();
            idc.parse(jobj);
            return idc;
        }
        return null;
    }
}
