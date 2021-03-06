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
     * the {@link IOPaths} object
     */
    public final IOPaths paths;

    /**
     *  the Memory FS
     */
    public final MemoryFS tempfs;

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
     * the debug required flag
     */
    public final boolean debugrequired;
    

    /**
     * Constructor
     *
     * @param projectfolder the project's root folder
     * @param buildfile the build file
     * @param msg the reporting output stream
     * @param err the error reporting output stream
     * @param debugrequired true if debugging required
     * @throws IOException if any error detected during construction
     */
    public ExecutionEnvironment(FileObject projectfolder, FileObject buildfile,
            OutputWriter msg, OutputWriter err, boolean debugrequired) throws IOException {
        this.commandsequences = new CommandSequenceStore(buildfile, err::println);
        this.paths = new IOPaths(
                projectfolder,
                projectfolder.getFileObject("src"),
                useOrCreateFolder(err, projectfolder, "cache", buildfile.getName()),
                useOrCreateFolder(err, projectfolder, "output", buildfile.getName()),
                msg,
                err);
        this.tempfs = new MemoryFS();
        this.idgenerator = new IDGenerator();
        this.errorflag = new ErrorCount();
        this.debugrequired = debugrequired;
    }

    private ExecutionEnvironment(IOPaths paths, MemoryFS tempfs, CommandSequenceStore commandsequences,
            IDGenerator idgenerator, ErrorCount errorflag, boolean debugrequired) {
        this.paths = paths;
        this.tempfs = tempfs;
        this.commandsequences = commandsequences;
        this.idgenerator = idgenerator;
        this.errorflag = errorflag;
        this.debugrequired = debugrequired;
    }

    /**
     * Clone a copy of this Environment, with revised IoPaths
     *
     * @param paths the new IOPaths object to be inserted into the new
     * ExecutionEnvironment
     * @return the new ExecutionEnvironment
     */
    public final ExecutionEnvironment clone(IOPaths paths) {
        return new ExecutionEnvironment(paths, this.tempfs,
                this.commandsequences, this.idgenerator, this.errorflag, this.debugrequired);
    }

    /**
     * Clone a copy of this Environment, with revised IoPaths and new empty
     * TemporaryFileStore
     *
     * @param paths the new IOPaths object to be inserted into the new
     * ExecutionEnvironment
     * @return the new ExecutionEnvironment
     */
    public final ExecutionEnvironment cloneWithNewTFS(IOPaths paths) {
        return new ExecutionEnvironment(paths, new MemoryFS(),
                this.commandsequences, this.idgenerator, this.errorflag, this.debugrequired);
    }

    private FileObject useOrCreateFolder(OutputWriter err, FileObject parent, String... foldernames) {
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

    // utility methods
    /**
     * Print a line to the reporting writer
     *
     * @param s the line to output
     */
    public void println(String s) {
        paths.getMsg().println(s);
    }

    /**
     * Print a line to the error reporting writer
     *
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
