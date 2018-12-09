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
package uk.theretiredprogrammer.extexp.visualeditor.palette;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Action;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;
import uk.theretiredprogrammer.extexp.execution.Command;
import static uk.theretiredprogrammer.extexp.execution.Command.DATA_FLAVOR_COMMAND;

/**
 *
 * @author richard
 */
public class PaletteSupport {

    public static PaletteController createPalette() {
        AbstractNode paletteRoot = new AbstractNode(new CategoryChildren());
        paletteRoot.setName("Palette Root");
        return PaletteFactory.createPalette(paletteRoot, new MyActions(), null, new MyDnDHandler());
    }

    private static class MyActions extends PaletteActions {

        @Override
        public Action[] getImportActions() {
            return null;
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return null;
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action[] getCustomItemActions(Lookup lookup) {
            return null;
        }

        @Override
        public Action getPreferredAction(Lookup lookup) {
            return null;
        }
    }

    private static class MyDnDHandler extends DragAndDropHandler {

        @Override
        public void customize(ExTransferable exTransferable, Lookup lookup) {
            Command command = lookup.lookup(Command.class);
            exTransferable.put(new ExTransferable.Single(DATA_FLAVOR_COMMAND) {

                @Override
                protected Command getData() throws IOException, UnsupportedFlavorException {
                    return command;
                }
            });
        }
    }
}
