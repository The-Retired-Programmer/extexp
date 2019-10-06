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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A Factory which can be used to obtain the content
 *
 * The name can reference content in any of:
 *
 * a "file" in the memory based temporary filestore
 *
 * a file located in the content folder, shared content folder or cache folder
 *
 * an explicit in-line string value
 *
 * Access to content can be provided via Reader/Writers, FileObjects, Paths or
 * OutputDescriptors
 *
 * @author richard linsdale
 */
public class IOFactory {

    /**
     * Create a Reader based on the provided parameter value.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return a Reader to act as a source
     * @throws java.io.IOException if problem
     */
    public static BufferedReader createReader(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return createReader(ee, parametervalue.get());
    }

    /**
     * Create a Reader based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return a Reader to act as a source
     * @throws java.io.IOException if problem
     */
    public static BufferedReader createReader(ExecutionEnvironment ee, String parametervalue) throws IOException {
        switch (parametervalue.charAt(0)) {
            case '=':
                return new BufferedReader(new StringReader(parametervalue.substring(1)));
            case '!':
            case '+':
            case '&':
                throw new IOException("filename should not start with output prefix in input reader context");
            default:
                InputStreamReader tempfsISW = ee.tempfs.getInputStreamReader(parametervalue);
                if (tempfsISW != null) {
                    return new BufferedReader(tempfsISW);
                }
                return new BufferedReader(new InputStreamReader(findInputFile(ee, parametervalue).getInputStream()));
        }
    }

    private static FileObject findInputFile(ExecutionEnvironment ee, String filename) throws IOException {
        for (FileObject fo
                : new FileObject[]{ee.paths.getCachefolder(), ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder()}) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return file;
                }
            }
        }
        throw new IOException("Input file missing - " + filename);
    }

    /**
     * Get an input FileObject based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return a FileObject to act as a source
     * @throws java.io.IOException if problem
     */
    public static FileObject getInputFO(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return getInputFO(ee, parametervalue.get());
    }

    /**
     * Get an input FileObject based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return a FileObject to act as a source
     * @throws java.io.IOException if problem
     */
    public static FileObject getInputFO(ExecutionEnvironment ee, String parametervalue) throws IOException {
        switch (parametervalue.charAt(0)) {
            case '=':
                throw new IOException("literal input should not be used in input fileobject context");
            case '!':
            case '+':
            case '&':
                throw new IOException("filename should not start with output prefix in input fileobject context");
            default:
                FileObject fo = ee.tempfs.getFileObject(parametervalue);
                return fo != null ? fo : findInputFile(ee, parametervalue);
        }
    }

    /**
     * Get an input filepath based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return the filepath
     * @throws java.io.IOException if problem, including file not found
     */
    public static String getInputPath(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return getInputPath(ee, parametervalue.get());
    }

    /**
     * Get an input filepath based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return the filepath
     * @throws java.io.IOException if problem, including file not found
     */
    public static String getInputPath(ExecutionEnvironment ee, String parametervalue) throws IOException {
        switch (parametervalue.charAt(0)) {
            case '=':
                throw new IOException("literal input should not be used in input filepath context");
            case '!':
            case '+':
            case '&':
                throw new IOException("filename should not start with output prefix in input filepath context");
            default:
                FileObject fo = ee.tempfs.getFileObject(parametervalue);
                if (fo == null) {
                    fo = findInputFile(ee, parametervalue);
                } else {
                    fo = toCacheFo(parametervalue, fo, ee);
                }
                return FileUtil.toFile(fo).getCanonicalPath();
        }
    }

    private static FileObject toCacheFo(String name, FileObject fromFo, ExecutionEnvironment ee) throws IOException {
        FileObject cachefolder = ee.paths.getCachefolder();
        FileObject outfo = cachefolder.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return fromFo.copy(cachefolder, name, "");
    }

    /**
     * Create a Writer based on the provided parameter value
     *
     * @param ee the ExecutorEnvironment
     * @param parametervalue the filename
     * @return a Writer to act as the target
     * @throws IOException if problems
     */
    public static BufferedWriter createWriter(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return createWriter(ee, parametervalue.get());
    }

    /**
     * Create a Writer based on the provided parameter value
     *
     * @param ee the ExecutorEnvironment
     * @param parametervalue the filename
     * @return a Writer to act as the target
     * @throws IOException if problems
     */
    public static BufferedWriter createWriter(ExecutionEnvironment ee, String parametervalue) throws IOException {
        switch (parametervalue.charAt(0)) {
            case '=':
                throw new IOException("literal input should not be used in output writer context");
            case '!':
                return new BufferedWriter(ee.tempfs.getOutputStreamWriter(parametervalue.substring(1)));
            case '+':
                return new BufferedWriter(ee.tempfs.getOutputStreamWriter(parametervalue.substring(1), true));
            case '&':
                return new BufferedWriter(new OutputStreamWriter(getOutputStream(ee.paths.getCachefolder(), parametervalue.substring(1))));
            default:
                return new BufferedWriter(new OutputStreamWriter(getOutputStream(ee.paths.getOutfolder(), parametervalue)));
        }
    }

    private static OutputStream getOutputStream(FileObject todirectory, String name) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return todirectory.createAndOpen(name);
    }

    /**
     * Get an output filepath based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename (optionally prefixed by '!' or '+')
     * @return the filepath
     * @throws java.io.IOException if problem
     */
    public static String getOutputPath(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return getOutputPath(ee, parametervalue.get());
    }

    /**
     * Get an output filepath based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return the filepath
     * @throws java.io.IOException if problem, including file not found
     */
    public static String getOutputPath(ExecutionEnvironment ee, String parametervalue) throws IOException {
        FileObject outfo;
        FileObject outfolder;
        switch (parametervalue.charAt(0)) {
            case '=':
                throw new IOException("literal input should not be used in output filepath context");
            case '!':
                throw new IOException("new in-memory file prefix should not be used in output filepath context");
            case '+':
                throw new IOException("append in-memory file prefix should not be used in output filepath context");
            case '&':
                outfolder = ee.paths.getCachefolder();
                outfo = outfolder.getFileObject(parametervalue.substring(1));
                break;
            default:
                outfolder = ee.paths.getOutfolder();
                outfo = outfolder.getFileObject(parametervalue);
        }
        if (outfo != null) {
            outfo.delete();
        }
        outfo = outfolder.createData(parametervalue);
        return FileUtil.toFile(outfo).getCanonicalPath();
    }

    /**
     * Get an output descriptor based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return the output descriptor
     * @throws java.io.IOException if problem
     */
    public static OutputDescriptor getOutputDescriptor(ExecutionEnvironment ee, Optional<String> parametervalue) throws IOException {
        if (!parametervalue.isPresent()) {
            throw new IOException("Missing Parameter Value");
        }
        return getOutputDescriptor(ee, parametervalue.get());
    }

    /**
     * Get an output descriptor based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename
     * @return the output descriptor
     * @throws java.io.IOException if problem, including file not found
     */
    public static OutputDescriptor getOutputDescriptor(ExecutionEnvironment ee, String parametervalue) throws IOException {
        switch (parametervalue.charAt(0)) {
            case '=':
                throw new IOException("literal input should not be used in output descriptor context");
            case '!':
                return new OutputDescriptor(ee.tempfs.getRoot(), parametervalue.substring(1));
            case '+':
                throw new IOException("append in-memory file prefix should not be used in output descriptor context");
            case '&':
                return new OutputDescriptor(ee.paths.getCachefolder(), parametervalue.substring(1));
            default:
                return new OutputDescriptor(ee.paths.getOutfolder(), parametervalue);
        }
    }
    
    /**
     * Descriptor for an output file.
     * 
     * Note that the File is not yet created.
     * 
     */
    public static class OutputDescriptor {

        /**
         * the folder in which the file will be created
         */
        public final FileObject folder;
        /**
         * the filename to be used
         */
        public final String filename;

        /**
         * create the descriptor
         * 
         * @param folder the folder in which the file will be created 
         * @param filename the filename to be used
         */
        public OutputDescriptor(FileObject folder, String filename) {
            this.folder = folder;
            this.filename = filename;
        }
    }
}
