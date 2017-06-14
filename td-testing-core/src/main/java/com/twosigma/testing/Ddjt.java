package com.twosigma.testing;

import com.twosigma.testing.data.table.TableData;
import com.twosigma.testing.expectation.*;
import com.twosigma.testing.expectation.code.ThrowExceptionMatcher;
import com.twosigma.testing.expectation.equality.EqualMatcher;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Data Driven Java Testing
 * <p>
 * Convenient class for a single static * imports
 * @author mykola
 */
public class Ddjt {
    private Ddjt() {
    }

    public static TableData header(String... columnNames) {
        return new TableData(Arrays.stream(columnNames));
    }

    public static ActualValueExpectations actual(Object actual) {
        return new ActualValue(actual);
    }

    public static ActualCodeExpectations code(CodeBlock codeBlock) {
        return new ActualCode(codeBlock);
    }

    public static EqualMatcher equal(Object expected) {
        return new EqualMatcher(expected);
    }

    public static ThrowExceptionMatcher throwException(String expectedMessage) {
        return new ThrowExceptionMatcher(expectedMessage);
    }

    public static ThrowExceptionMatcher throwException(Pattern expectedMessageRegexp) {
        return new ThrowExceptionMatcher(expectedMessageRegexp);
    }

    public static ThrowExceptionMatcher throwException(Class expectedClass) {
        return new ThrowExceptionMatcher(expectedClass);
    }

    public static ThrowExceptionMatcher throwException(Class expectedClass, Pattern expectedMessageRegexp) {
        return new ThrowExceptionMatcher(expectedClass, expectedMessageRegexp);
    }

    public static ThrowExceptionMatcher throwException(Class expectedClass, String expectedMessage) {
        return new ThrowExceptionMatcher(expectedClass, expectedMessage);
    }

    public static ActualPath createActualPath(String path) {
        return new ActualPath(path);
    }
}
