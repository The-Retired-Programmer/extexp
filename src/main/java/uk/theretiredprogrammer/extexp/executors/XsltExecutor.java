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

import java.awt.Image;
import uk.theretiredprogrammer.extexp.visualeditor.WidgetData;
import java.io.IOException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openide.util.ImageUtilities;
import org.w3c.dom.Document;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.IOReader;
import uk.theretiredprogrammer.extexp.execution.IOWriter;
import uk.theretiredprogrammer.extexp.visualeditor.PinDef;
import uk.theretiredprogrammer.extexp.visualeditor.palette.CategoryChildren;

/**
 *
 * @author richard
 */
public class XsltExecutor extends Executor {

    @Override
    protected void executecommand() throws IOException {
        IOWriter output = new IOWriter(ee, this.getLocalParameter("to"));
        IOReader input = new IOReader(ee, this.getLocalParameter("from"));
        IOReader stylesheet = new IOReader(ee, this.getLocalParameter("stylesheet"));
        //
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
        //
        output.close();
        input.close();
        stylesheet.close();
    }

    @Override
    public WidgetData getWidgetData() {
        return new XsltExecutorWidgetData();
    }

    private class XsltExecutorWidgetData extends WidgetData {

        private static final String EXECUTORIMAGENAME = "uk/theretiredprogrammer/extexp/visualeditor/arrow_switch.png";

        public XsltExecutorWidgetData() {
            addPinDef("from", new PinDef("from", XsltExecutor.this.getParam("from")));
            addPinDef("stylesheet", new PinDef("stylesheet", XsltExecutor.this.getParam("stylesheet")));
            addPinDef("to", new PinDef("to", XsltExecutor.this.getParam("to")));
            addExtraPinDefs(XsltExecutor.this.getParams(),"Do");
        }

        @Override
        public Image getWidgetImage() {
            return ImageUtilities.loadImage(EXECUTORIMAGENAME);
        }

        @Override
        public String getWidgetImageName() {
            return EXECUTORIMAGENAME;
        }

        @Override
        public CategoryChildren.CategoryType getCategoryType() {
            return CategoryChildren.CategoryType.EXECUTOR;
        }

        @Override
        public String getDisplayName() {
            return "XSLT";
        }
    }
}
