package com.twosigma.testing.expectation;

/**
 * @author mykola
 */
public class ActualPath {
    private String path;

    public static ActualPath createActualPath(String path) {
        return new ActualPath(path);
    }

    private ActualPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        this.path = path;
    }

    public ActualPath property(String propName) {
        return new ActualPath(isEmpty() ? propName : path + "." + propName);
    }

    public ActualPath index(int idx) {
        return new ActualPath(path + "[" + idx + "]");
    }

    public String getPath() {
        return path;
    }

    private String attachToPath(String suffix) {
        return isEmpty() ? suffix : path + suffix;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override
    public String toString() {
        return path;
    }
}
