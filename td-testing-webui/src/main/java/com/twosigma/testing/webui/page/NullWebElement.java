package com.twosigma.testing.webui.page;

import org.openqa.selenium.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author mykola
 */
public class NullWebElement implements WebElement {
    private static final String NULL_VALUE = "[null value] element is not present on the page";
    private String id;

    public NullWebElement(String id) {
        this.id = id;
    }

    @Override
    public void click() {
        error("click");
    }

    @Override
    public void submit() {
        error("submit");
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        error("send " + Arrays.toString(charSequences) + " keys");
    }

    @Override
    public void clear() {
        error("clear");
    }

    @Override
    public String getTagName() {
        return NULL_VALUE;
    }

    @Override
    public String getAttribute(String s) {
        return NULL_VALUE;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return NULL_VALUE;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return Collections.emptyList();
    }

    @Override
    public WebElement findElement(By by) {
        return new NullWebElement(by.toString());
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public Point getLocation() {
        return new Point(0, 0);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public String getCssValue(String s) {
        return NULL_VALUE;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        error("screenshotAs");
        return null;
    }

    private void error(String action) {
        throw new RuntimeException("can't " + action + " as element is not found: " + id + ". Try to wait for it to appear first.");
    }
}
