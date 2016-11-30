package com.github.jishida.gradle.commons.util;

public class Converter {
    public static String bytesToHex(final byte[] bytes) {
        return bytesToHex(bytes, null);
    }

    public static String bytesToHex(final byte[] bytes, final String separator) {
        Checker.checkNull(bytes, "bytes");
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            builder.append(String.format("%02x", bytes[i]));
            if (separator != null && i != bytes.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    public static byte[] hexToBytes(String hex) {
        return hexToBytes(hex, null);
    }

    public static byte[] hexToBytes(String hex, final String separator) {
        hex = hex.toLowerCase();
        if (separator != null) {
            hex = hex.replace(separator, "");
        }
        Checker.checkHex(hex);
        final int len = hex.length() / 2;
        final byte[] result = new byte[len];
        final char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            final int hi = hexCharToNibble(chars[i * 2]);
            final int lo = hexCharToNibble(chars[i * 2 + 1]);
            final byte b = (byte) ((hi << 4) | lo);
            result[i] = b;
        }
        return result;
    }

    private static int hexCharToNibble(final char c) {
        final int code = (int) c;
        if (code >= 0x30 && code <= 0x39) {
            return code - 0x30;
        } else if (code >= 0x61 && code <= 0x7a) {
            return code - 0x57;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
