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
package uk.theretiredprogrammer.extexp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
 */
public class BuildStepExecutor {

    private final Map<String, String> strings = new HashMap<>();
    private Map<String, String> parentstrings;
    //
    private final FileObject contentfolder;
    private final FileObject sharedcontentfolder;
    private final FileObject cachefolder;
    private final FileObject outfolder;
    private final FileObject resourcesfolder;
    private final String relativepath;
    //
    Executor exec;
    private final Map<String, String> recipestore;

    public BuildStepExecutor(FileObject contentfolder, FileObject sharedcontentfolder,
            FileObject cachefolder, FileObject outfolder,
            FileObject resourcesfolder, String relativepath,
            Map<String, String> recipestore) {
        this.contentfolder = contentfolder;
        this.sharedcontentfolder = sharedcontentfolder;
        this.cachefolder = cachefolder;
        this.outfolder = outfolder;
        this.resourcesfolder = resourcesfolder;
        this.relativepath = relativepath;
        this.recipestore = recipestore;
    }

    // fluent configuration 
    public BuildStepExecutor extractParams(JsonObject jobj) {
        parentstrings = null;
        extractParameters(jobj, strings);
        return this;
    }

    public BuildStepExecutor extractParams(JsonObject jobj, Map<String, String> parentstrings) {
        strings.putAll(parentstrings);
        this.parentstrings = parentstrings;
        extractParameters(jobj, strings);
        return this;
    }

    public static void extractParameters(JsonObject jobj, Map<String, String> strings) {
        for (String key : jobj.keySet()) {
            JsonValue jval = jobj.get(key);
            switch (jval.getValueType()) {
                case ARRAY:
                case OBJECT:
                    strings.put(key, jval.toString());
                    break;
                case STRING:
                    strings.put(key, ((JsonString) jobj.get(key)).getString());
                    break;
                case NUMBER:
                    JsonNumber jnum = (JsonNumber) jval;
                    if (jnum.isIntegral()) {
                        strings.put(key, Integer.toString(jnum.intValueExact()));
                    } else {
                        strings.put(key, jnum.toString());
                    }
                    break;
                default:
                    strings.put(key, jval.toString());
            }
        }
    }

    public void execute(OutputWriter msg, OutputWriter err) throws IOException {
        msg.println("    ..." + strings.get("description"));
        switch (strings.get("action")) {
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
            default:
                throw new IOException("unknown action");
        }
        // get/prepare the IO requirements for the executor
        List<IODescriptor<Writer>> writerstoclose = new ArrayList<>();
        List<IODescriptor<StringWriter>> writerstosavestrings = new ArrayList<>();
        List<IODescriptor<StringWriter>> writerstosaverecipes = new ArrayList<>();
        List<IODescriptor<StringWriter>> writerstoexecrecipes = new ArrayList<>();
        for (IODescriptor iodescriptor : exec.getIODescriptors()) {
            String pname = iodescriptor.getName();
            if (!(iodescriptor.isOptional() && !strings.containsKey(pname))) {
                String fname = "";
                boolean isrealfile = true;
                if (pname != null) {
                    fname = strings.get(pname);
                    if (fname == null) {
                        throw new IOException("required parameter is not defined: " + pname);
                    }
                    isrealfile = !strings.containsKey(fname);
                }
                switch (iodescriptor.getType()) {
                    case RESOURCESDESCRIPTOR:
                        iodescriptor.setValue(
                                new ResourcesDescriptor(
                                        resourcesfolder,
                                        relativepath
                                )
                        );
                        break;
                    case PARAMETERDESCRIPTOR:
                        iodescriptor.setValue(
                                new ParameterDescriptor(
                                        (s) -> strings.get(s)
                                )
                        );
                        break;
                    case RECIPE:
                        iodescriptor.setValue(recipestore.get(fname));
                        break;
                    case PARAMSTRING:
                        iodescriptor.setValue(fname);
                        break;
                    case INPUTSTRING:
                        iodescriptor.setValue(isrealfile
                                ? IoUtil.findFile(fname, contentfolder, sharedcontentfolder).asText()
                                : strings.get(fname)
                        );
                        break;
                    case READER:
                        iodescriptor.setValue(isrealfile
                                ? new BufferedReader(new InputStreamReader(
                                        IoUtil.findFile(fname, contentfolder, sharedcontentfolder).getInputStream()
                                ))
                                : new StringReader(strings.get(fname))
                        );
                        break;
                    case INPUTPATH:
                        iodescriptor.setValue((isrealfile
                                ? IoUtil.findFile(fname, contentfolder, sharedcontentfolder)
                                : IoUtil.stringToFile(cachefolder, fname, strings.get(fname))).getPath());
                        break;
                    case WRITER:
                        Writer writer;
                        if (fname.startsWith("!")) {
                            writer = new StringWriter();
                            writerstosavestrings.add(iodescriptor);
                        } else {
                            writer = new BufferedWriter(new OutputStreamWriter(IoUtil.getOutputStream(outfolder, fname)));
                            writerstoclose.add(iodescriptor);
                        }
                        iodescriptor.setValue(writer);
                        break;
                    case RECIPEWRITER:
                        StringWriter recipewriter = new StringWriter();
                        writerstosaverecipes.add(iodescriptor);
                        iodescriptor.setValue(recipewriter);
                        break;
                    case EXECRECIPEWRITER:
                        StringWriter execrecipewriter = new StringWriter();
                        writerstoexecrecipes.add(iodescriptor);
                        iodescriptor.setValue(execrecipewriter);
                        break;
                    case OUTPUTPATH:
                        if (fname.startsWith("!")) {
                            throw new IOException("Cannot use string based object, please use a filestore object");
                        } else {
                            iodescriptor.setValue(outfolder.getPath() + "/" + fname);
                        }
                        break;
                    default:
                        throw new IOException("IODescription - unknow requirement");
                }
            }
        }
        // and do the required action
        exec.execute(msg, err);
        // just close the file writers
        for (IODescriptor<Writer> iodesc : writerstoclose) {
            iodesc.getValue().close();
        }
        // and copy all string writers to parameter storage
        for (IODescriptor<StringWriter> iodesc : writerstosavestrings) {
            parentstrings.put(strings.get(iodesc.getName()).substring(1), iodesc.getValue().toString());
        }
        // and copy all recipe writers to recipe storage
        for (IODescriptor<StringWriter> iodesc : writerstosaverecipes) {
            recipestore.put(strings.get(iodesc.getName()), iodesc.getValue().toString());
        }
        // and exec all execrecipe writers
        for (IODescriptor<StringWriter> iodesc : writerstoexecrecipes) {
            String execjson = iodesc.getValue().toString();
            //msg.println(execjson);
            RecipeExecutor.execute(contentfolder, sharedcontentfolder, cachefolder, outfolder,
            resourcesfolder, relativepath, recipestore, msg, err,
            strings, iodesc.getValue().toString());
        }
    }
}
