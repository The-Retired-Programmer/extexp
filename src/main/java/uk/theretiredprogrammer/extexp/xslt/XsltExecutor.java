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
package uk.theretiredprogrammer.extexp.xslt;

import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import uk.theretiredprogrammer.extexp.support.Executor;
import uk.theretiredprogrammer.extexp.support.IOReader;
import uk.theretiredprogrammer.extexp.support.IOWriter;

/**
 * The XSLT executor class.
 *
 * Executes a XSLT transform from the named IOReader to the named IOWriter,
 * using a stylesheet from a named IOReader.
 *
 * Requires three parameters:
 *
 * 'from' - the name of the xml input
 *
 * 'stylesheet' - the name of the xslt stylesheet input
 *
 * 'to' - the name of the xml output
 *
 * @author richard linsdale
 */
public class XsltExecutor extends Executor {

    @Override
    public String getDisplayName() {
        return "XSLT";
    }

    @Override
    public String[] getPrimaryPinData() {
        return new String[]{"from", "stylesheet", "to"};
    }

    @Override
    protected void executecommand() throws IOException {
        try (
                IOWriter output = new IOWriter(ee, getParameter("to"));
                IOReader input = new IOReader(ee, getParameter("from"));
                IOReader stylesheet = new IOReader(ee, getParameter("stylesheet"))) {
            try {
                Transformer tr;
                tr = TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheet.get()));
                DOMResult dr = new DOMResult();
                tr.transform(new StreamSource(input.get()), dr);
                //
                tr = TransformerFactory.newInstance().newTransformer();
                tr.transform(new DOMSource((Document) dr.getNode()), new StreamResult(output.get()));
            } catch (TransformerException ex) {
                throw new IOException(ex);
            }
        }
    }
}
