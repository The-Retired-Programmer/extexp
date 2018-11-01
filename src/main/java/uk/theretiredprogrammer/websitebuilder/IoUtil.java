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
package uk.theretiredprogrammer.websitebuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author richard
 */
public class IoUtil {

    public static void copyFilesIfNotPresent(FileObject directory, String... filenames) throws IOException {
        for (String filename : filenames) {
            if (directory.getFileObject(filename) == null) {
                try (InputStream is = IoUtil.class.getResourceAsStream(filename);
                        OutputStream os = directory.createAndOpen(filename)) {
                    FileUtil.copy(is, os);
                }
            }
        }
    }
    
    public static void copyFile(FileObject todirectory, FileObject from) throws IOException {
        
        try (InputStream is = from.getInputStream();
                OutputStream os = getOutputStream(todirectory,from.getNameExt())) {
            FileUtil.copy(is, os);
        }
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

    public static OutputStream getOutputStream(FileObject parent, String filename) throws IOException {
        FileObject outfo = parent.getFileObject(filename);
        if (outfo != null) {
            outfo.delete();
        }
        return parent.createAndOpen(filename);
    }
    
    public static FileObject findFile(String filename) {
        for (FileObject fo: Build.getFos()){
            FileObject file = fo.getFileObject(filename);
            if (file != null && file.isData()){
                return file;
            }
        }
        return null;
    }
}
