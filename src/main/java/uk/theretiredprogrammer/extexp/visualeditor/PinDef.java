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

import java.awt.Color;
import uk.theretiredprogrammer.extexp.execution.ExtexpPinWidget;

/**
 *
 * @author richard
 */
public class PinDef {

    public static final int REQUIRED = 0;
    public static final int INHERITED = 1;
    public static final int OPTIONAL = 2;

    private final String name;
    private final Color foreground;

    public PinDef(String name) {
        this.name = name;
        foreground = Color.BLACK;
    }

    public PinDef(String name, String value) {
        this(name, value, 0);
    }

    public PinDef(String name, String value, int valuetype) {
        if (value != null) {
            this.name = name + ": " + value;
            foreground = Color.BLACK;
            return;
        }
        switch (valuetype) {
            case INHERITED:
                foreground = Color.ORANGE;
                this.name = name + ": \u00abfrom Run\u00bb";
                break;
            case OPTIONAL:
                foreground = Color.BLACK;
                this.name = name + ": \u00aboptional - from Run\u00bb";
                break;
            default: // case REQUIRED or any other unknown valuetypes
                foreground = Color.CYAN;
                this.name = name + ": \u00abundefined\u00bb";
        }
    }

    public void configPin(ExtexpPinWidget pin) {
        pin.setForeground(foreground);
        pin.setPinName(name);
    }
}
