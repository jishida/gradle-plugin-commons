package com.github.jishida.gradle.commons.archive;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;

import java.io.File;

public interface Unarchiver {
    FileTree getFileTree(Project project, File archiveFile);
}
