package com.github.jishida.gradle.commons.util

import com.github.jishida.gradle.commons.function.Factory
import com.github.jishida.gradle.commons.function.Func
import com.github.jishida.gradle.commons.function.Predicate
import spock.lang.Specification

import static com.github.jishida.gradle.commons.util.CollectionUtils.enumerable

class EnumerableTest extends Specification {
    def "Enumerable test"() {
        when:
        final iter1 = sut.iterator()

        then:
        iter1.next() == 1
        iter1.next() == 3
        iter1.next() == 5

        when:
        iter1.next()

        then:
        thrown(NoSuchElementException)

        when:
        final iter2 = sut.iterator()
        if (!iter2.hasNext()) {
            print(iter2.toString())
        }

        then:
        iter2.hasNext()
        iter2.hasNext()
        iter2.next() == 1
        iter2.hasNext()
        iter2.hasNext()
        iter2.next() == 3
        iter2.hasNext()
        iter2.hasNext()
        iter2.next() == 5
        !iter2.hasNext()
        !iter2.hasNext()

        when:
        iter2.next()

        then:
        thrown(NoSuchElementException)

        where:
        sut                                 | _
        new Enumerable([1, 3, 5])           | null
        new Enumerable([0, 1, 2, 3, 4, 5]).where(new Predicate<Integer>() {
            @Override
            boolean invoke(Integer it) {
                return it % 2 != 0
            }
        })                                  | null
        new Enumerable([0, 2, 4]).select(new Func<Integer, Integer>() {
            @Override
            Integer invoke(Integer it) {
                return it + 1
            }
        })                                  | null
        new Enumerable([5, 3, 1]).reverse() | null
    }

    def 'first test'() {
        expect:
        new Enumerable<Integer>([1, 2, 3]).first() == 1
        new Enumerable<Integer>([1, 2, 3]).first(5) == 1
        new Enumerable<Integer>([1, 2, 3]).first(new Factory<Integer>() {
            @Override
            Integer create() {
                return 10
            }
        }) == 1
        new Enumerable<Integer>([1, 2, 3]).firstOrNull() == 1
        new Enumerable<Integer>([]).first(5) == 5
        new Enumerable<Integer>([]).first(new Factory<Integer>() {
            @Override
            Integer create() {
                return 10
            }
        }) == 10
        new Enumerable<Integer>([]).firstOrNull() == null


        when:
        new Enumerable<Integer>([]).first()

        then:
        thrown(IllegalArgumentException)
    }

    def 'last test'() {
        expect:
        new Enumerable<Integer>([1, 2, 3]).last() == 3
        new Enumerable<Integer>([1, 2, 3]).last(5) == 3
        new Enumerable<Integer>([1, 2, 3]).last(new Factory<Integer>() {
            @Override
            Integer create() {
                return 10
            }
        }) == 3
        new Enumerable<Integer>([1, 2, 3]).lastOrNull() == 3

        new Enumerable<Integer>([]).last(5) == 5
        new Enumerable<Integer>([]).last(new Factory<Integer>() {
            @Override
            Integer create() {
                return 10
            }
        }) == 10
        new Enumerable<Integer>([]).lastOrNull() == null

        when:
        new Enumerable<Integer>([]).last()

        then:
        thrown(IllegalArgumentException)
    }

    def 'single test'() {
        final defaultFactory = new Factory<Integer>() {
            @Override
            Integer create() {
                return 10
            }
        }

        expect:
        new Enumerable<Integer>([3]).single() == 3
        new Enumerable<Integer>([3]).single(5) == 3
        new Enumerable<Integer>([3]).single(defaultFactory) == 3
        new Enumerable<Integer>([3]).singleOrNull() == 3

        new Enumerable<Integer>([]).single(5) == 5
        new Enumerable<Integer>([]).single(defaultFactory) == 10
        new Enumerable<Integer>([]).singleOrNull() == null

        when:
        new Enumerable<Integer>([]).single()

        then:
        thrown(IllegalArgumentException)

        when:
        new Enumerable<Integer>([2, 4]).single()

        then:
        thrown(IllegalArgumentException)

        when:
        new Enumerable<Integer>([2, 4]).single()

        then:
        thrown(IllegalArgumentException)

        when:
        new Enumerable<Integer>([2, 4]).single(5)

        then:
        thrown(IllegalArgumentException)

        when:
        new Enumerable<Integer>([2, 4]).single(defaultFactory)

        then:
        thrown(IllegalArgumentException)

        when:
        new Enumerable<Integer>([2, 4]).singleOrNull()

        then:
        thrown(IllegalArgumentException)
    }

    def 'any test'() {
        final predicate = new Predicate<Integer>() {
            @Override
            boolean invoke(Integer it) {
                return it == 3
            }
        }

        expect:
        new Enumerable([1, 2, 3]).any()
        new Enumerable([1, 2, 3]).where(predicate).any()
        new Enumerable([1, 2, 3]).any(predicate)

        !new Enumerable([]).any()
        !new Enumerable([4, 5, 6]).where(predicate).any()
        !new Enumerable([4, 5, 6]).any(predicate)
    }

    def 'all test'() {
        final predicate = new Predicate<Integer>() {
            @Override
            boolean invoke(Integer it) {
                return it == 3
            }
        }

        expect:
        !new Enumerable([1, 2, 3]).all(predicate)
        new Enumerable([3, 3, 3]).all(predicate)
        new Enumerable([]).all(predicate)
    }

    def 'count test'() {
        final predicate = new Predicate<Integer>() {
            @Override
            boolean invoke(Integer it) {
                return it == 3
            }
        }

        expect:
        new Enumerable([1, 2, 3]).count() == 3
        new Enumerable([1, 2, 3]).where(predicate).count() == 1
        new Enumerable([]).count() == 0
        new Enumerable([1, 2, 3]).countLong() == 3L
        new Enumerable([1, 2, 3]).where(predicate).countLong() == 1L
        new Enumerable([]).countLong() == 0L
    }

    def 'convert test'() {
        final list = [1, 2, 3]
        final array = [1, 2, 3] as Integer[]
        final keySelector = new Func<Integer, String>() {
            @Override
            String invoke(Integer it) {
                return it.toString()
            }
        }
        final valueSelector = new Func<Integer, Integer>() {
            @Override
            Integer invoke(Integer it) {
                return it
            }
        }
        expect:
        new Enumerable(list).toList() instanceof List
        new Enumerable(list).toList() == list
        !new Enumerable(list).toList().is(list)
        new Enumerable(list).toList().size() == 3
        new Enumerable(list).toList().add(4)

        new Enumerable(list).toImmutableList() instanceof List
        new Enumerable(list).toImmutableList() == list
        !new Enumerable(list).toImmutableList().is(list)
        new Enumerable(list).toImmutableList().size() == 3
        when:
        new Enumerable(list).toImmutableList().add(4)
        then:
        thrown(UnsupportedOperationException)

        new Enumerable(list).toSet() instanceof Set
        new Enumerable(list).toSet() == new LinkedHashSet(list)
        !new Enumerable(list).toSet().is(list)
        new Enumerable(list).toSet().size() == 3
        new Enumerable(list).toSet().add(4)

        new Enumerable(list).toImmutableSet() instanceof Set
        new Enumerable(list).toImmutableSet() == new LinkedHashSet(list)
        !new Enumerable(list).toImmutableSet().is(list)
        new Enumerable(list).toImmutableSet().size() == 3
        when:
        new Enumerable(list).toImmutableSet().add(4)
        then:
        thrown(UnsupportedOperationException)

        new Enumerable(list).toMap(keySelector, valueSelector) instanceof Map
        new Enumerable(list).toMap(keySelector, valueSelector) == ['1': 1, '2': 2, '3': 3]
        !new Enumerable(list).toMap(keySelector, valueSelector).is(list)
        new Enumerable(list).toMap(keySelector, valueSelector).size() == 3
        new Enumerable(list).toMap(keySelector, valueSelector).put('3', 4) != null


        new Enumerable(list).toImmutableMap(keySelector, valueSelector) instanceof Map
        new Enumerable(list).toImmutableMap(keySelector, valueSelector) == ['1': 1, '2': 2, '3': 3]
        !new Enumerable(list).toImmutableMap(keySelector, valueSelector).is(list)
        new Enumerable(list).toImmutableMap(keySelector, valueSelector).size() == 3
        when:
        new Enumerable(list).toImmutableMap(keySelector, valueSelector).put('3', 4)
        then:
        thrown(UnsupportedOperationException)

        enumerable(array).toArray() instanceof Object[]
        enumerable(array).toArray()[0] == array[0]
        enumerable(array).toArray()[1] == array[1]
        enumerable(array).toArray()[2] == array[2]
        !enumerable(array).toArray().is(array)
        enumerable(array).toArray().length == 3
    }
}
