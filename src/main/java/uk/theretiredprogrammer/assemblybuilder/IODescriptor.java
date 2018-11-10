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
package uk.theretiredprogrammer.assemblybuilder;

/**
 *
 * @author richard
 */
public class IODescriptor <T> {

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
     * @return the optional
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
        INPUTSTRING, READER, WRITER, INPUTPATH, OUTPUTPATH, RESOURCESDESCRIPTOR, PARAMETERDESCRIPTOR
    }
    
    private final String name;
    private final IOREQUIREMENT type;
    private final boolean optional;
    private T value;
    
    public IODescriptor(String name, IOREQUIREMENT type) {
        this.name = name;
        this.type = type;
        this.optional = false;
        this.value = null;
    }
    
    public IODescriptor(IOREQUIREMENT type) {
        this.name = null;
        this.type = type;
        this.optional = false;
        this.value = null;
    }
    
    public IODescriptor(String name, IOREQUIREMENT type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
        this.value = null;
    }
}
