package com.github.jishida.gradle.commons.util;

import com.github.jishida.gradle.commons.function.Func;
import com.github.jishida.gradle.commons.function.Proc;

import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.lang.String.format;
import static org.codehaus.groovy.runtime.ResourceGroovyMethods.deleteDir;

public class IOUtils {
    private final static Charset defaultCharset = Charset.forName("UTF-8");

    public static void deleteFile(final File file) {
        if (file.isFile()) {
            if (!file.delete()) {
                throw new UnsupportedOperationException(format("could not delete `%s`", file));
            }
        } else if (file.isDirectory()) {
            if (!deleteDir(file)) {
                throw new UnsupportedOperationException(format("could not delete `%s`", file));
            }
        }
    }

    public static <T extends Closeable> void withCloseable(final T closeable, final Proc<T> proc) throws IOException {
        try {
            proc.invoke(closeable);
        } finally {
            closeable.close();
        }
    }

    public static <T extends Closeable, U> U withCloseable(final T closeable, final Func<T, U> func) throws IOException {
        try {
            return func.invoke(closeable);
        } finally {
            closeable.close();
        }
    }

    public static void withInputStream(final File file, final Proc<InputStream> proc) throws IOException {
        withCloseable(new FileInputStream(file), proc);
    }

    public static <T> T withInputStream(final File file, final Func<InputStream, T> func) throws IOException {
        return withCloseable(new FileInputStream(file), func);
    }

    public static void withOutputStream(final File file, final Proc<OutputStream> proc) throws IOException {
        withCloseable(new FileOutputStream(file), proc);
    }

    public static void withOutputStream(final File file, final boolean append, final Proc<OutputStream> proc) throws IOException {
        withCloseable(new FileOutputStream(file, append), proc);
    }

    public static <T> T withOutputStream(final File file, final Func<OutputStream, T> func) throws IOException {
        return withCloseable(new FileOutputStream(file), func);
    }

    public static <T> T withOutputStream(final File file, final boolean append, final Func<OutputStream, T> func) throws IOException {
        return withCloseable(new FileOutputStream(file, append), func);
    }

    public static void withWriter(final File file, final Proc<Writer> proc) throws IOException {
        withCloseable(new OutputStreamWriter(new FileOutputStream(file), defaultCharset), proc);
    }

    public static void withWriter(final File file, final Charset charset, final Proc<Writer> proc) throws IOException {
        withCloseable(new OutputStreamWriter(new FileOutputStream(file), charset), proc);
    }

    public static void withWriter(final File file, final boolean append, final Proc<Writer> proc) throws IOException {
        withCloseable(new OutputStreamWriter(new FileOutputStream(file, append), defaultCharset), proc);
    }

    public static void withWriter(final File file, final boolean append, final Charset charset, final Proc<Writer> proc) throws IOException {
        withCloseable(new OutputStreamWriter(new FileOutputStream(file, append), charset), proc);
    }

    public static <T> T withWriter(final File file, final Func<Writer, T> func) throws IOException {
        return withCloseable(new OutputStreamWriter(new FileOutputStream(file), defaultCharset), func);
    }

    public static <T> T withWriter(final File file, final Charset charset, final Func<Writer, T> func) throws IOException {
        return withCloseable(new OutputStreamWriter(new FileOutputStream(file), charset), func);
    }

    public static <T> T withWriter(final File file, final boolean append, final Func<Writer, T> func) throws IOException {
        return withCloseable(new OutputStreamWriter(new FileOutputStream(file, append), defaultCharset), func);
    }

    public static <T> T withWriter(final File file, final boolean append, final Charset charset, final Func<Writer, T> func) throws IOException {
        return withCloseable(new OutputStreamWriter(new FileOutputStream(file, append), charset), func);
    }

    public static boolean verifyFile(final File file, final byte[] expectedHash, final String algorithm) {
        try {
            return withInputStream(file, new Func<InputStream, Boolean>() {
                @Override
                public Boolean invoke(final InputStream it) {
                    return verifyInputStream(it, expectedHash, algorithm);
                }
            });
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean verifyInputStream(final InputStream stream, final byte[] expectedHash, final String algorithm) {
        Checker.checkNull(expectedHash, "expectedHash");
        final byte[] buffer = new byte[1024];
        int size;
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            while ((size = stream.read(buffer)) >= 0) {
                if (size != 0) {
                    md.update(buffer, 0, size);
                }
            }
            final byte[] actualHash = md.digest();
            return Arrays.equals(actualHash, expectedHash);
        } catch (IOException e) {
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static File getAbsoluteFile(final String path) {
        return getAbsoluteFile(path, null);
    }

    public static File getAbsoluteFile(final String path, File baseDir) {
        Checker.checkNull(path, "path");
        final File file = new File(path);
        try {
            if (file.isAbsolute()) {
                return file.getCanonicalFile();
            }
            if (baseDir == null) {
                baseDir = new File(".");
            }
            return new File(baseDir, path).getCanonicalFile();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
