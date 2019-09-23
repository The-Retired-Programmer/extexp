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
package uk.theretiredprogrammer.extexp.basic;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOFactory;

/**
 * The COPY executor class.
 *
 * Copy a file.
 *
 * Requires two parameters:
 *
 * 'from' - the name of the input
 *
 * 'to' - the name of the output
 *
 * @author richard linsdale
 */
public class CopyExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "COPY";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"from", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (Writer output = IOFactory.createWriter(ee, getParameter("to"));
                Reader input = IOFactory.createReader(ee, getParameter("from"))) {
            input.transferTo(output);
        }
    }
}
