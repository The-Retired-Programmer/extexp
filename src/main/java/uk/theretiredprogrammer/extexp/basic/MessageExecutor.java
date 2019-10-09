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

/**
 * The MESSAGE executor class.
 *
 * Writes a message to the output window.
 *
 * Requires one parameter:
 *
 * 'text' - the message to be written.
 *
 * @author richard linsdale
 */
public class MessageExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "MESSAGE";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"text"};
    }

    @Override
    protected void executecommand() throws IOException {
        ee.println(this.getParameter("text").get());
    }
}
