package com.github.jishida.gradle.commons.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K, V> {
    private final Map<K, V> map;

    public MapBuilder() {
        this(LinkedHashMap.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Map> MapBuilder(Class<T> mapClass) {
        try {
            map = mapClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MapBuilder<K, V> put(final K key, final V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return CollectionUtils.copy(map);
    }
}
