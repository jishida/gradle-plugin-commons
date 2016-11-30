package com.github.jishida.gradle.commons.util

import spock.lang.Specification

class CheckerTest extends Specification {
    def 'checkNull test'() {
        Checker.checkNull(new Object(), 'obj')

        when:
        Checker.checkNull(null, 'obj')

        then:
        thrown(IllegalArgumentException)
    }
}
