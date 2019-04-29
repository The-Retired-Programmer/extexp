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
package uk.theretiredprogrammer.extexp.freemarker;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richard
 */
public class FreeMarkerExecutorTest {

    public FreeMarkerExecutorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void freemarkerrunTest() {
        Writer w = new OutputStreamWriter(System.out);
        String infn = "/Users/richard/NetBeansProjects/tests/test.template";
        Map<String, String> model = new HashMap<>();
        model.put("insert", "Hello World");
        FreeMarkerExecutor fm = new FreeMarkerExecutor("test case 1", "/");
        fm.freemarkerrun((s) -> System.out.println(s), infn, w, model);
    }
}
