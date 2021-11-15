/*
 * Copyright 2018-2021 richard linsdale.
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
package uk.theretiredprogrammer.extexp.controls;

import java.util.Set;
import java.util.function.Consumer;
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.extexp.api.ControlFactory;
import uk.theretiredprogrammer.extexp.support.Control;

/**
 * The Factory creating Controls. These include: IfDefined, Run, Use, For and ForList.
 *
 * @author richard linsdale
 */
@ServiceProvider(service = ControlFactory.class)
public class ControlsFactory implements ControlFactory {

    @Override
    public Control create(Set<String> keys) {
        if (keys.contains("Run")) {
            return new RunControl();
        }
        if (keys.contains("Use")) {
            return new UseControl();
        }
        if (keys.contains("If-defined")) {
            return new IfDefinedControl();
        }
        if (keys.contains("For")) {
            return new ForControl();
        }
        if (keys.contains("ForList")) {
            return new ForListControl();
        }
        return null;
    }

    @Override
    public void consumeAllCommands(Consumer<Control> consumer) {
        consumer.accept(new RunControl());
        consumer.accept(new UseControl());
        consumer.accept(new IfDefinedControl());
        consumer.accept(new ForControl());
        consumer.accept(new ForListControl());
    }
}
