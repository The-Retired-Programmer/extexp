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

/**
 *
 * @author richard
 */
public abstract class IO<T> {

    final String parametervalue;
    protected final ExecutionEnvironment ee;

    public IO(ExecutionEnvironment ee, String parametervalue) {
        this.parametervalue = parametervalue;
        this.ee = ee;
    }

    public final T get() throws IOException {
        return parametervalue == null ? null : setup();
    }

    abstract T setup() throws IOException;

    public final void close() throws IOException {
        if (parametervalue != null) {
            drop();
        }
    }

    void drop() throws IOException {
        // overwrite if closing activities required
    }

}
