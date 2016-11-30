package com.github.jishida.gradle.commons.function;

public interface Func<T, U> {
    U invoke(T it);
}
