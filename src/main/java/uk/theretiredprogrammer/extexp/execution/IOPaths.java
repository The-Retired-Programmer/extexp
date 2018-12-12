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
package uk.theretiredprogrammer.extexp.execution;

import uk.theretiredprogrammer.extexp.execution.IoUtil;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.windows.OutputWriter;

/**
 *
 * @author richard
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
    
    public IOPaths(FileObject projectfolder, FileObject contentfolder, FileObject cachefolder, FileObject outfolder,
            FileObject resourcesfolder, String relativepath,OutputWriter msg, OutputWriter err) {
        this(projectfolder, contentfolder, null, cachefolder,outfolder,
                resourcesfolder, relativepath, msg, err);
    }
    
    private IOPaths(FileObject projectfolder, FileObject contentfolder, FileObject sharedcontentfolder,FileObject cachefolder, FileObject outfolder,
            FileObject resourcesfolder, String relativepath,OutputWriter msg, OutputWriter err) {
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
    
    public IOPaths updatePath(String path) throws IOException {
        return new IOPaths(getProjectfolder(), contentfolder.getFileObject(path),
                sharedcontentfolder == null ? contentfolder : sharedcontentfolder,
                IoUtil.useOrCreateFolder(cachefolder, path), outfolder,
                resourcesfolder,relativepath, getMsg(), getErr());
    }
    
    /**
     * @return the projectfolder
     */
    public FileObject getProjectfolder() {
        return projectfolder;
    }
    
    /**
     * @return the contentfolder
     */
    public FileObject getContentfolder() {
        return contentfolder;
    }

    /**
     * @return the sharedcontentfolder
     */
    public FileObject getSharedcontentfolder() {
        return sharedcontentfolder;
    }

    /**
     * @return the cachefolder
     */
    public FileObject getCachefolder() {
        return cachefolder;
    }

    /**
     * @return the outfolder
     */
    public FileObject getOutfolder() {
        return outfolder;
    }
    
    public String getOutPath() {
        return outfolder.getPath();
    }

    /**
     * @return the resourcesfolder
     */
    public FileObject getResourcesfolder() {
        return resourcesfolder;
    }
    
    public FileObject setResourcesfolder(FileObject root, String foldername) throws IOException  {
        relativepath = foldername;
        resourcesfolder = IoUtil.useOrCreateFolder(root, relativepath);
        return resourcesfolder;
    }

    /**
     * @return the relativepath
     */
    public String getRelativepath() {
        return relativepath+'/';
    }
    
    /**
     * @return the msg
     */
    public OutputWriter getMsg() {
        return msg;
    }

    /**
     * @return the err
     */
    public OutputWriter getErr() {
        return err;
    }
}
