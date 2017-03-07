package com.twosigma.documentation.parser

import com.twosigma.documentation.codesnippets.CodeTokenizer
import com.twosigma.documentation.core.ComponentsRegistry
import com.twosigma.documentation.extensions.PluginResourcesResolver

/**
 * @author mykola
 */
class TestComponentsRegistry implements ComponentsRegistry {
    @Override
    public MarkupParser parser() {
        return null;
    }

    @Override
    CodeTokenizer codeTokenizer() {
        return new TestCodeTokenizer();
    }

    @Override
    public PluginResourcesResolver includeResourceResolver() {
        return new TestResourceResolver();
    }
}