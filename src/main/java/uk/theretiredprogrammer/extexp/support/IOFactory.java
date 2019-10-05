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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Optional;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A Factory which will return a reader, which can be used to obtain the content
 *
 * The name can reference content in any of:
 *
 * a "file" in the memory based temporary filestore
 *
 * a file located in the content folder or the shared content folder
 *
 * a parameter value
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
        InputStreamReader tempfsISW = ee.tempfs.getInputStreamReader(parametervalue);
        if (tempfsISW != null) {
            return new BufferedReader(tempfsISW);
        }
        FileObject fo = findInputFile(parametervalue, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
        if (fo != null) {
            return new BufferedReader(new InputStreamReader(fo.getInputStream()));
        } else {
            return new BufferedReader(new StringReader(parametervalue));
        }
    }

    private static FileObject findInputFile(String filename, FileObject... fos) throws IOException {
        for (FileObject fo : fos) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return file;
                }
            }
        }
        return null;
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
        FileObject fo = ee.tempfs.getFileObject(parametervalue);
        if (fo != null) {
            return fo;
        }
        return findInputFile(parametervalue, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
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
        FileObject fo = ee.tempfs.getFileObject(parametervalue);
        if (fo == null) {
            fo = findInputFile(parametervalue, ee.paths.getContentfolder(), ee.paths.getSharedcontentfolder());
        } else {
            fo = toCacheFo(parametervalue,fo,ee);
        }
        return FileUtil.toFile(fo).getCanonicalPath();
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
     * @param parametervalue the filename (optionally prefixed by '!' or '+')
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
     * @param parametervalue the filename (optionally prefixed by '!' or '+')
     * @return a Writer to act as the target
     * @throws IOException if problems
     */
    public static BufferedWriter createWriter(ExecutionEnvironment ee, String parametervalue) throws IOException {
        if (parametervalue.startsWith("!")) {
            return new BufferedWriter(ee.tempfs.getOutputStreamWriter(parametervalue.substring(1)));
        }
        if (parametervalue.startsWith("+")) {
            return new BufferedWriter(ee.tempfs.getOutputStreamWriter(parametervalue.substring(1), true));
        }
        return new BufferedWriter(new OutputStreamWriter(getOutputStream(ee.paths.getOutfolder(), parametervalue)));
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
     * Get an input filepath based on the provided parameter.
     *
     * @param ee the ExecutionEnvironment
     * @param parametervalue the filename (optionally prefixed by '!' or '+')
     * @return the filepath
     * @throws java.io.IOException if problem, including file not found
     */
    public static String getOutputPath(ExecutionEnvironment ee, String parametervalue) throws IOException {
        if (parametervalue.startsWith("!")) {
            throw new IOException("Path name cannot be created for a in-memory file");
        }
        if (parametervalue.startsWith("+")) {
            throw new IOException("Path name cannot be created for a in-memory file");
        }
        FileObject outfolder = ee.paths.getOutfolder();
        FileObject outfo = outfolder.getFileObject(parametervalue);
        if (outfo != null) {
            outfo.delete();
        }
        outfo = outfolder.createData(parametervalue);
        return FileUtil.toFile(outfo).getCanonicalPath();
    }
}
