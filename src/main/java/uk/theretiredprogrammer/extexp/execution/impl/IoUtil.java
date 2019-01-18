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
package uk.theretiredprogrammer.extexp.execution.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public class IoUtil {

    public static FileObject stringToFile(FileObject todirectory, String name, String content) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        outfo = todirectory.createData(name);
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(outfo.getOutputStream()))) {
            out.write(content);
        }
        return outfo;
    }

    public static OutputStream getOutputStream(FileObject todirectory, String name) throws IOException {
        FileObject outfo = todirectory.getFileObject(name);
        if (outfo != null) {
            outfo.delete();
        }
        return todirectory.createAndOpen(name);
    }

    public static FileObject useFolder(FileObject parent, String... foldernames) {
        FileObject folder = parent;
        for (String foldername : foldernames) {
            folder = folder.getFileObject(foldername);
            if (folder == null) {
                return null;
            }
            if (!folder.isFolder()) {
                return null;
            }
        }
        return folder;
    }

    public static FileObject useOrCreateFolder(FileObject parent, String... foldernames) throws IOException {
        FileObject folder = parent;
        for (String foldername : foldernames) {
            FileObject parentfolder = folder;
            folder = parentfolder.getFileObject(foldername);
            if (folder == null) {
                folder = parentfolder.createFolder(foldername);
            }
            if (!folder.isFolder()) {
                throw new IOException("../" + foldername + " is not a folder");
            }
        }
        return folder;
    }

    public static FileObject findFile(String filename, FileObject... fos) throws IOException {
        for (FileObject fo : fos) {
            if (fo != null) {
                FileObject file = fo.getFileObject(filename);
                if (file != null && file.isData()) {
                    return file;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot locate file \"");
        sb.append(filename);
        sb.append("\" for input\n");
        for (FileObject fo : fos) {
            if (fo == null) {
                sb.append("null file object\n");
            } else {
                sb.append(fo.getPath());
                sb.append('\n');
            }
        }
        throw new IOException(sb.toString());
    }
}
