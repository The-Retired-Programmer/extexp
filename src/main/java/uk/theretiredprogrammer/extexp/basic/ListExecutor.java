/*
 * Copyright 2019 richard linsdale.
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
package uk.theretiredprogrammer.extexp.basic;

import java.io.IOException;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputString;

/**
 * The LIST executor class.
 *
 * Lists a file to the output window.
 *
 * Requires two parameters:
 *
 * 'title' - the title which is prepended to the output.
 *
 * 'from' - the name of the IOInputString.
 *
 * @author richard linsdale
 */
public class ListExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "LIST";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"title", "from"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (
                IOInputString input = new IOInputString(ee, this.getParameter("from"));
                IOInputString title = new IOInputString(ee, this.getParameter("title"))) {
            ee.println("======================================");
            ee.println(title.get());
            ee.println("======================================");
            ee.println(input.get());
            ee.println("======================================");
        }
    }
}
