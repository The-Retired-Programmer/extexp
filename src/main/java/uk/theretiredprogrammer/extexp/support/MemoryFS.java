/*
 * Copyright 2019 richard linsdale.
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Implements a temporary in-memory FS
 *
 * @author richard linsdale
 */
public class MemoryFS {

    private final FileObject tempfsroot;

    public MemoryFS() {
        this.tempfsroot = FileUtil.createMemoryFileSystem().getRoot();
    }
    
    /**
     *  get the root folder for this filesystem
     * 
     * @return the root folder
     */
    public FileObject getRoot() {
        return tempfsroot;
    }

    /**
     * Get a list of all filenames in the temporary filestore.
     *
     * @return the list of filenames
     */
    public List<String> allnames() {
        return Arrays.asList(tempfsroot.getChildren()).stream()
                .filter(fo -> fo.isData())
                .map(fo -> fo.getNameExt())
                .collect(Collectors.toList());
    }

    /**
     * Test if a filename exists in the memory filestore.
     *
     * @param name the file name to be tested
     * @return true if named file exists
     */
    public boolean exists(String name) {
        return tempfsroot.getFileObject(name) != null;
    }

    /**
     * Get the FileObject for a temporary file.
     *
     * @param name the name of a temporary file
     * @return the FileObject
     */
    public FileObject getFileObject(String name) {
        return tempfsroot.getFileObject(name);
    }

    /**
     * Get the InputStreamReader for reading a temporary file.
     *
     * @param name the name of a temporary file
     * @return the InputStreamReader object
     */
    public InputStreamReader getInputStreamReader(String name) {
        FileObject fo;
        try {
            return (fo = tempfsroot.getFileObject(name)) != null
                    ? new InputStreamReader(fo.getInputStream()) : null;
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    /**
     * Get the content for a temporary file.
     *
     * @param name the name of a temporary file
     * @return the content
     * @throws java.io.IOException if problems
     */
    public Optional<String> read(String name) throws IOException {
        FileObject fo = tempfsroot.getFileObject(name);
        if (fo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(fo.asText());
    }

    /**
     * Write a string to a new created named file.
     *
     * if the file already exists, it will be deleted and a new file created for
     * the content to be inserted.
     *
     * @param name the name of a temporary file
     * @param content string to insert into the newly created temporary file
     * @throws java.io.IOException if problems
     */
    public void write(String name, String content) throws IOException {
        if (exists(name)) {
            FileObject fo = tempfsroot.getFileObject(name);
            try (OutputStream os = fo.getOutputStream();
                    PrintWriter pw = new PrintWriter(os)) {
                pw.print(content);
            }
        } else {
            FileObject fo = tempfsroot.createData(name);
            try (OutputStream os = fo.getOutputStream();
                    PrintWriter pw = new PrintWriter(os)) {
                pw.print(content);
            }
        }
    }

    /**
     * Append a string to a named temporary file.
     *
     * if the file dues not exist, an empty file will be created, prior to
     * appending the content
     *
     * @param name the name of a temporary file
     * @param content string to appended to the named file
     * @throws java.io.IOException if problems
     */
    public void append(String name, String content) throws IOException {
        if (exists(name)) {
            FileObject fo = tempfsroot.getFileObject(name);
            String previouscontent = fo.asText();
            try (OutputStream os = fo.getOutputStream();
                    PrintWriter pw = new PrintWriter(os)) {
                pw.print(previouscontent);
                pw.print(content);
            }
        } else {
            FileObject fo = tempfsroot.createData(name);
            try (OutputStream os = fo.getOutputStream();
                    PrintWriter pw = new PrintWriter(os)) {
                pw.print(content);
            }
        }
    }

    /**
     * Get the OutputStreamWriter for writing to a temporary file.
     *
     * @param name the name of a temporary file
     * @return the OutputStreamWriter object
     */
    public OutputStreamWriter getOutputStreamWriter(String name) {
        return getOutputStreamWriter(name, false);
    }

    /**
     * Get the OutputStreamWriter for writing to a temporary file.
     *
     * @param name the name of a temporary file
     * @param append true if writer is to set up for appending
     * @return the OutputStreamWriter object
     */
    public OutputStreamWriter getOutputStreamWriter(String name, boolean append) {
        FileObject fo;
        try {
            if ((fo = tempfsroot.getFileObject(name)) != null) {
                OutputStreamWriter osw = new OutputStreamWriter(fo.getOutputStream());
                if (append) {
                    osw.write(fo.asText());
                }
                return osw;
            } else {
                return new OutputStreamWriter(tempfsroot.createAndOpen(name));
            }
        } catch (IOException ex) {
            return null;
        }
    }
}
