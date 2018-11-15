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

import uk.theretiredprogrammer.extexp.executors.SubstituteExecutor;
import uk.theretiredprogrammer.extexp.executors.UseRecipeExecutor;
import uk.theretiredprogrammer.extexp.executors.XsltExecutor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import uk.theretiredprogrammer.extexp.executors.CopyExecutor;
import uk.theretiredprogrammer.extexp.executors.CreateRecipeExecutor;
import uk.theretiredprogrammer.extexp.executors.FopExecutor;
import uk.theretiredprogrammer.extexp.executors.IfDefinedExecutor;
import uk.theretiredprogrammer.extexp.executors.ImagesetExecutor;
import uk.theretiredprogrammer.extexp.executors.MarkdownExecutor;
import static javax.json.JsonValue.ValueType.OBJECT;

/**
 *
 * @author richard
 */
public class ProcessStep {

    public static void execute(IOPaths paths, Map<String, JsonStructure> recipestore, ParameterFrame frame) throws IOException {
        String description = frame.getParameter("description");
        if (description != null) {
            paths.getMsg().println("    ..." + description);
        }
        if (!frame.containsKey("action")) {
            throw new IOException("action label missing");
        }
        String action = frame.getParameter("action");
        if (action == null) {
            JsonStructure js = frame.getJsonParameter("action");
            if (js == null) {
                throw new IOException("action label cannot be enumerated - System Error");
            }
            String pval = frame.getFrameParameter("path");
            IOPaths newpaths = pval == null ? paths : paths.updatePath(pval);
            if (js.getValueType() == OBJECT) {
                ParameterFrame nextframe = new ParameterFrame((JsonObject) js, frame);
                execute(newpaths, recipestore, nextframe);
            } else {
                for (JsonObject jchild : ((JsonArray) js).getValuesAs(JsonObject.class)) {
                    ParameterFrame nextframe = new ParameterFrame(jchild, frame);
                    execute(newpaths, recipestore, nextframe);
                }
            }
        } else {
            Executor exec;
            switch (action) {
                case "markdown":
                    exec = new MarkdownExecutor();
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
                case "create-recipe":
                    exec = new CreateRecipeExecutor();
                    break;
                case "use-recipe":
                    exec = new UseRecipeExecutor();
                    break;
                case "if-defined":
                    exec = new IfDefinedExecutor();
                    break;
                default:
                    throw new IOException("unknown action:" + action);
            }
            // get/prepare the IO requirements for the executor
            for (IODescriptor iodescriptor : exec.getIODescriptors()) {
                if (!(iodescriptor.isOptional() && !frame.containsKey(iodescriptor.getName()))) {
                    String pvalue;
                    String content;
                    switch (iodescriptor.getType()) {
                        case RESOURCESDESCRIPTOR:
                            iodescriptor.setValue(
                                    new ResourcesDescriptor(
                                            paths.getResourcesfolder(),
                                            paths.getRelativepath()
                                    )
                            );
                            break;
                        case PARAMETERDESCRIPTOR:
                            iodescriptor.setValue(
                                    new ParameterDescriptor(
                                            (key) -> frame.getParameter(key)
                                    )
                            );
                            break;
                        case INPUTRECIPE:
                            iodescriptor.setValue(recipestore.get(frame.getParameter("recipe")));
                            break;
                        case PARAMSTRING:
                            iodescriptor.setValue(getParameterValue(frame, iodescriptor));
                            break;
                        case JSONPARAMSTRING:
                            iodescriptor.setValue(getJsonStructureValue(frame, iodescriptor));
                            break;
                        case INPUTSTRING:
                            pvalue = getParameterValue(frame, iodescriptor);
                            content = frame.getParameter(pvalue);
                            iodescriptor.setValue(content == null
                                    ? IoUtil.findFile(pvalue, paths.getContentfolder(), paths.getSharedcontentfolder()).asText()
                                    : content
                            );
                            break;
                        case READER:
                            pvalue = getParameterValue(frame, iodescriptor);
                            content = frame.getParameter(pvalue);
                            iodescriptor.setValue(content == null
                                    ? new BufferedReader(new InputStreamReader(
                                            IoUtil.findFile(pvalue, paths.getContentfolder(), paths.getSharedcontentfolder()).getInputStream()
                                    ))
                                    : new StringReader(content)
                            );
                            break;
                        case INPUTPATH:
                            pvalue = getParameterValue(frame, iodescriptor);
                            content = frame.getParameter(pvalue);
                            iodescriptor.setValue((content == null
                                    ? IoUtil.findFile(pvalue, paths.getContentfolder(), paths.getSharedcontentfolder())
                                    : IoUtil.stringToFile(paths.getCachefolder(), pvalue, content)).getPath());
                            break;
                        case WRITER:
                            pvalue = getParameterValue(frame, iodescriptor);
                            Writer writer;
                            if (pvalue.startsWith("!")) {
                                writer = new StringWriter();
                            } else {
                                writer = new BufferedWriter(new OutputStreamWriter(IoUtil.getOutputStream(paths.getOutfolder(), pvalue)));
                            }
                            iodescriptor.setValue(writer);
                            break;
                        case OUTPUTPATH:
                            pvalue = getParameterValue(frame, iodescriptor);
                            if (pvalue.startsWith("!")) {
                                throw new IOException("Cannot use string based object, please use a filestore object");
                            } else {
                                iodescriptor.setValue(paths.getOutPath() + "/" + pvalue);
                            }
                            break;
                        case OUTPUTRECIPE:
                        case JSONSTRUCTURE:
                            break;
                        default:
                            throw new IOException("IODescription - unknown pre-exec requirement: " + iodescriptor.getType().toString());
                    }
                }
            }
            // and do the required action
            exec.execute(paths.getMsg(), paths.getErr());
            // and handle the post processing
            for (IODescriptor iodescriptor : exec.getIODescriptors()) {
                if (iodescriptor.isResult()) {
                    String pvalue = getParameterValue(frame, iodescriptor);
                    switch (iodescriptor.getType()) {
                        case RESOURCESDESCRIPTOR:
                        case PARAMETERDESCRIPTOR:
                        case PARAMSTRING:
                        case INPUTSTRING:
                        case INPUTPATH:
                        case OUTPUTPATH:
                        case INPUTRECIPE:
                        case JSONPARAMSTRING:
                            throw new IOException("Cannot set result on these IODescriptors");
                        case OUTPUTRECIPE:
                            recipestore.put(pvalue, (JsonStructure) iodescriptor.getValue());
                            break;
                        case JSONSTRUCTURE:
                            String pval = frame.getFrameParameter("path");
                            IOPaths newpaths = pval == null ? paths : paths.updatePath(pval);
                            JsonStructure js = (JsonStructure) iodescriptor.getValue();
                            if (js != null) {
                                if (js.getValueType() == OBJECT) {
                                    ParameterFrame newframe = new SimpleParameterFrame((JsonObject) js, frame);
                                    execute(newpaths, recipestore, newframe);
                                } else {
                                    for (JsonObject jobj : ((JsonArray) js).getValuesAs(JsonObject.class)) {
                                        ParameterFrame newframe = new SimpleParameterFrame(jobj, frame);
                                        execute(newpaths, recipestore, newframe);
                                    }
                                }
                            }
                            break;
                        case READER:
                            ((Reader) iodescriptor.getValue()).close();
                            break;
                        case WRITER:
                            Writer w = (Writer) iodescriptor.getValue();
                            w.close();
                            if (w instanceof StringWriter) {
                                frame.setStringFileParameter(
                                        getParameterValue(frame, iodescriptor).substring(1),
                                        iodescriptor.getValue().toString()
                                );
                            }
                            break;
                        default:
                            throw new IOException("IODescription - unknown post-exec requirement: " + iodescriptor.getType().toString());
                    }
                }
            }
        }
    }

    private static String getParameterValue(ParameterFrame frame, IODescriptor iodescriptor) {
        return frame.getParameter(iodescriptor.getName());
    }

    private static JsonStructure getJsonStructureValue(ParameterFrame frame, IODescriptor iodescriptor) {
        return frame.getJsonParameter(iodescriptor.getName());
    }
}
