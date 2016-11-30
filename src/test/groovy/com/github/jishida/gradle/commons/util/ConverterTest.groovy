package com.github.jishida.gradle.commons.util

import spock.lang.Specification

class ConverterTest extends Specification {
    def 'bytesToHex test'() {
        expect:
        Converter.bytesToHex(bytes as byte[]) == expected
        Converter.bytesToHex(bytes as byte[], " - ") == separated

        where:
        bytes                                            | expected           | separated
        []                                               | ''                 | ''
        [0x5f]                                           | '5f'               | '5f'
        [0xa7, 0x0b]                                     | 'a70b'             | 'a7 - 0b'
        [0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef] | '0123456789abcdef' | '01 - 23 - 45 - 67 - 89 - ab - cd - ef'
    }

    def 'hexToBytes test'() {
        expect:
        Converter.hexToBytes(hex) == expected as byte[]
        Converter.hexToBytes(separatedHex, " - ") == expected as byte[]

        where:
        hex                | separatedHex                            | expected
        ''                 | ''                                      | []
        '5f'               | '5f'                                    | [0x5f]
        'a70b'             | 'a7 - 0b'                               | [0xa7, 0x0b]
        '0123456789abcdef' | '01 - 23 - 45 - 67 - 89 - ab - cd - ef' | [0x01, 0x23, 0x45, 0x67, 0x89, 0xab, 0xcd, 0xef]
    }
}
