package com.github.jishida.gradle.commons.archive;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;

import java.io.File;

public class ZipUnarchiver implements Unarchiver {
    @Override
    public FileTree getFileTree(Project project, File archiveFile) {
        return project.zipTree(archiveFile);
    }
}
