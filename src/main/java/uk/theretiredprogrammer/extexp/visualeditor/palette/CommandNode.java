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

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import uk.theretiredprogrammer.extexp.support.Command;

/**
 *
 * @author richard
 */
public class CommandNode extends AbstractNode {
    
    private final Command command;
    
    public CommandNode(Command command) {
        super(Children.LEAF, Lookups.fixed( new Object[] {command} ) );
        this.command = command;
        setIconBaseWithExtension(command.getWidgetImageName());
        this.setDisplayName(command.getDisplayName());
    }
    
    public Command getCommand() {
        return command;
    }
    
}
