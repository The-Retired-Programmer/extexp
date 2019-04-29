/*
 * Copyright 2018-2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.api;

import java.util.Set;
import java.util.function.Consumer;
import uk.theretiredprogrammer.extexp.support.Control;

/**
 * Factory for creation and processing of Control instances
 *
 * @author richard linsdale
 */
public interface ControlFactory {

    /**
     * Create a Control.
     *
     * @param keys the set of user defined keys. One of these keys being the
     * control name
     * @return the request Control instance or null (if request cannot be
     * decoded)
     */
    public Control create(Set<String> keys);

    /**
     * Run a requested function against all controls managed by this factory
     *
     * @param consumer the requested function (Consumer of Control)
     */
    public void consumeAllCommands(Consumer<Control> consumer);
}
