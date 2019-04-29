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
package uk.theretiredprogrammer.extexp.visualeditor;

import org.netbeans.api.visual.vmd.VMDPinWidget;

/**
 * The Pin class for the visual editor
 * 
 * @author richard linsdale
 */
public class PPin extends VMDPinWidget {

    /**
     * Constructor
     * 
     * @param scene the visual editor scene 
     */
    public PPin(final PScene scene) {
        super(scene);
    }

    /**
     * Constructor
     * 
     * @param scene the visual editor scene
     * @param name the pin name
     */
    public PPin(final PScene scene, String name) {
        super(scene);
        setPinName(name);
    }

    /**
     * Constructor
     * 
     * @param scene the visual editor scene
     * @param name the pin name
     * @param value the value associated with this pin
     */
    public PPin(final PScene scene, String name, String value) {
        this(scene, name + ": " + value);
    }
}
