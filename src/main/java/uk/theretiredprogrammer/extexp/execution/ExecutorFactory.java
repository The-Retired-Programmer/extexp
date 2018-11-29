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
package uk.theretiredprogrammer.extexp.execution;

import java.io.IOException;
import javax.json.JsonObject;
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
public class ExecutorFactory {

    public static final Executor create(JsonObject jobj) throws IOException {
        Executor exec;
        String actionname = jobj.getString("Do", "");
        switch (actionname) {
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
            case "fop":
                exec = new FopExecutor();
                break;
            case "create-imageset":
                exec = new ImagesetExecutor();
                break;
            default:
                exec = null;
        }
        if (exec != null) {
            exec.parse(jobj);
        }
        return exec;
    }
}
