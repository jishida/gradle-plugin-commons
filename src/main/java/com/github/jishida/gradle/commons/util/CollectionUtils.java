package com.github.jishida.gradle.commons.util;

import java.util.*;

public final class CollectionUtils {
    public static <T> Enumerable<T> enumerable(final Iterable<T> iterable) {
        return new Enumerable<T>(iterable);
    }

    public static <T> Enumerable<T> enumerable(final T... values) {
        return new Enumerable<T>(new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < values.length;
                    }

                    @Override
                    public T next() {
                        if (index >= values.length) {
                            throw new NoSuchElementException();
                        }
                        return values[index++];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> copy(final Iterable<T> iter) {
        return copy(iter, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> copy(final Map<K, V> map) {
        return copy(map, LinkedHashMap.class);
    }

    @SuppressWarnings("unchecked")
    public static <T, C extends Collection> C copy(final Iterable<T> iter, final Class<C> collectionClass) {
        C result = null;
        try {
            result = collectionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        for (Object item : iter) {
            result.add(item);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <K, V, M extends Map> M copy(final Map<K, V> map, Class<M> mapClass) {
        M result = null;
        try {
            result = mapClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        for (Object entry : result.entrySet()) {
            Map.Entry<K, V> e = (Map.Entry<K, V>) entry;
            result.put(e.getKey(), e.getValue());
        }
        return result;
    }
}
