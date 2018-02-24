package com.twosigma.documentation.parser

import com.twosigma.documentation.structure.DocStructure
import com.twosigma.documentation.structure.DocUrl

import java.nio.file.Path

/**
 * @author mykola
 */
class TestDocStructure implements DocStructure {
    private Set<String> validLinks = [] as Set

    @Override
    void validateUrl(Path path, String sectionWithLinkTitle, DocUrl docUrl) {
        if (docUrl.isGlobalUrl()) {
            return
        }

        def urlBase = "${docUrl.dirName}/${docUrl.fileName}"
        def url = urlBase + (docUrl.anchorId.isEmpty() ? "" : "#${docUrl.anchorId}")

        if (! validLinks.contains(url.toString())) {
            throw new IllegalArgumentException("no valid link found in section '${sectionWithLinkTitle}': " + url)
        }
    }

    @Override
    String createUrl(DocUrl docUrl) {
        if (docUrl.isGlobalUrl()) {
            return docUrl.url
        }

        def base = "/test-doc/${docUrl.dirName}/${docUrl.fileName}"
        return base + (docUrl.anchorId ? "#${docUrl.anchorId}" : "")
    }

    @Override
    String prefixUrlWithProductId(String url) {
        return "/test-doc/${url}"
    }

    @Override
    void registerGlobalAnchor(Path sourcePath, String anchorId) {

    }

    @Override
    String globalAnchorUrl(Path clientPath, String anchorId) {
        return null
    }

    void addValidLink(String link) {
        validLinks.add(link)
    }
}
