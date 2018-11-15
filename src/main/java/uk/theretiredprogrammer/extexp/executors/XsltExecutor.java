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
package uk.theretiredprogrammer.extexp.executors;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.windows.OutputWriter;
import org.w3c.dom.Document;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IODescriptor;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.READER;
import static uk.theretiredprogrammer.extexp.execution.IODescriptor.IOREQUIREMENT.WRITER;

/**
 *
 * @author richard
 */
public class XsltExecutor extends Executor {

    private final IODescriptor<Reader> input = new IODescriptor<>("from", READER);
    private final IODescriptor<Reader> stylesheet = new IODescriptor<>("stylesheet", READER);
    private final IODescriptor<Writer> output = new IODescriptor<>("to", WRITER);

    @Override
    public IODescriptor[] getIODescriptors() {
        return new IODescriptor[]{input, stylesheet, output};
    }

    @Override
    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        try {
            Transformer tr;
            tr = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet.getValue()));
            DOMResult dr = new DOMResult();
            tr.transform(new StreamSource(input.getValue()), dr);
            //
            tr = TransformerFactory.newInstance().newTransformer();
            tr.transform(new DOMSource((Document) dr.getNode()), new StreamResult(output.getValue()));
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }
}
