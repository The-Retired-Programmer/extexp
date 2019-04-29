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

/**
 * Describes the applicable file paths, a component of the ExecutionEnvironment
 * 
 * @author richard linsdale.
 */
public class IOPaths {

    private final FileObject projectfolder;
    private final FileObject contentfolder;
    private final FileObject sharedcontentfolder;
    private final FileObject cachefolder;
    private final FileObject outfolder;
    private FileObject resourcesfolder;
    private String relativepath;
    private final OutputWriter msg;
    private final OutputWriter err;

    /**
     * Constructor
     * 
     * @param projectfolder the project folder
     * @param contentfolder the content folder
     * @param cachefolder the cache (temporary files) folder
     * @param outfolder the output folder
     * @param msg the message writer
     * @param err the error writer
     */
    public IOPaths(FileObject projectfolder, FileObject contentfolder, FileObject cachefolder, FileObject outfolder,
            OutputWriter msg, OutputWriter err) {
        this(projectfolder, contentfolder, null, cachefolder, outfolder, null, null, msg, err);
    }

    private IOPaths(FileObject projectfolder, FileObject contentfolder, FileObject sharedcontentfolder, FileObject cachefolder, FileObject outfolder,
            FileObject resourcesfolder, String relativepath, OutputWriter msg, OutputWriter err) {
        this.projectfolder = projectfolder;
        this.contentfolder = contentfolder;
        this.sharedcontentfolder = sharedcontentfolder;
        this.cachefolder = cachefolder;
        this.outfolder = outfolder;
        this.resourcesfolder = resourcesfolder;
        this.relativepath = relativepath;
        this.msg = msg;
        this.err = err;
    }

    /**
     * Update the path to be used for the content folder.
     * 
     * @param path the incremental child folder to be added to the connect folder path
     * @return The new IOPaths instance.
     */
    public IOPaths updatePath(String path) {
        return new IOPaths(getProjectfolder(), contentfolder.getFileObject(path),
                sharedcontentfolder == null ? contentfolder : sharedcontentfolder,
                useOrCreateFolder(cachefolder, path), outfolder,
                resourcesfolder, relativepath, getMsg(), getErr());
    }

    /**
     * Update the paths to be used for the content folder, cache folder and output folder.
     * 
     * @param path the incremental child folder to be added to the content folder, cache folder and output folder path
     * @return The new IOPaths instance.
     */
    public IOPaths updateBothPath(String path) {
        return new IOPaths(getProjectfolder(), contentfolder.getFileObject(path),
                sharedcontentfolder == null ? contentfolder : sharedcontentfolder,
                useOrCreateFolder(cachefolder, path),
                useOrCreateFolder(outfolder, path),
                resourcesfolder, relativepath, getMsg(), getErr());
    }

    private FileObject useOrCreateFolder(FileObject parent, String... foldernames) {
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

    /**
     * Get the project folder.
     * 
     * @return the projectfolder
     */
    public FileObject getProjectfolder() {
        return projectfolder;
    }

    /**
     * Get the content folder
     * 
     * @return the contentfolder
     */
    public FileObject getContentfolder() {
        return contentfolder;
    }

    /**
     * Get the shared content folder
     * 
     * @return the sharedcontentfolder
     */
    public FileObject getSharedcontentfolder() {
        return sharedcontentfolder;
    }

    /**
     * Get the cache folder
     * 
     * @return the cachefolder
     */
    public FileObject getCachefolder() {
        return cachefolder;
    }

    /**
     * Get the output folder
     * 
     * @return the outfolder
     */
    public FileObject getOutfolder() {
        return outfolder;
    }

    /**
     * Get the output folder path
     * 
     * @return the output folder path
     */
    public String getOutPath() {
        return outfolder.getPath();
    }

    /**
     * Get the resources folder
     * 
     * @return the resourcesfolder
     */
    public FileObject getResourcesfolder() {
        return resourcesfolder;
    }

    /**
     * Set the resources folder.
     * 
     * @param root the root folder for target resources
     * @param foldername the folder name for resources
     * @return the resources folder
     */
    public FileObject setResourcesfolder(FileObject root, String foldername) {
        relativepath = foldername;
        resourcesfolder = useOrCreateFolder(root, relativepath);
        return resourcesfolder;
    }

    /**
     * Get the relative path to the resources folder
     * 
     * @return the relativepath
     */
    public String getRelativepath() {
        return relativepath + '/';
    }

    /**
     * Get the message writer
     * 
     * @return the msg writer
     */
    public OutputWriter getMsg() {
        return msg;
    }

    /**
     * Get the error writer
     * 
     * @return the error writer
     */
    public OutputWriter getErr() {
        return err;
    }
}
