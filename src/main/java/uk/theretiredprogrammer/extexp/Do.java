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
package uk.theretiredprogrammer.extexp;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

/**
 *
 * @author richard
 */
public class Do {
    
    public static void substitute(String in, Function<String, String> getparam, Writer out) throws IOException {
        int p = in.indexOf("${");
        if (p == -1) {
            out.write(in);
            return;
        }
        String fragment = in.substring(0, p);
        if (fragment != null && !fragment.isEmpty()) {
            out.write(fragment);
        }
        int q = in.indexOf("}", p + 2);
        String name = in.substring(p + 2, q);

        fragment = getparam.apply(name);
        if (fragment != null && !fragment.isEmpty()) {
            substitute(fragment, getparam, out);
        }

        fragment = in.substring(q + 1);
        if (fragment != null && !fragment.isEmpty()) {
            substitute(fragment, getparam, out);
        }
    }
    
}
