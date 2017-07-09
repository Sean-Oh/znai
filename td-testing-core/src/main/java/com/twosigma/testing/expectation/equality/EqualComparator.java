package com.twosigma.testing.expectation.equality;

import com.twosigma.testing.data.render.DataRenderers;
import com.twosigma.testing.expectation.ActualPath;
import com.twosigma.testing.expectation.equality.handlers.AnyEqualHandler;
import com.twosigma.testing.expectation.equality.handlers.NullEqualHandler;
import com.twosigma.utils.ServiceUtils;
import com.twosigma.utils.TraceUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * @author mykola
 */
public class EqualComparator {
    private static List<EqualComparatorHandler> handlers = discoverHandlers();

    private List<Mismatch> mismatches = new ArrayList<>();
    private List<ActualPathWithValue> missing = new ArrayList<>();
    private List<ActualPathWithValue> extra = new ArrayList<>();

    private final boolean isNegative;

    public static EqualComparator comparator() {
        return new EqualComparator(false);
    }

    public static EqualComparator negativeComparator() {
        return new EqualComparator(true);
    }

    /**
     * creates new instance of comparator without any collected mismatches. Preserves isNegative switch.
     * @return fresh copy of equal comparator
     */
    public EqualComparator freshCopy() {
        return new EqualComparator(isNegative);
    }

    private EqualComparator(final boolean isNegative) {
        this.isNegative = isNegative;
    }

    public ComparatorResult compare(ActualPath actualPath, Object actual, Object expected) {
        EqualComparatorHandler handler = handlers.stream().
            filter(h -> h.handle(actual, expected)).findFirst().
            orElseThrow(() -> noHandlerFound(actual, expected));

        int before = getNumberOfIssues();
        handler.compare(this, actualPath, actual, expected);
        int after = getNumberOfIssues();

        return new ComparatorResult(after != before);
    }

    /**
     * comparator operates in two mode: should and shouldNot
     * @return true if the mode is shouldNot
     */
    public boolean isNegative() {
        return isNegative;
    }

    public boolean areEqual() {
        boolean isMismatch = mismatches.isEmpty() && missing.isEmpty() && extra.isEmpty();
        return isNegative() != isMismatch;
    }

    public String generateMismatchReport() {
        List<String> reports = new ArrayList<>();
        if (! mismatches.isEmpty()) {
            reports.add("mismatches:\n\n" +
                    mismatches.stream().map(Mismatch::fullMessage).collect(joining("\n")));
        }

        if (! missing.isEmpty()) {
            reports.add("missing, but expected values:\n\n" +
                    missing.stream().map(ActualPathWithValue::getFullMessage).collect(joining("\n")));
        }

        if (! extra.isEmpty()) {
            reports.add("unexpected values:\n\n" +
                    extra.stream().map(ActualPathWithValue::getFullMessage).collect(joining("\n")));
        }

        return reports.stream().collect(joining("\n\n"));
    }

    public void reportMismatch(EqualComparatorHandler reporter, ActualPath actualPath, String mismatch) {
        mismatches.add(new Mismatch(actualPath, mismatch));
    }

    public void reportMissing(EqualComparatorHandler reporter, ActualPath actualPath, Object value) {
        missing.add(new ActualPathWithValue(actualPath, value));
    }

    public void reportExtra(EqualComparatorHandler reporter, ActualPath actualPath, Object value) {
        extra.add(new ActualPathWithValue(actualPath, value));
    }

    public int getNumberOfMismatches() {
        return mismatches.size();
    }

    public int getNumberOfMissing() {
        return missing.size();
    }

    public int getNumberOfExtra() {
        return extra.size();
    }

    public int getNumberOfIssues() {
        return getNumberOfMismatches() + getNumberOfExtra() + getNumberOfMissing();
    }

    private static List<EqualComparatorHandler> discoverHandlers() {
        List<EqualComparatorHandler> result = new ArrayList<>();

        List<EqualComparatorHandler> discovered = ServiceUtils.discover(EqualComparatorHandler.class);

        discovered.stream().filter(EqualComparatorHandler::handleNulls).forEach(result::add);
        result.add(new NullEqualHandler());
        discovered.stream().filter(h -> ! h.handleNulls()).forEach(result::add);

        result.add(new AnyEqualHandler());

        return result;
    }

    private RuntimeException noHandlerFound(Object actual, Object expected) {
        return new RuntimeException(
            "no equal comparator handler found for\nactual: " + DataRenderers.render(actual) + " " + TraceUtils.renderType(actual) +
            "\nexpected: " + DataRenderers.render(expected) + " " + TraceUtils.renderType(expected));
    }
}
