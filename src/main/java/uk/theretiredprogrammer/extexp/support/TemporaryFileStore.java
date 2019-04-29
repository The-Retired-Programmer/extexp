/*
 * Copyright 2018-2019 richard.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An In-Memory Filesystem for temporary file storage.
 *
 * @author richard linsdale
 */
public class TemporaryFileStore {

    private final Map<String, String> tempstore = new HashMap<>();

    /**
     * Insert file content into the store.
     *
     * @param name the filename
     * @param content the file content
     */
    public void put(String name, String content) {
        tempstore.put(name, content);
    }

    /**
     * Get the file content.
     *
     * @param name the filename
     * @return the file content
     */
    public Optional<String> get(String name) {
        return Optional.ofNullable(tempstore.get(name));
    }

    /**
     * Get the file content.
     *
     * @param name the filename
     * @return the file content
     */
    public Optional<String> get(Optional<String> name) {
        return name.map(n -> tempstore.get(n));
    }

    /**
     * Get all filenames in the temporary filestore.
     *
     * @return set of all filenames
     */
    public Set<String> getAllNames() {
        return tempstore.keySet();
    }
}
