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
package uk.theretiredprogrammer.extexp.execution.impl;

import java.io.IOException;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.extexp.execution.ControlFactory;

/**
 *
 * @author richard
 */
@ServiceProvider(service = ControlFactory.class)
public class BaseControlFactory implements ControlFactory {

    @Override
    public Control create(Set<String> keys) throws IOException {
        if (keys.contains("Run")) {
            return new RunControl();
        }
        if (keys.contains("Use")) {
            return new UseControl();
        }
        if (keys.contains("If-defined")) {
            return new IfDefinedControl();
        }
        return null;
    }
}
