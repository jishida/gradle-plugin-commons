package com.github.jishida.gradle.commons.util;

import com.github.jishida.gradle.commons.function.Factory;
import com.github.jishida.gradle.commons.function.Func;
import com.github.jishida.gradle.commons.function.Predicate;

import java.util.*;

public class Enumerable<T> implements Iterable<T> {
    private final Iterable<T> iterable;

    public Enumerable(final Iterable<T> iterable) {
        Checker.checkNull(iterable, "iterable");
        this.iterable = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    public Enumerable<T> where(final Predicate<T> predicate) {
        Checker.checkNull(predicate, "predicate");
        return new Enumerable<T>(new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                final Iterator<T> iter = iterable.iterator();

                return new Iterator<T>() {
                    private boolean end = false;
                    private boolean hasBuffer = false;
                    private T buffer;

                    private boolean nextBuffer() {
                        if (end) {
                            return false;
                        }
                        while (true) {
                            if (iter.hasNext()) {
                                buffer = iter.next();
                                if (predicate.invoke(buffer)) {
                                    hasBuffer = true;
                                    return true;
                                }
                            } else {
                                end = true;
                                buffer = null;
                                return false;
                            }
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return hasBuffer || nextBuffer();
                    }

                    @Override
                    public T next() {
                        if (!hasBuffer && !nextBuffer()) {
                            throw new NoSuchElementException();
                        }
                        hasBuffer = false;
                        return buffer;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                };
            }
        });
    }

    public <U> Enumerable<U> select(final Func<T, U> selector) {
        Checker.checkNull(selector, "selector");

        return new Enumerable<U>(new Iterable<U>() {
            @Override
            public Iterator<U> iterator() {
                final Iterator<T> iter = iterable.iterator();

                return new Iterator<U>() {
                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public U next() {
                        return selector.invoke(iter.next());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                };
            }
        });
    }

    public Enumerable<T> reverse() {
        return new Enumerable<T>(new Iterable<T>() {
            private final List<T> list = toList();

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int index = list.size() - 1;

                    @Override
                    public boolean hasNext() {
                        return index >= 0;
                    }

                    @Override
                    public T next() {
                        if (index < 0) {
                            throw new NoSuchElementException();
                        }
                        return list.get(index--);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove");
                    }
                };
            }
        });
    }

    public T first(final T defaultValue) {
        final Iterator<T> iter = iterable.iterator();
        if (!iter.hasNext()) {
            return defaultValue;
        }
        return iter.next();
    }

    public T first(final Factory<T> defaultFactory) {
        Checker.checkNull(defaultFactory, "defaultFactory");
        final Iterator<T> iter = iterable.iterator();
        if (!iter.hasNext()) {
            return defaultFactory.create();
        }
        return iter.next();
    }

    public T first() {
        final Iterator<T> iter = iterable.iterator();
        if (!iter.hasNext()) {
            throw new IllegalArgumentException();
        }
        return iter.next();
    }

    public T firstOrNull() {
        final Iterator<T> iter = iterable.iterator();
        return iter.hasNext() ? iter.next() : null;
    }

    public T last() {
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            found = true;
            result = item;
        }
        if (!found) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    public T last(final T defaultValue) {
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            found = true;
            result = item;
        }
        if (!found) {
            return defaultValue;
        }
        return result;
    }

    public T last(final Factory<T> defaultFactory) {
        Checker.checkNull(defaultFactory, "defaultFactory");
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            found = true;
            result = item;
        }
        if (!found) {
            return defaultFactory.create();
        }
        return result;
    }

    public T lastOrNull() {
        T result = null;
        for (T item : iterable) {
            result = item;
        }
        return result;
    }

    public T single() {
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            if (found) {
                throw new IllegalArgumentException();
            }
            found = true;
            result = item;
        }
        if (!found) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    public T single(final T defaultValue) {
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            if (found) {
                throw new IllegalArgumentException();
            }
            found = true;
            result = item;
        }
        if (!found) {
            return defaultValue;
        }
        return result;
    }

    public T single(final Factory<T> defaultFactory) {
        Checker.checkNull(defaultFactory, "defaultFactory");
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            if (found) {
                throw new IllegalArgumentException();
            }
            found = true;
            result = item;
        }
        if (!found) {
            return defaultFactory.create();
        }
        return result;
    }

    public T singleOrNull() {
        boolean found = false;
        T result = null;
        for (T item : iterable) {
            if (found) {
                throw new IllegalArgumentException();
            }
            found = true;
            result = item;
        }
        return result;
    }

    public boolean any() {
        return iterable.iterator().hasNext();
    }

    public boolean any(final Predicate<T> predicate) {
        Checker.checkNull(predicate, "predicate");
        for (T item : iterable) {
            if (predicate.invoke(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean all(final Predicate<T> predicate) {
        Checker.checkNull(predicate, "predicate");
        for (T item : iterable) {
            if (!predicate.invoke(item)) {
                return false;
            }
        }
        return true;
    }

    public int count() {
        if (iterable instanceof Collection) {
            return ((Collection) iterable).size();
        }
        int result = 0;
        for (T item : iterable) {
            result++;
        }
        return result;
    }

    public long countLong() {
        long result = 0;
        for (T item : iterable) {
            result++;
        }
        return result;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<T>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }

    public Set<T> toSet() {
        Set<T> result = new LinkedHashSet<T>();
        for (T item : iterable) {
            result.add(item);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) toList().toArray();
    }

    public <K, V> Map<K, V> toMap(final Func<T, K> keySelector, final Func<T, V> valueSelector) {
        Checker.checkNull(keySelector, "keySelector");
        Checker.checkNull(valueSelector, "valueSelector");
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (T item : iterable) {
            result.put(keySelector.invoke(item), valueSelector.invoke(item));
        }
        return result;
    }

    public List<T> toImmutableList() {
        return Collections.unmodifiableList(toList());
    }

    public Set<T> toImmutableSet() {
        return Collections.unmodifiableSet(toSet());
    }

    public <K, V> Map<K, V> toImmutableMap(final Func<T, K> keySelector, final Func<T, V> valueSelector) {
        return Collections.unmodifiableMap(toMap(keySelector, valueSelector));
    }
}
