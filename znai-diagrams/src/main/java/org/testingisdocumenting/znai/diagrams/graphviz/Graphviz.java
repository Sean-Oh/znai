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

package org.testingisdocumenting.znai.diagrams.graphviz;

import org.testingisdocumenting.znai.diagrams.graphviz.meta.GraphvizShapeConfig;
import org.testingisdocumenting.znai.utils.ResourceUtils;

public class Graphviz {
    public static final GraphvizShapeConfig shapeConfig =
            new GraphvizShapeConfig(ResourceUtils.textContent("graphviz-shapes.json"));

    public static final GraphvizEngine graphvizEngine = new GraphvizEngine(shapeConfig)
            .registerRuntime(new InteractiveCmdGraphviz("dot"))
            .registerRuntime(new InteractiveCmdGraphviz("neato"));
}
