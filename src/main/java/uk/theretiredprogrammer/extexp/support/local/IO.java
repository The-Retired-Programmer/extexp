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

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import uk.theretiredprogrammer.extexp.support.ExecutionEnvironment;

/**
 * IO -the abstract class from which all IO descriptor are based
 *
 * @author richard linsdale
 * @param T the Class of the Input/Output Object
 */
public abstract class IO<T> implements Closeable {

    private final String parametervalue;
    private final ExecutionEnvironment ee;
    private T ioobj;

    /**
     * Constructor
     *
     * @param ee the execution environment
     * @param parametervalue the parameter value defining the IO parameter
     * @throws IOException if problem
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public IO(ExecutionEnvironment ee, Optional<String> parametervalue)  throws IOException {
        
        this.parametervalue = parametervalue.orElseThrow(()->new IOException("Missing Parameter Value"));
        this.ee = ee;
        ioobj = setup(this.parametervalue, ee);
    }
    
//    /**
//     * Test if IO instance is open (available for input output operations)
//     *
//     * @return true if open
//     */
//    public final boolean isOpen() {
//        return ioobj.isPresent();
//    }

    /**
     * Get the IO value
     *
     * @return the IO value or null if not open
     */
    public final T get() {
        return ioobj;
    }

//    /**
//     * Get the IO value
//     *
//     * @return the IO value
//     */
//    protected final Optional<T> getOptional() {
//        return ioobj;
//    }

    /**
     * Setup the IO for the specfic data direction / type
     *
     * @param pvalue the parameter value
     * @param ee the executionEnvironment
     * @return the IO value
     * @throws IOException if problems
     */
    protected abstract T setup(String pvalue, ExecutionEnvironment ee) throws IOException;

    @Override
    public final void close() throws IOException {
        if (ioobj  != null) {
            T io = ioobj;
            ioobj = null;
            drop(io, ee);
        }
    }

    /**
     * Specific actions required during close.
     *
     * Overwrite this method if the implementing class requires specific closing
     * actions. Otherwise this null method will apply
     *
     * @param io the IO instance
     * @param ee the ExecutionEnvironment
     * @throws IOException if close problems
     */
    protected abstract void drop(T io, ExecutionEnvironment ee) throws IOException;

}
