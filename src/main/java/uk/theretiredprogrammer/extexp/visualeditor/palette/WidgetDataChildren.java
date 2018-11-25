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
 * ShapeChildren.java
 *
 * Created on September 21, 2006, 9:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * To understand this class, see https://platform.netbeans.org/tutorials/nbm-nodesapi3.html
 */

import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Index;
import uk.theretiredprogrammer.extexp.visualeditor.CopyExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.CreateRecipeWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.FileSourceWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.FileTargetWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.FopExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.IfDefinedWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.ImagesetExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.MarkdownAndSubstituteExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.MarkdownExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.ParameterSourceWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.SequenceWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.SubstituteExecutorWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.TemporaryFileInputOutputWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.RunWidgetData;
import uk.theretiredprogrammer.extexp.visualeditor.XsltExecutorWidgetData;

/**
 *
 * @author Geertjan Wielenga
 */
public class WidgetDataChildren  extends Index.ArrayChildren {

    private final Category category;

    public WidgetDataChildren(Category category) {
        this.category = category;
    }

    @Override
    protected List initCollection() {
        List<WidgetDataNode> childrenNodes = new ArrayList<>();
        switch (category.getType()) {
            case CONTROL:
                childrenNodes.add(new WidgetDataNode(new SequenceWidgetData()));
                childrenNodes.add(new WidgetDataNode(new IfDefinedWidgetData()));
                childrenNodes.add(new WidgetDataNode(new CreateRecipeWidgetData()));
                childrenNodes.add(new WidgetDataNode(new RunWidgetData()));
                break;
            case EXECUTOR:
                childrenNodes.add(new WidgetDataNode(new CopyExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new FopExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new ImagesetExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new MarkdownAndSubstituteExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new MarkdownExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new SubstituteExecutorWidgetData()));
                childrenNodes.add(new WidgetDataNode(new XsltExecutorWidgetData()));
                break;
            case SOURCE:
                childrenNodes.add(new WidgetDataNode(new ParameterSourceWidgetData()));
                childrenNodes.add(new WidgetDataNode(new FileSourceWidgetData()));
                break;
            case TARGET:
                childrenNodes.add(new WidgetDataNode(new FileTargetWidgetData()));
                break;
            case BOTH:
                childrenNodes.add(new WidgetDataNode(new TemporaryFileInputOutputWidgetData()));
                break;
            case CONNECTOR:
        }
        return childrenNodes;
    }

}
