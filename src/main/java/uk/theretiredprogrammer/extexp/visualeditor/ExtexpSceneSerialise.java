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

import java.awt.Point;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 *
 * @author richard
 */
public class ExtexpSceneSerialise {

    private Function<JsonObject, Boolean> outputfunction;

    public void setOutputFunction(Function<JsonObject, Boolean> outputfunction) {
        this.outputfunction = outputfunction;
    }

    // call in AWT to serialize scene
    public void serialize(ExtexpScene scene) {
    }

    // call in AWT to deserialize scene
    public void deserialize(ExtexpScene scene, JsonObject jobj) throws IOException {
        int col = 1;
        boolean runwidget = false;
        for (Entry<String, JsonValue> es : jobj.entrySet()) {
            String name = es.getKey();
            JsonValue content = es.getValue();
            switch (content.getValueType()) {
                case ARRAY:
                    putSerial(scene, col++, name, (JsonArray) content);
                    break;
                case OBJECT:
                    JsonObject jo = (JsonObject) content;
                    if (name.equals("Run")) {
                        putWidget(scene, 0, 0, new RunWidgetData(jo.getString("name", "**missing**")));
                        runwidget = true;
                    } else {
                        throw new IOException("Deserialise - Illegal Json File content (1)");
                    }
                    break;
                case STRING:
                    String val = ((JsonString) content).getString();
                    if (name.equals("Run")) {
                        putWidget(scene, 0, 0, new RunWidgetData(val));
                        runwidget = true;
                    } else {
                        throw new IOException("Deserialise - Illegal Json File content (2)");
                    }
                    break;
                default:
                    throw new IOException("Deserialise - Illegal Json File content (3)");
            }
        }
        if (! runwidget) {
            putWidget(scene, 0, 0, new RunWidgetData("MAIN"));
        }
    }

    private static final int ROWSTEP = 120;
    private static final int COLSTEP = 200;

    private void putSerial(ExtexpScene scene, int col, String name, JsonArray content) throws IOException {
        int row = putWidget(scene, col, 1, new SequenceWidgetData(name));
        for (JsonObject j : content.getValuesAs(JsonObject.class)) {
            String run = j.getString("Run", "");
            if (!run.isEmpty()) {
                row = putWidget(scene, col, row, new RunWidgetData(run));
            } else {
                String action = j.getString("action", "");
                switch (action) {
                    case "markdown-substitute":
                        row = putWidget(scene, col, row, new MarkdownAndSubstituteExecutorWidgetData());
                        break;
                    case "markdown":
                        row = putWidget(scene, col, row, new MarkdownExecutorWidgetData());
                        break;
                    case "copy":
                        row = putWidget(scene, col, row, new CopyExecutorWidgetData());
                        break;
                    case "if-defined":
                        row = putWidget(scene, col, row, new IfDefinedWidgetData());
                        break;
                    case "fop":
                        row = putWidget(scene, col, row, new FopExecutorWidgetData());
                        break;
                    case "create-imageset":
                        row = putWidget(scene, col, row, new ImagesetExecutorWidgetData());
                        break;
                    case "substitute":
                        row = putWidget(scene, col, row, new SubstituteExecutorWidgetData());
                        break;
                    case "xslt":
                        row = putWidget(scene, col, row, new XsltExecutorWidgetData());
                        break;
                    default:
                        throw new IOException("Unknown Json Object action: "+action);
                }
            }
        }
    }

    private int putWidget(ExtexpScene scene, int col, int row, WidgetData widgetdata) {
        int x;
        int y;
//            int x = j.getInt("x", -1);
//            int y = j.getInt("y", -1);
//            if (x == -1) {
        x = COLSTEP * col;
//            }
//            if (y == -1) {
        y = ROWSTEP * row++;
//            }
        scene.insertWidget(widgetdata, new Point(x, y));
        return row;
    }
}
