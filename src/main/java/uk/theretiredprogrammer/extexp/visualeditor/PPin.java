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
package uk.theretiredprogrammer.extexp.visualeditor;

import java.util.Map.Entry;
import org.netbeans.api.visual.vmd.VMDPinWidget;

/**
 *
 * @author richard
 */
public class PPin extends VMDPinWidget {

    public static final int REQUIRED = 0;
    public static final int INHERITED = 1;
    public static final int OPTIONAL = 2;

    public PPin(final PScene scene) {
        super(scene);
    }

    public PPin(final PScene scene, String name) {
        super(scene);
        setPinName(name);
    }
    
    public PPin(final PScene scene, String name, String value) {
        this(scene, name, value, 0);
    }

    public PPin(final PScene scene, Entry<String, String> param) {
        this(scene, param.getKey(), param.getValue(), 0);
    }

    public PPin(final PScene scene, String name, String value, int valuetype) {
        this(scene,
                name + ": " + (value != null ? value
                        : valuetype == INHERITED ? "\u00abfrom Run\u00bb"
                                : valuetype == OPTIONAL ? "\u00aboptional - from Run\u00bb"
                                        : "\u00abundefined\u00bb"));
    }
}
