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

/**
 *
 * @author richard
 */
public class IODescriptor<T> {

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public IOREQUIREMENT getType() {
        return type;
    }

    /**
     * @return the optional flag
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.value = value;
    }

    public enum IOREQUIREMENT {
        INPUTSTRING, PARAMSTRING, JSONPARAMSTRING, JSONSTRUCTUREFRAME, JSONSTRUCTURESIMPLEFRAME,
        INPUTRECIPE, OUTPUTRECIPE,
        READER, WRITER,
        INPUTPATH, OUTPUTPATH,
        RESOURCESDESCRIPTOR, PARAMETERDESCRIPTOR
    }

    private final String name;
    private final IOREQUIREMENT type;
    private boolean optional = false;
    private T value = null;

    public IODescriptor(String name, IOREQUIREMENT type) {
        this.name = name;
        this.type = type;
    }

    public IODescriptor(IOREQUIREMENT type) {
        this(null, type);
    }

    public IODescriptor optional() {
        optional = true;
        return this;
    }
}
