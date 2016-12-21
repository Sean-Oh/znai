package com.twosigma.console;

import com.twosigma.utils.ServiceLoaderUtils;

import java.util.List;

/**
 * @author mykola
 */
public class ConsoleOutputs {
    private static List<ConsoleOutput> outputs = ServiceLoaderUtils.load(ConsoleOutput.class);

    public static void out(Object... styleOrValues) {
        outputs.forEach(o -> o.out(styleOrValues));
    }

    public static void err(Object... styleOrValues) {
        outputs.forEach(o -> o.err(styleOrValues));
    }

    public static void add(ConsoleOutput consoleOutput) {
        outputs.add(consoleOutput);
    }

    public static void remove(ConsoleOutput consoleOutput) {
        outputs.remove(consoleOutput);
    }
}
