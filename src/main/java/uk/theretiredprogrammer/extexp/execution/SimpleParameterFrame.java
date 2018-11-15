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
public class SimpleParameterFrame extends ParameterFrame {


    public SimpleParameterFrame(JsonObject jobj) {
        this(jobj, null);
    }

    public SimpleParameterFrame(JsonObject jobj, ParameterFrame parent) {
        super(jobj, parent);
    }
    
    @Override
    public void clearStringFileParameters() {
        previous.clearStringFileParameters();
    }

    @Override
    public void setStringFileParameter(String name, String val) throws IOException {
        previous.setStringFileParameter(name, val);
    }
}
