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
import uk.theretiredprogrammer.extexp.execution.IOPaths;
import uk.theretiredprogrammer.extexp.execution.IOReader;
import uk.theretiredprogrammer.extexp.execution.TemporaryFileStore;
import uk.theretiredprogrammer.extexp.execution.IOWriter;

/**
 *
 * @author richard
 */
public class XsltExecutor extends Executor {

    @Override
    public void execute(OutputWriter msg, OutputWriter err, IOPaths paths, TemporaryFileStore tempfs) throws IOException {
        IOWriter output = new IOWriter(this.getLocalParameter("to", paths, tempfs));
        IOReader input = new IOReader(this.getLocalParameter("from", paths, tempfs));
        IOReader stylesheet = new IOReader(this.getLocalParameter("stylesheet", paths, tempfs));
        //
        try {
            Transformer tr;
            tr = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet.get(paths, tempfs)));
            DOMResult dr = new DOMResult();
            tr.transform(new StreamSource(input.get(paths, tempfs)), dr);
            //
            tr = TransformerFactory.newInstance().newTransformer();
            tr.transform(new DOMSource((Document) dr.getNode()), new StreamResult(output.get(paths, tempfs)));
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
        //
        output.close(paths, tempfs);
        input.close(paths, tempfs);
        stylesheet.close(paths, tempfs);
    }
}
