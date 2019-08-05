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

import React from 'react'
import ApiParameter from './ApiParameter'

import './ApiParameters.css'

export default function ApiParameters({parameters, nestedLevel, parentWidth = 0, elementsLibrary}) {
    const renderedParameters = parameters.map(p => <ApiParameter key={p.name}
                                                                 name={p.name}
                                                                 type={p.type}
                                                                 isExpanded={false}
                                                                 children={p.children}
                                                                 description={p.description}
                                                                 nestedLevel={nestedLevel}
                                                                 elementsLibrary={elementsLibrary}/>)

    const isNested = nestedLevel > 0
    const className = 'api-parameters' + (isNested ? ' nested' : '')
    const style = {marginLeft: -parentWidth}

    return (
        <div className={className} style={style}>
            {renderedParameters}
        </div>
    )
}