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
package uk.theretiredprogrammer.extexp.execution;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import uk.theretiredprogrammer.extexp.execution.impl.IO;
import uk.theretiredprogrammer.extexp.execution.impl.IoUtil;

/**
 *
 * @author richard
 */
public class IOWriter extends IO<Writer> {

    private Writer writer;
    private String tempfilename;
    private boolean append;

    public IOWriter(ExecutionEnvironment ee, String parametervalue) {
        super(ee, parametervalue);
    }

    @Override
    protected Writer setup() throws IOException {
        if (parametervalue.startsWith("!")) {
            writer = new StringWriter();
            tempfilename = parametervalue.substring(1);
            append = false;
        } else if (parametervalue.startsWith("+")) {
            writer = new StringWriter();
            tempfilename = parametervalue.substring(1);
            append = true;
        } else {
            writer = new BufferedWriter(new OutputStreamWriter(IoUtil.getOutputStream(ee.paths.getOutfolder(), parametervalue)));
        }
        return writer;
    }

    @Override
    protected void drop() throws IOException {
        writer.close();
        if (writer instanceof StringWriter) {
            String previous = ee.tempfs.get(tempfilename);
            if (append && previous != null) {
                ee.tempfs.put(tempfilename, previous+((StringWriter) writer).toString());
            } else {
                ee.tempfs.put(tempfilename, ((StringWriter) writer).toString());
            }
        }
    }
}
