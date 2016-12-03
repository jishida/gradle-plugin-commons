package com.github.jishida.gradle.commons.util

import spock.lang.Specification

class CollectionUtilsTest extends Specification {
    def 'copy list'() {
        final list = [1, 2, 3]

        expect:
        CollectionUtils.copy(list) == list
        !CollectionUtils.copy(list).is(list)
        CollectionUtils.copy(list) instanceof List

        CollectionUtils.copy(list, ArrayList) == list
        !CollectionUtils.copy(list, ArrayList).is(list)
        CollectionUtils.copy(list, ArrayList) instanceof ArrayList

        CollectionUtils.copy(list, LinkedList) == list
        !CollectionUtils.copy(list, LinkedList).is(list)
        CollectionUtils.copy(list, LinkedList) instanceof LinkedList
    }

    def 'copy map'() {
        final map = [a: 1, b: 2, c: 3]

        expect:
        CollectionUtils.copy(map) == map
        !CollectionUtils.copy(map).is(map)
        CollectionUtils.copy(map) instanceof Map

        CollectionUtils.copy(map, HashMap) == map
        !CollectionUtils.copy(map, HashMap).is(map)
        CollectionUtils.copy(map, HashMap) instanceof HashMap

        CollectionUtils.copy(map, LinkedHashMap) == map
        !CollectionUtils.copy(map, LinkedHashMap).is(map)
        CollectionUtils.copy(map, LinkedHashMap) instanceof LinkedHashMap
    }
}
