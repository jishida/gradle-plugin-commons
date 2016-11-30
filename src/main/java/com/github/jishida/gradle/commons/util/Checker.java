package com.github.jishida.gradle.commons.util;

import static java.lang.String.format;

public final class Checker {
    public static void checkNull(final Object value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException(format("argument `%s` cannot be null.", name));
        }
    }

    public static void checkHex(final String hex) {
        checkNull(hex, "hex");
        if (!Regex.hexString.matcher(hex).matches()) {
            throw new IllegalArgumentException(format("`%s` is invalid hex string.", hex));
        }
    }
}
