package com.github.jishida.gradle.commons.util;

import com.github.jishida.gradle.commons.function.Func;
import com.github.jishida.gradle.commons.function.Predicate;
import org.gradle.internal.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;

import static com.github.jishida.gradle.commons.util.CollectionUtils.enumerable;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.join;

public class EnvironmentUtils {
    private final static boolean windows = OperatingSystem.current() == OperatingSystem.WINDOWS;

    public static boolean isWindows() {
        return windows;
    }

    public static int[] getJavaVersion() {
        final String versionString = System.getProperty("java.version");
        if (versionString == null)
            return null;
        final Matcher matcher = Regex.javaVersion.matcher(versionString);
        if (!matcher.matches())
            return null;
        final int[] result = new int[4];
        result[0] = Integer.parseInt(matcher.group(1));
        result[1] = Integer.parseInt(matcher.group(2));
        result[2] = Integer.parseInt(matcher.group(3));
        result[3] = Integer.parseInt(matcher.group(4));
        return result;
    }

    static void setPaths(final Map<String, Object> env, final File... files) {
        setPaths(env, true, files);
    }

    public static void setPaths(final Map<String, Object> env, final boolean prior, final File... files) {
        Checker.checkNull(env, "env");
        Checker.checkNull(files, "files");

        setPathInternal(env, formatFiles(enumerable(files)), prior);
    }

    static void setPaths(final Map<String, Object> env, final Iterable<File> files) {
        setPaths(env, files, true);
    }

    static void setPaths(final Map<String, Object> env, final Iterable<File> files, final boolean prior) {
        Checker.checkNull(env, "env");
        Checker.checkNull(files, "files");

        setPathInternal(env, formatFiles(enumerable(files)), prior);
    }

    static void setPath(final Map<String, Object> env, final File file) {
        setPath(env, file, true);
    }

    static void setPath(final Map<String, Object> env, final File file, final boolean prior) {
        Checker.checkNull(env, "env");
        Checker.checkNull(file, "file");

        try {
            setPathInternal(env, file.getCanonicalPath(), prior);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatFiles(final Enumerable<File> files) {
        final Iterable<String> paths = files.select(new Func<File, String>() {
            @Override
            public String invoke(File it) {
                try {
                    return it.getCanonicalPath();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return join(paths, File.pathSeparator);
    }

    private static void setPathInternal(final Map<String, Object> env, final String paths, final boolean prior) {
        final String pathKey = env.containsKey("PATH") ? "PATH" : enumerable(env.keySet()).where(new Predicate<String>() {
            @Override
            public boolean invoke(String it) {
                return it.toUpperCase().equals("PATH");
            }
        }).first("PATH");

        final Object pathValue = env.get(pathKey);
        env.remove(pathKey);

        final StringBuilder pathBuilder = new StringBuilder();
        if (prior) {
            pathBuilder.append(paths);
            if (!pathValue.toString().isEmpty()) {
                pathBuilder.append(File.pathSeparatorChar);
                pathBuilder.append(pathValue);
            }
        } else {
            if (!pathValue.toString().isEmpty()) {
                pathBuilder.append(pathValue);
                pathBuilder.append(File.pathSeparatorChar);
            }
            pathBuilder.append(paths);
        }
        env.put("PATH", pathBuilder.toString());
    }

    public static void setEnvValue(final Map<String, Object> env, final String key, final String value) {
        if (windows) {
            removeEnvKey(env, key);
        }
        env.put(key, value);
    }

    public static void removeEnvKey(final Map<String, Object> env, final String key) {
        if (windows) {
            for (String k : enumerable(env.keySet()).where(new Predicate<String>() {
                @Override
                public boolean invoke(String it) {
                    return it.toUpperCase().equals(key.toUpperCase());
                }
            })) {
                env.remove(k);
            }
        } else {
            env.remove(key);
        }
    }
}
