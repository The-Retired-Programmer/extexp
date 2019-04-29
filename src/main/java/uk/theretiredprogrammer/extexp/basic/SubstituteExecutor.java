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

import java.util.Optional;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOInputString;
import uk.theretiredprogrammer.extexp.support.IOWriter;

/**
 * The SUBSTITUTE executor class.
 *
 * Copy a file from the IOInputString name to IOWriter name, applying the
 * substitution process to the copied text. See
 * {@link uk.theretiredprogrammer.extexp.support.Command#substitute(java.util.Optional, java.util.function.Function)}
 * for details of the substitution process.
 *
 * Requires two or more parameters:
 *
 * 'from' - the name of the input
 *
 * 'to' - the name of the output
 *
 * Other parameters which are required to support the substitution process
 *
 * @author richard linsdale
 */
public class SubstituteExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "SUBSTITUTE";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"from", "to"};
    }

    @Override
    protected void executecommand() {
        IOWriter output = new IOWriter(ee, getParameter("to"));
        if (!output.isOpen()) {
            return;
        }
        IOInputString input = new IOInputString(ee, getParameter("from"));
        if (!input.isOpen()) {
            return;
        }
        //
        substitute(Optional.of(input.get()), (name) -> getParameter(name), output.get());
        //
        output.close();
        input.close();
    }
}
