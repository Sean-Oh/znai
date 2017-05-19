package com.twosigma.testing.data.table.comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.twosigma.testing.data.table.Record;
import com.twosigma.testing.data.table.TableData;

/**
 * @author mykola
 */
public class TableDataComparisonResult {
    private Map<Integer, Map<String, String>> messageByActualRowIdxAndColumn;
    private Map<Integer, Map<String, String>> messageByExpectedRowIdxAndColumn;

    private Set<String> missingColumns;
    private TableData missingRows;
    private TableData extraRows;

    private TableData actual;
    private TableData expected;

    public TableDataComparisonResult(TableData actual, TableData expected) {
        this.actual = actual;
        this.expected = expected;

        messageByActualRowIdxAndColumn = new HashMap<>();
        messageByExpectedRowIdxAndColumn = new HashMap<>();

        missingColumns = new TreeSet<>();
        missingRows = new TableData(expected.getHeader());
        extraRows = new TableData(actual.getHeader());
    }

    public boolean areEqual() {
        return messageByActualRowIdxAndColumn.isEmpty() &&
            missingColumns.isEmpty() &&
            missingRows.isEmpty() &&
            extraRows.isEmpty();
    }

    public TableData getActual() {
        return actual;
    }

    public TableData getExpected() {
        return expected;
    }

    public void addMissingColumn(String name) {
        missingColumns.add(name);
    }

    public void addExtraRow(Record row) {
        extraRows.addRow(row);
    }

    public void addMissingRow(Record row) {
        missingRows.addRow(row);
    }

    public Set<String> getMissingColumns() {
        return missingColumns;
    }

    public void setMissingColumns(final Set<String> missingColumns) {
        this.missingColumns = missingColumns;
    }

    public TableData getMissingRows() {
        return missingRows;
    }

    public TableData getExtraRows() {
        return extraRows;
    }

    // TODO keys support
    public void addMismatch(int actualRowIdx, int expectedRowIdx, String columnName, String message) {
        addMismatch(messageByActualRowIdxAndColumn, actualRowIdx, columnName, message);
        addMismatch(messageByExpectedRowIdxAndColumn, expectedRowIdx, columnName, message);
    }

    public String getActualMismatch(int rowIdx, String columnName) {
        return getMismatch(messageByActualRowIdxAndColumn, rowIdx, columnName);
    }

    public String getExpectedMismatch(int rowIdx, String columnName) {
        return getMismatch(messageByExpectedRowIdxAndColumn, rowIdx, columnName);
    }

    private String getMismatch(Map<Integer, Map<String, String>> messagesByRowIdx, int rowIdx, String columnName) {
        Map<String, String> byRow = messagesByRowIdx.get(rowIdx);
        if (byRow == null) {
            return null;
        }

        return byRow.get(columnName);
    }

    private void addMismatch(Map<Integer, Map<String, String>> messagesByRowIdx, int rowIdx, String columnName, String message) {
        Map<String, String> byRow = messagesByRowIdx.computeIfAbsent(rowIdx, k -> new HashMap<>());

        byRow.put(columnName, message);
    }

}
