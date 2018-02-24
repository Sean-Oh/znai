package com.twosigma.documentation.extensions.code;

import com.google.gson.Gson;
import com.twosigma.documentation.core.ComponentsRegistry;
import com.twosigma.documentation.extensions.include.IncludeContext;
import com.twosigma.documentation.extensions.PluginParams;
import com.twosigma.documentation.extensions.include.IncludePlugin;
import com.twosigma.documentation.extensions.PluginResult;
import com.twosigma.documentation.parser.ParserHandler;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.twosigma.utils.FileUtils.fileTextContent;
import static java.util.stream.Collectors.toList;

/**
 * @author mykola
 */
public class CodeExecutionResultIncludePlugin implements IncludePlugin {
    private IncludeContext context;

    private Map<Path, List<SnippetAndResult>> snippetAndResultsByPath;
    private Map<Path, Integer> currentSnippetIdxByPath;

    private SnippetAndResult emptySnippetAndResult = new SnippetAndResult("no-snippet", "no-result");

    @Override
    public String id() {
        return "code-execution-result";
    }

    @Override
    public void reset(final IncludeContext context) {
        this.context = context;

        snippetAndResultsByPath = new HashMap<>();
        currentSnippetIdxByPath = new HashMap<>();
    }

    @Override
    public PluginResult process(ComponentsRegistry componentsRegistry, ParserHandler parserHandler, Path markupPath, final PluginParams pluginParams) {
        return PluginResult.docElement("TestComponent", Collections.emptyMap());
//        final String fileName = pluginParams.getFreeParam();
//        final Path fullPath = context.getCurrentFilePath().getParent().resolve(fileName + ".json").toAbsolutePath();
//
//        List<SnippetAndResult> snippetAndResults = getSnippets(fullPath);
//        Integer snippetIdx = currentSnippetIdx(fullPath);
//
//        SnippetAndResult currentSnippet = snippetIdx >= snippetAndResults.size() ?
//            emptySnippetAndResult :
//            snippetAndResults.get(snippetIdx);
//
//        incrementCurrentSnippetIdx(fullPath);
//
//        return IncludeResult.inContext(context).withHtml(
//            "<pre class='prettyprint'>" + currentSnippet.getSnippet() + "</pre>" +
//                "<pre>" + currentSnippet.getResult() + "</pre>").andUsedFile(fullPath);
    }

    private void incrementCurrentSnippetIdx(final Path fullPath) {
        currentSnippetIdxByPath.put(fullPath, currentSnippetIdxByPath.get(fullPath) + 1);
    }

    private Integer currentSnippetIdx(final Path fullPath) {
        return currentSnippetIdxByPath.computeIfAbsent(fullPath, k -> 0);
    }

    private List<SnippetAndResult> getSnippets(final Path fullPath) {
        List<SnippetAndResult> snippetAndResults = snippetAndResultsByPath.get(fullPath);
        if (snippetAndResults == null) {
            snippetAndResults = parse(fileTextContent(fullPath));
            snippetAndResultsByPath.put(fullPath, snippetAndResults);
        }

        return snippetAndResults;
    }

    private List<SnippetAndResult> parse(String json) {
        final Gson gson = new Gson();
        final List<?> list = gson.fromJson(json, List.class);

        final Function<Object, SnippetAndResult> function =
            e -> new SnippetAndResult(((Map) e).get("snippet").toString(), ((Map) e).get("result").toString());

        return list.stream().map(function).
            collect(toList());
    }
}
