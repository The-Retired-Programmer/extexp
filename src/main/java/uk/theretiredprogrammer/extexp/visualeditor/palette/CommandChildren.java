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
package uk.theretiredprogrammer.extexp.visualeditor.palette;

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import uk.theretiredprogrammer.extexp.support.CommandFactory;

/**
 * The Command Children Palette Class
 * 
 * @author richard linsdale
 */
public class CommandChildren  extends Index.ArrayChildren {

    private final Category category;

    /**
     * Constructor
     * 
     * @param category the Command palette category
     */
    public CommandChildren(Category category) {
        this.category = category;
    }

    @Override
    protected List initCollection() {
        List<CommandNode> childrenNodes = new ArrayList<>();
        switch (category.getType()) {
            case CONTROL:
                CommandFactory.consumeAllControls((control)-> childrenNodes.add(new CommandNode(control)));
                break;
            case EXECUTOR:
                CommandFactory.consumeAllExecutors((executor)-> childrenNodes.add(new CommandNode(executor)));
        }
        return childrenNodes;
    }
}
