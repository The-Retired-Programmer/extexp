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

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import uk.theretiredprogrammer.extexp.execution.impl.IfDefinedControl;
import uk.theretiredprogrammer.extexp.execution.impl.RunControl;
import uk.theretiredprogrammer.extexp.executors.CopyExecutor;
import uk.theretiredprogrammer.extexp.executors.FopExecutor;
import uk.theretiredprogrammer.extexp.executors.ImagesetExecutor;
import uk.theretiredprogrammer.extexp.executors.MarkdownAndSubstituteExecutor;
import uk.theretiredprogrammer.extexp.executors.MarkdownExecutor;
import uk.theretiredprogrammer.extexp.executors.SubstituteExecutor;
import uk.theretiredprogrammer.extexp.executors.XsltExecutor;

/**
 *
 * @author richard
 */
public class CommandChildren  extends Index.ArrayChildren {

    private final Category category;

    public CommandChildren(Category category) {
        this.category = category;
    }

    @Override
    protected List initCollection() {
        List<CommandNode> childrenNodes = new ArrayList<>();
        switch (category.getType()) {
            case CONTROL:
                childrenNodes.add(new CommandNode(new IfDefinedControl()));
                childrenNodes.add(new CommandNode(new RunControl()));
                break;
            case EXECUTOR:
                childrenNodes.add(new CommandNode(new CopyExecutor()));
                childrenNodes.add(new CommandNode(new FopExecutor()));
                childrenNodes.add(new CommandNode(new ImagesetExecutor()));
                childrenNodes.add(new CommandNode(new MarkdownAndSubstituteExecutor()));
                childrenNodes.add(new CommandNode(new MarkdownExecutor()));
                childrenNodes.add(new CommandNode(new SubstituteExecutor()));
                childrenNodes.add(new CommandNode(new XsltExecutor()));
        }
        return childrenNodes;
    }
}
