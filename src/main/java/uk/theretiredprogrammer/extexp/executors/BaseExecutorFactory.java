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
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.extexp.execution.Executor;
import uk.theretiredprogrammer.extexp.execution.ExecutorFactory;

/**
 *
 * @author richard
 */
@ServiceProvider(service = ExecutorFactory.class)
public class BaseExecutorFactory implements ExecutorFactory {

    @Override
    public Executor create(String name) throws IOException {
        Executor exec;
        switch (name) {
            case "markdown":
                exec = new MarkdownExecutor();
                break;
            case "markdown-substitute":
                exec = new MarkdownAndSubstituteExecutor();
                break;
            case "copy":
                exec = new CopyExecutor();
                break;
            case "substitute":
                exec = new SubstituteExecutor();
                break;
            case "xslt":
                exec = new XsltExecutor();
                break;
            case "create-imageset":
                exec = new ImagesetExecutor();
                break;
            case "copy-resources":
                exec = new CopyResourcesExecutor();
                break;
            default:
                exec = null;
        }
        return exec;
    }
}