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

/*
 * CategoryChildren.java
 *
 * Created on September 21, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * To understand this class, see https://platform.netbeans.org/tutorials/nbm-nodesapi3.html
 */

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Geertjan Wielenga
 */
public class CategoryChildren extends Children.Keys {

    public static enum CategoryType {
        CONTROL("Executor Controls"),
        EXECUTOR("Executors"),
        SOURCE("Sources"),
        TARGET("Targets"),
        BOTH("Both (Target and Source)"),
        CONNECTOR("Connectors");
        
        public String title;

        CategoryType(String title) {
            this.title = title;
        }
    }

    public CategoryChildren() {
    }

    @Override
    protected Node[] createNodes(Object key) {
        Category obj = (Category) key;
        return new Node[]{new CategoryNode(obj)};
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        CategoryType[] all = CategoryType.values();
        Category[] categories = new Category[all.length];
        for (int i = 0; i < all.length; i++) {
            categories[i] = new Category();
            categories[i].setName(all[i].title);
            categories[i].setType(all[i]);
        }
        setKeys(categories);
    }
}
