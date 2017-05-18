package com.twosigma.testing.expectation.equality.handlers

import com.twosigma.testing.expectation.equality.EqualComparator
import org.junit.Test

import static com.twosigma.testing.data.table.TableData.header
import static com.twosigma.testing.expectation.ActualPath.createActualPath
import static com.twosigma.testing.expectation.ActualValue.actual
import static com.twosigma.testing.Ddjt.equal

/**
 * @author mykola
 */
class IterableAndTableDataEqualHandlerTest {
    @Test
    void "should compare list of beans and table data"() {
        def beans = [new SimpleBean(price: 2, lot: 2, symbol: "SA"),
                     new SimpleBean(price: 2, lot: 2, symbol: "SB")]

        actual(beans).should(equal(header("symbol", "price", "lot").values(
                                              "SA",      2,     2,
                                              "SB",      2,     2)))
    }

    @Test
    void "should report table mismatch"() {
        def beans = [new SimpleBean(price: 2, lot: 1, symbol: "SA"),
                     new SimpleBean(price: 1, lot: 2, symbol: "SB")]

        def expected = header("symbol", "price", "lot").values(
                "SA", 2, 2,
                "SB", 2, 2)

        def equalComparator = EqualComparator.comparator()
        equalComparator.compare(createActualPath("beans"), beans, expected)

        assert ! equalComparator.areEqual()

        def report = equalComparator.generateMismatchReport()
        assert report.contains("lot:   actual: 1.0")
    }

    static class SimpleBean {
        double price
        double lot
        String symbol
    }
}
