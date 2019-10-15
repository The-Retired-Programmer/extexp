/*
 * Copyright 2019 richard.
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
package uk.theretiredprogrammer.extexp.support;

import java.io.IOException;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 *
 * @author richard
 */
public class ControlTest {

    @Test
    @Order(1)
    @DisplayName("FileGroup creation (parsing JSONARRAY)")
    void testFileGroupCreation(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
        Control instance = new ControlImpl();
        assertNotNull(instance);
        JsonObject json = createJson();
        assertNotNull(json);
        instance.parse(json);
        Map<String, String> res = instance.getFileGroup("UNKNOWN");
        assertEquals(res.size(), 0);
        res = instance.getFileGroup("Testing_name");
        assertEquals(res.size(), 5);
        assertEquals(res.get("swimmingimage"), "index-swimming.html");
    }

    private JsonObject createJson() {
        JsonObject object = Json.createObjectBuilder()
                .add("Testing_name",
                        Json.createArrayBuilder()
                                .add("index-gym.html->gymimage")
                                .add("index-classes.html->classesimage")
                                .add("index-swimming.html->swimmingimage")
                                .add("index-tennis.html->tennisimage")
                                .add("index-badminton.html->badmintonimage")
                )
                .build();
        return object;
    }

    class ControlImpl extends Control {

        @Override
        public String[] getPrimaryPinData() {
            return null;
        }

        @Override
        public String getWidgetImageName() {
            return "UNIT TEST";
        }

        @Override
        public String getDisplayName() {
            return "Unit Test";
        }

        @Override
        protected void executecommand() throws IOException {
            // null action;
        }
    }
}
