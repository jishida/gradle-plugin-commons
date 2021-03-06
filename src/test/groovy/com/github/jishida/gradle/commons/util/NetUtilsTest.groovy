package com.github.jishida.gradle.commons.util

import spock.lang.Specification

class NetUtilsTest extends Specification {
    def 'check `findFileName` results'() {
        expect:
        NetUtils.findFileName(new URL(url), null) == name

        where:
        url                                                                                               || name
        'http://jishida.github.com/distrib/i686/msys2-base-i686-20160921.tar.xz'                          || 'msys2-base-i686-20160921.tar.xz'
        'http://jishida.github.com/dist/msys2-base-x86_64-20161025.tar.xz?query1=hoge&query2=foo'         || 'msys2-base-x86_64-20161025.tar.xz'
        'http://jishida.github.com/dist/directory/'                                                       || null
        'https://sourceforge.net/projects/msys2/files/Base/i686/msys2-base-i686-20161025.tar.xz/download' || 'msys2-base-i686-20161025.tar.xz'
    }
}
