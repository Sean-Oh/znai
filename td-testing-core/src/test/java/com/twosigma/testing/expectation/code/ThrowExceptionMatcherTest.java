package com.twosigma.testing.expectation.code;

import org.junit.Test;

import java.util.regex.Pattern;

import static com.twosigma.testing.Ddjt.code;
import static com.twosigma.testing.Ddjt.throwException;

/**
 * @author mykola
 */
public class ThrowExceptionMatcherTest {
    @Test
    public void examples() {
        code(() -> {
            businessLogic(-10);
        }).should(throwException(IllegalArgumentException.class, "negative are not allowed"));

        code(() -> {
            businessLogic(-10);
        }).should(throwException(IllegalArgumentException.class, Pattern.compile("negative .* not allowed")));

        code(() -> {
            businessLogic(-10);
        }).should(throwException(IllegalArgumentException.class));
    }

    private static void businessLogic(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("negative are not allowed");
        }
    }
}