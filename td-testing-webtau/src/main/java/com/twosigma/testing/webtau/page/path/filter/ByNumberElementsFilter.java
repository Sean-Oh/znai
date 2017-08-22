package com.twosigma.testing.webtau.page.path.filter;

import com.twosigma.testing.reporter.TokenizedMessage;
import com.twosigma.testing.webtau.page.path.ElementsFilter;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

import static com.twosigma.testing.reporter.TokenizedMessage.tokenizedMessage;
import static com.twosigma.testing.reporter.IntegrationTestsMessageBuilder.selectorType;
import static com.twosigma.testing.reporter.IntegrationTestsMessageBuilder.selectorValue;

/**
 * @author mykola
 */
public class ByNumberElementsFilter implements ElementsFilter {
    private int number;

    public ByNumberElementsFilter(int number) {
        this.number = number;
    }

    @Override
    public List<WebElement> filter(List<WebElement> original) {
        return (number > 0 && number <= original.size()) ?
                Collections.singletonList(original.get(number - 1)):
                Collections.emptyList();
    }

    @Override
    public TokenizedMessage description() {
        return tokenizedMessage(selectorType("element number"), selectorValue(String.valueOf(number)));
    }
}
