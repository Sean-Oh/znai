/*
 * Copyright 2019 TWO SIGMA OPEN SOURCE, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.testingisdocumenting.znai.server.urlhandlers;

import org.testingisdocumenting.znai.utils.ServiceLoaderUtils;

import java.util.Set;
import java.util.stream.Stream;

public class UrlContentHandlers {
    private static Set<UrlContentHandler> handlers =
            ServiceLoaderUtils.load(UrlContentHandler.class);

    public static void add(UrlContentHandler provider) {
        handlers.add(provider);
    }

    public static Stream<UrlContentHandler> urlContentHandlers() {
        return handlers.stream();
    }
}
