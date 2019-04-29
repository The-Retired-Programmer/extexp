/*
 * Copyright 2018-2019 Richard Linsdale.
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
package uk.theretiredprogrammer.extexp.support;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import javax.json.JsonObject;
import org.openide.util.Lookup;
import uk.theretiredprogrammer.extexp.api.ControlFactory;
import uk.theretiredprogrammer.extexp.api.ExecutorFactory;

/**
 *  Command Factory
 * 
 *  A factory class for all Commands (Executors and Controls)
 *
 * @author Richard Linsdale
 */
public class CommandFactory {

    private static Collection<? extends ControlFactory> controlfactories = null;

    private static Collection<? extends ExecutorFactory> executorfactories = null;

    /**
     * clear the factories cache
     */
    public static void init() {
        controlfactories = null;
        executorfactories = null;
    }

    /**
     * Run a requested function against all controls
     *
     * @param consumer the requested function (Consumer of Control)
     */
    public static void consumeAllControls(Consumer<Control> consumer) {
        if (controlfactories == null) {
            controlfactories = Lookup.getDefault().lookupAll(ControlFactory.class);
        }
        controlfactories.forEach((factory) -> {
            factory.consumeAllCommands(consumer);
        });
    }

    /**
     * Run a requested function against all executors
     *
     * @param consumer the requested function (Consumer of Executor)
     */
    public static void consumeAllExecutors(Consumer<Executor> consumer) {
        if (executorfactories == null) {
            executorfactories = Lookup.getDefault().lookupAll(ExecutorFactory.class);
        }
        executorfactories.forEach((factory) -> {
            factory.consumeAllExecutors(consumer);
        });
    }

    /**
     * Create a new Instance of the requested Command (Control or Exector).
     *
     * @param jobj the json object representing this command (the parameters) 
     * @return the requested Command
     */
    public static Optional<? extends Command> create(JsonObject jobj) {
        if (controlfactories == null) {
            controlfactories = Lookup.getDefault().lookupAll(ControlFactory.class);
        }
        Optional<Control> ctl = controlfactories.stream()
                .map(f -> f.create(jobj.keySet()))
                .filter(c -> c != null)
                .peek(c -> c.parse(jobj))
                .findFirst();
        if (ctl.isPresent()) {
            return ctl;
        }
        if (executorfactories == null) {
            executorfactories = Lookup.getDefault().lookupAll(ExecutorFactory.class);
        }
        return executorfactories.stream()
                .map(f -> f.create(jobj.getString("Do", "")))
                .filter(c -> c != null)
                .peek(c -> c.parse(jobj))
                .findFirst();
    }
}
