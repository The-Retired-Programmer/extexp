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
package uk.theretiredprogrammer.assemblybuilder;

import java.util.function.Function;
import org.openide.filesystems.FileObject;

/**
 *
 * @author richard
 */
public class ResourcesDescriptor {

    public final Function<String, String> parameterExtractor;
    public final FileObject resourcesFolder;
    public final String relativeResourcesPath;

    public ResourcesDescriptor(Function<String, String> parameterExtractor,
            FileObject resourcesFolder, String relativeResourcesPath) {
        this.parameterExtractor = parameterExtractor;
        this.resourcesFolder = resourcesFolder;
        this.relativeResourcesPath = relativeResourcesPath;
    }
}
