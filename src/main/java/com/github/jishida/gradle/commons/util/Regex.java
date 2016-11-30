package com.github.jishida.gradle.commons.util;

import java.util.regex.Pattern;

final class Regex {
    final static Pattern javaVersion = Pattern.compile("^([0-9]+)\\.([0-9]+)\\.([0-9]+)_([0-9]+).*$");
    final static Pattern hexString = Pattern.compile("^([0-9a-f]{2})*$");
    final static Pattern urlFilePath = Pattern.compile("^.*/([a-zA-Z0-9._\\-%]+)$");
    final static Pattern sourceForgeFilePath = Pattern.compile("^.*/([a-zA-Z0-9._\\-%]+)/download$");
}
