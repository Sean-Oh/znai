package com.twosigma.documentation.openapi;

import com.twosigma.documentation.core.AuxiliaryFile;
import com.twosigma.documentation.core.ComponentsRegistry;
import com.twosigma.documentation.extensions.PluginParams;
import com.twosigma.documentation.extensions.PluginResult;
import com.twosigma.documentation.extensions.include.IncludePlugin;
import com.twosigma.documentation.parser.ParserHandler;
import com.twosigma.utils.FileUtils;

import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class OpenApiOperationIncludePlugin implements IncludePlugin {
    private Path specPath;

    @Override
    public String id() {
        return "open-api-operation";
    }

    @Override
    public PluginResult process(ComponentsRegistry componentsRegistry,
                                ParserHandler parserHandler,
                                Path markupPath,
                                PluginParams pluginParams) {
        specPath = componentsRegistry.resourceResolver().fullPath(pluginParams.getFreeParam());

        OpenApiSpec openApiSpec = OpenApiSpec.fromJson(componentsRegistry.markdownParser(),
                FileUtils.fileTextContent(specPath));

        String operationId = pluginParams.getOpts().get("operationId");
        OpenApiOperation operation = openApiSpec.findOperationById(operationId);

        parserHandler.onGlobalAnchor(operationId);

        return PluginResult.docElement("OpenApiOperation",
                Collections.singletonMap("operation", operation.toMap()));
    }

    @Override
    public Stream<AuxiliaryFile> auxiliaryFiles(ComponentsRegistry componentsRegistry) {
        return Stream.of(AuxiliaryFile.builtTime(specPath));
    }
}
