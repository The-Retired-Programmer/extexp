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
package uk.theretiredprogrammer.extexp.support.local;

import java.util.Optional;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;

/**
 * IO -the abstract class from which all IO descriptor are based
 *
 * @author richard linsdale
 * @param T the Class of the Input/Output Object
 */
public abstract class IO<T> {

    private final Optional<String> parametervalue;
    private final ExecutionEnvironment ee;
    private final Optional<T> ioobj;

    /**
     * Constructor
     *
     * @param ee the execution environemnt
     * @param parametervalue the parameter value defining the IO parameter
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public IO(ExecutionEnvironment ee, Optional<String> parametervalue) {
        this.parametervalue = parametervalue;
        this.ee = ee;
        ioobj = parametervalue.isPresent() ? setup(parametervalue.get(), ee) : Optional.empty();
    }

    /**
     * Test if IO instance is open (available for input output operations)
     *
     * @return true if open
     */
    public final boolean isOpen() {
        return ioobj.isPresent();
    }

    /**
     * Get the IO value
     *
     * @return the IO value or null if not open
     */
    public final T get() {
        return ioobj.orElse(null);
    }

    /**
     * Get the IO value
     *
     * @return the IO value
     */
    protected final Optional<T> getOptional() {
        return ioobj;
    }

    /**
     * Setup the IO for the specfic data direction / type
     *
     * @param pvalue the parameter value
     * @param ee the executionEnvironment
     * @return the IO value
     */
    protected abstract Optional<T> setup(String pvalue, ExecutionEnvironment ee);

    /**
     * Close this IO
     *
     */
    public final void close() {
        ioobj.ifPresent((p -> drop(p, ee)));
    }

    /**
     * Specific actions required during close.
     *
     * Overwrite this method if the implementing class requires specific closing
     * actions. Otherwise this null method will apply
     *
     * @param io the IO instance
     * @param ee the ExecutionEnvironment
     * @return true if drop completed without problem
     */
    protected boolean drop(T io, ExecutionEnvironment ee) {
        return true;
    }

}
