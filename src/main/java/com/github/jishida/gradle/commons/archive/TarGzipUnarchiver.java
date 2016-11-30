package com.github.jishida.gradle.commons.archive;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;

import java.io.File;

public class TarGzipUnarchiver implements Unarchiver {
    @Override
    public FileTree getFileTree(Project project, File archiveFile) {
        return project.tarTree(project.getResources().gzip(archiveFile));
    }
}
