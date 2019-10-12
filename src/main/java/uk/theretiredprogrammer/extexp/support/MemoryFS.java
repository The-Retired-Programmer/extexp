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
import java.io.OutputStreamWriter;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Implements a temporary in-memory FS
 *
 * @author richard linsdale
 */
final class MemoryFS {

    private final FileObject tempfsroot = FileUtil.createMemoryFileSystem().getRoot();

    /**
     * get the root folder for this filesystem
     *
     * @return the root folder
     */
    FileObject getRoot() {
        return tempfsroot;
    }

    /**
     * Get the FileObject for a temporary file.
     *
     * @param filename the name of a temporary file
     * @return the FileObject or null if the file does not exist
     */
    FileObject getFileObject(String filename) {
        return tempfsroot.getFileObject(filename);
    }

    /**
     * Get the InputStreamReader for reading a temporary file.
     *
     * @param filename the name of a temporary file
     * @return the InputStreamReader object or null if file does not exist
     */
    InputStreamReader getInputStreamReader(String filename) {
        FileObject fo;
        try {
            return (fo = tempfsroot.getFileObject(filename)) != null
                    ? new InputStreamReader(fo.getInputStream()) : null;
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    /**
     * Get the OutputStreamWriter for writing to a temporary file.
     *
     * @param filename the name of a temporary file
     * @return the OutputStreamWriter object
     * @throws java.io.IOException if problems
     */
    OutputStreamWriter getOutputStreamWriter(String filename) throws IOException {
        return getOutputStreamWriter(filename, false);
    }

    /**
     * Get the OutputStreamWriter for writing to a temporary file.
     *
     * @param filename the name of a temporary file
     * @param append true if writer is to set up for appending
     * @return the OutputStreamWriter object
     * @throws java.io.IOException if problems
     */
    OutputStreamWriter getOutputStreamWriter(String filename, boolean append) throws IOException {
        FileObject fo;
        String oldvalue = "";
        try {
            if ((fo = tempfsroot.getFileObject(filename)) != null) {
                if (append) {
                    oldvalue = fo.asText();
                }
                OutputStreamWriter osw = new OutputStreamWriter(fo.getOutputStream());
                if (append) {
                    osw.write(oldvalue);
                }
                return osw;
            }
            return new OutputStreamWriter(tempfsroot.createAndOpen(filename));
        } catch (FileAlreadyLockedException ex) {
            String msg = append ? "failed to get OutputStreamWriter when appending to " + filename + "(file locked)"
                    : "failed to get OutputStreamWriter when creating " + filename + "(file locked)";
            throw new IOException(msg);
        } catch (IOException ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null) {
                msg = append ? "failed to get OutputStreamWriter when appending to " + filename
                        : "failed to get OutputStreamWriter when creating " + filename;
            }
            throw new IOException(msg);
        }
    }
}
