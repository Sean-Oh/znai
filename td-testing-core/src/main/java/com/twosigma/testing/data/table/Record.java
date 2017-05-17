package com.twosigma.testing.data.table;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author mykola
 */
public class Record {
    private Header header;
    private List<Object> values;
    private CompositeKey key;

    public Record(Header header, Stream<Object> values) {
        this.header = header;
        this.values = values.collect(toList());
        this.key = header.hasKeyColumns() ? new CompositeKey(header.keyIdx().map(this::valueByIdx)): null;
    }

    public Header getHeader() {
        return header;
    }

    public CompositeKey getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public <E> E get(String name) {
        return (E) values.get(header.columnIdxByName(name));
    }

    public Object valueByIdx(int idx) {
        header.validateIdx(idx);
        return values.get(idx);
    }

    public Stream<Object> values() {
        return values.stream();
    }

    @SuppressWarnings("unchecked")
    public  <T, R> Stream<R> mapValues(Function<T, R> mapper) {
        return values.stream().map(v -> mapper.apply((T) v));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        header.columnIdxStream().forEach(i -> result.put(header.columnNameByIdx(i), values.get(i)));

        return result;
    }

    @Override
    public String toString() {
        return toMap().toString();
    }
}
