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

import React, {Component} from 'react'

import {pageTypesRegistry} from './PageTypesRegistry'

class Page extends Component {
    render() {
        const {tocItem} = this.props

        const PageContent = pageTypesRegistry.pageContentComponent(tocItem)
        const PageBottomPadding = pageTypesRegistry.pageBottomPaddingComponent(tocItem)

        return (
            <React.Fragment>
                <div className="page-content">
                    <PageContent key={tocItem.pageTitle}
                                 {...this.props}/>
                </div>
                <PageBottomPadding/>
            </React.Fragment>
        )
    }

    shouldComponentUpdate(nextProps) {
        return this.props.previewEnabled ||
            this.props.tocItem.dirName !== nextProps.tocItem.dirName ||
            this.props.tocItem.fileName !== nextProps.tocItem.fileName
    }
}

const PresentationTitle = ({tocItem}) => {
    return <h1 className="presentation-title">{tocItem.pageTitle}</h1>
}

const presentationPageHandler = {component: PresentationTitle,
    numberOfSlides: () => 1,
    slideInfoProvider: ({tocItem}) => {return {pageTitle: tocItem.pageTitle}}}

export {Page, PresentationTitle, presentationPageHandler}