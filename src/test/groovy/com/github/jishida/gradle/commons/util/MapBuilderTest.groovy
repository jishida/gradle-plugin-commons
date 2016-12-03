package com.github.jishida.gradle.commons.util

import spock.lang.Specification

class MapBuilderTest extends Specification {
    def 'build map'() {
        expect:
        new MapBuilder().put('b', 2).put('a', 1).put('c', 3).build() == {
            it.put('b', 2)
            it.put('a', 1)
            it.put('c', 3)
            it
        }.call(new LinkedHashMap())
        new MapBuilder(HashMap).put('b', 2).put('a', 1).put('c', 3).build() == [b: 2, a: 1, c: 3]
    }
}
