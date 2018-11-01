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

/**
 *
 * @author richard
 */
public class Using {
    
    private final String name;
    private final Build content;
    
    public Using(String name, Build content) {
        this.name = name;
        this.content = content;
    }
    
    public String getName() {
        return name;
    }
    
    public String getContentString() throws IOException {
        return content.getContentString();
    }
}
