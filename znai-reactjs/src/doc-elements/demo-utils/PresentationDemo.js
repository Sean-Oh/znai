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

import {elementsLibrary, presentationElementHandlers} from '../DefaultElementsLibrary'
import PresentationRegistry from '../presentation/PresentationRegistry'
import Presentation from '../presentation/Presentation'

const defaultDocMeta = {id: "znai", title: "Znai", type: "User Guide"}

export function createPresentationDemo(content, cfg = {docMeta: defaultDocMeta, slideIdx: 0}) {
    const presentationRegistry = new PresentationRegistry(elementsLibrary, presentationElementHandlers, content)
    return () => (
        <Presentation docMeta={cfg.docMeta}
                      presentationRegistry={presentationRegistry}
                      slideIdx={cfg.slideIdx}
                      onNextPage={noOp}
                      onPrevPage={noOp}/>)
}

function noOp() {
}