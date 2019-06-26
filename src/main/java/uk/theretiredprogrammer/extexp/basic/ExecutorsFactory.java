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
package uk.theretiredprogrammer.extexp.basic;

import java.util.function.Consumer;
import org.openide.util.lookup.ServiceProvider;
import uk.theretiredprogrammer.extexp.api.ExecutorFactory;
import uk.theretiredprogrammer.extexp.support.Executor;

/**
 * The Factory creating a set of basic Executors. These include: Copy,
 * CopyResources, List and Substitute.
 *
 * @author richard linsdale
 */
@ServiceProvider(service = ExecutorFactory.class)
public class ExecutorsFactory implements ExecutorFactory {

    @Override
    public Executor create(String name) {
        switch (name) {
            case "copy":
                return new CopyExecutor();
            case "substitute":
                return new SubstituteExecutor();
            case "copy-resources":
                return new CopyResourcesExecutor();
            case "list":
                return new ListExecutor();
            case "message":
                return new MessageExecutor();
        }
        return null;
    }

    @Override
    public void consumeAllExecutors(Consumer<Executor> consumer) {
        consumer.accept(new CopyExecutor());
        consumer.accept(new SubstituteExecutor());
        consumer.accept(new CopyResourcesExecutor());
        consumer.accept(new ListExecutor());
    }
}
