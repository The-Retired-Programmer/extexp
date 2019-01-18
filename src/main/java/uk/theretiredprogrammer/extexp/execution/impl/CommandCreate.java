/*
 * Copyright 2014-2018 Richard Linsdale.
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
package uk.theretiredprogrammer.extexp.execution.impl;

import java.io.IOException;
import java.util.Collection;
import javax.json.JsonObject;
import org.openide.util.Lookup;

/**
 * The Create a new Command Object.
 *
 * @author Richard Linsdale (richard at theretiredprogrammer.uk)
 */
public class CommandCreate {
    
    public static Collection<? extends CommandFactory> factories = null;
    
    public static void init() {
        factories = null;
    }

    /**
     * Create a new Instance of the target class.
     *
     * @param jobj the json object describing the command (name and parameters)
     * @return a new instance of the requested command
     */
    public static Command newCommand(JsonObject jobj) throws IOException {
        if (factories == null) {
            factories = Lookup.getDefault().lookupAll(CommandFactory.class);
        }
        for (CommandFactory factory : factories) {
            Command cmd = factory.create(jobj);
            if (cmd != null) {
                return cmd;
            }
        }
        return null;
    }
}
