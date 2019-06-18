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
package uk.theretiredprogrammer.extexp.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.BiFunction;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import static javax.json.JsonValue.ValueType.ARRAY;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;
import uk.theretiredprogrammer.extexp.support.local.ErrorCount;
import uk.theretiredprogrammer.extexp.support.local.IDGenerator;


/**
 * The Execution Environment is a description of the current environment for
 * Extexp Command Execution.
 * 
 * As such it contains items such as:
 * 
 * IOPaths {@link IOPaths}
 * 
 * Temporary FileStore
 * 
 * CommandSequences Store
 * 
 * IDGenerator
 * 
 * ErrorTracker
 *
 * @author richard linsdale
 */
public class ExecutionEnvironment {
    
    /**
     * Create a initial Execution Environment for the project Build Instructions
     * 
     * @param projectfolder the project's root folder
     * @param msg the reporting output stream 
     * @param err the error reporting output stream
     * @return the new ExecutionEnvironment created 
     */
    public static ExecutionEnvironment create(FileObject projectfolder, OutputWriter msg, OutputWriter err) {
        CommandFactory.init();
        IOPaths paths = new IOPaths(
                projectfolder,
                projectfolder.getFileObject("src"),
                useOrCreateFolder(err, projectfolder, "cache"),
                useOrCreateFolder(err, projectfolder, "output"),
                msg,
                err
        );
        FileObject buildinstructions = paths.getProjectfolder().getFileObject("build.json");
        if (buildinstructions == null) {
            paths.getErr().println("Build Instructions (build.json) is missing");
            return null;
        }
        JsonObject jobj;
        try (InputStream is = buildinstructions.getInputStream();
                JsonReader rdr = Json.createReader(is)) {
            jobj = rdr.readObject();
        } catch (IOException ex) {
            paths.getErr().println("Error in reading Build Instructions: " + ex.getLocalizedMessage());
            return null;
        }
        CommandSequenceStore commandsequencestore = new CommandSequenceStore();
        if (parse(jobj, (name, sequence) -> commandsequencestore.addSequence(name, sequence, paths)) > 0) {
            return null;
        }
        CommandSequence commandsequence = commandsequencestore.getSequence("MAIN");
        if (commandsequence == null) {
            paths.getErr().println("Command sequence \"MAIN\" missing");
            return null;
        }
        return new ExecutionEnvironment(paths, commandsequencestore);
    }

    private static FileObject useOrCreateFolder(OutputWriter err, FileObject parent, String... foldernames) {
        FileObject folder = parent;
        for (String foldername : foldernames) {
            FileObject parentfolder = folder;
            folder = parentfolder.getFileObject(foldername);
            if (folder == null) {
                try {
                    folder = parentfolder.createFolder(foldername);
                } catch (IOException ex) {
                    return parentfolder;
                }
            }
            if (!folder.isFolder()) {
                err.println("../" + foldername + " is not a folder");
                return parentfolder;
            }
        }
        return folder;
    }

    private static int parse(JsonObject jobj, BiFunction<String, JsonArray, Integer> sequencehandler) {
        int errorcount = 0;
        for (Map.Entry<String,JsonValue> es : jobj.entrySet()){
           String name = es.getKey();
            JsonValue content = es.getValue();
            if (content.getValueType() == ARRAY) {
                errorcount+=sequencehandler.apply(name, (JsonArray) content);
            } 
        }
        return errorcount;
    }
    
    /**
     *  the {@link IOPaths} object
     */
    public final IOPaths paths;

    /**
     * the {@link TemporaryFileStore} object
     */
    public final TemporaryFileStore tempfs;

    /**
     * the {@link CommandSequenceStore} object
     */
    public final CommandSequenceStore commandsequences;

    /**
     * the {@link IDGenerator} object
     */
    public final IDGenerator idgenerator;

    /**
     * the {@link ErrorCount} object
     */
    public final ErrorCount errorflag;
    
    /**
     * Clone a copy of this Environment, with revised IoPaths
     * 
     * @param paths the new IOPaths object to be inserted into the new ExecutionEnvironment
     * @return the new ExecutionEnvironment
     */
    public final ExecutionEnvironment clone(IOPaths paths) {
        return new ExecutionEnvironment(paths, this.tempfs, this.commandsequences, this.idgenerator, this.errorflag);
    }
    
    /**
     * Clone a copy of this Environment, with revised IoPaths and new empty TemporaryFileStore
     * 
     * @param paths the new IOPaths object to be inserted into the new ExecutionEnvironment
     * @return the new ExecutionEnvironment
     */
    public final ExecutionEnvironment cloneWithNewTFS(IOPaths paths) {
        return new ExecutionEnvironment(paths, new TemporaryFileStore(), this.commandsequences, this.idgenerator, this.errorflag);
    }
    
    private ExecutionEnvironment(IOPaths paths, CommandSequenceStore commandsequences) {
        this(paths, new TemporaryFileStore(), commandsequences, new IDGenerator(), new ErrorCount());
    }

    private ExecutionEnvironment(IOPaths paths, TemporaryFileStore tempfs, CommandSequenceStore commandsequences,
            IDGenerator idgenerator, ErrorCount errorflag) {
        this.paths = paths;
        this.tempfs = tempfs;
        this.commandsequences = commandsequences;
        this.idgenerator = idgenerator;
        this.errorflag = errorflag;
    }
    
    // utility methods

    /**
     * Print a line to the reporting writer
     * @param s the line to output
     */
    
    public void println(String s) {
        paths.getMsg().println(s);
    }
    
    /**
     * Print a line to the error reporting writer
     * @param s the line to output
     */
    public void errln(String s) {
        paths.getErr().println(s);
        errorflag.addError();
    }
    
    /**
     * Increment the error count
     */
    public void addError() {
        errorflag.addError();
    }
    
    /**
     * Get the error count
     * 
     * @return the error count
     */
    public int getErrorCount() {
        return errorflag.getErrorCount();
    }
}