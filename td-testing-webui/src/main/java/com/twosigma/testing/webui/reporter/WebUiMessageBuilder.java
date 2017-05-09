package com.twosigma.testing.webui.reporter;

import com.twosigma.console.ansi.Color;
import com.twosigma.console.ansi.FontStyle;
import com.twosigma.testing.reporter.MessageToken;
import com.twosigma.testing.reporter.TokenizedMessageToAnsiConverter;

import java.util.Arrays;

/**
 * @author mykola
 */
public class WebUiMessageBuilder {
    private enum TokenTypes {
        ERROR("error", Color.RED),
        NONE("none", FontStyle.NORMAL),
        ACTION("action", Color.BLUE),
        ID("id", FontStyle.BOLD),
        CLASSIFIER("classifier", Color.CYAN),
        MATCHER("matcher", Color.GREEN),
        STRING_VALUE("stringValue", Color.GREEN),
        URL("url", Color.PURPLE),
        SELECTOR_TYPE("selectorType", Color.PURPLE),
        SELECTOR_VALUE("selectorValue", FontStyle.BOLD, Color.PURPLE),
        PREPOSITION("preposition", Color.BLACK),
        DELIMITER("delimiter", Color.WHITE);

        private final String type;
        private final boolean delimiterAfter;
        private final Object[] styles;

        TokenTypes(String type, Object... styles) {
            this(type, true, styles);
        }

        TokenTypes(String type, boolean delimiterAfter, Object... styles) {
            this.type = type;
            this.delimiterAfter = delimiterAfter;
            this.styles = styles;
        }

        public MessageToken token(Object value) {
            return new MessageToken(type, value);
        }
    }

    public static final MessageToken TO = TokenTypes.PREPOSITION.token("to");
    public static final MessageToken OF = TokenTypes.PREPOSITION.token("of");
    public static final MessageToken COMMA = TokenTypes.DELIMITER.token(",");

    private static final TokenizedMessageToAnsiConverter converter = createConverter();

    public static MessageToken id(String value) {
        return TokenTypes.ID.token(value);
    }

    public static MessageToken classifier(String value) {
        return TokenTypes.CLASSIFIER.token(value);
    }

    public static MessageToken stringValue(Object value) {
        return TokenTypes.STRING_VALUE.token(escapeSpecialChars(value.toString()));
    }

    public static MessageToken urlValue(String url) {
        return TokenTypes.URL.token(url);
    }

    public static MessageToken action(String action) {
        return TokenTypes.ACTION.token(action);
    }

    public static MessageToken matcher(String matcher) {
        return TokenTypes.MATCHER.token(matcher);
    }

    public static MessageToken none(String text) {
        return TokenTypes.NONE.token(text);
    }

    public static MessageToken selectorType(String selector) {
        return TokenTypes.SELECTOR_TYPE.token(selector);
    }

    public static MessageToken selectorValue(String selector) {
        return TokenTypes.SELECTOR_VALUE.token(selector);
    }

    public static TokenizedMessageToAnsiConverter getConverter() {
        return converter;
    }

    private static Object escapeSpecialChars(String text) {
        return text.replace("\n", "\\n");
    }

    private static TokenizedMessageToAnsiConverter createConverter() {
        TokenizedMessageToAnsiConverter c = new TokenizedMessageToAnsiConverter();
        Arrays.stream(TokenTypes.values()).forEach(t -> c.associate(t.type, t.delimiterAfter, t.styles));

        return c;
    }
}
