package com.github.jishida.gradle.commons.archive;

import org.gradle.api.Project;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.file.archive.compression.CompressedReadableResource;
import org.gradle.api.internal.project.AbstractProject;
import org.gradle.api.internal.resources.URIBuilder;
import org.gradle.api.resources.MissingResourceException;
import org.gradle.api.resources.ResourceException;
import org.gradle.api.resources.internal.ReadableResourceInternal;
import org.gradle.internal.resource.ResourceExceptions;
import org.tukaani.xz.XZInputStream;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

public class TarXZUnarchiver implements Unarchiver {
    private static class XZArchiver implements CompressedReadableResource {
        private final ReadableResourceInternal _resource;
        private final URI _uri;

        XZArchiver(final ReadableResourceInternal resource) {
            _resource = resource;
            _uri = new URIBuilder(resource.getURI()).schemePrefix("xz:").build();
        }

        @Override
        public InputStream read() throws MissingResourceException, ResourceException {
            try {
                return new XZInputStream(_resource.read());
            } catch (Exception e) {
                throw ResourceExceptions.readFailed(_resource.getDisplayName(), e);
            }
        }

        @Override
        public String getDisplayName() {
            return _resource.getDisplayName();
        }

        @Override
        public URI getURI() {
            return _uri;
        }

        @Override
        public String getBaseName() {
            return _resource.getBaseName();
        }

        @Override
        public File getBackingFile() {
            return _resource.getBackingFile();
        }
    }

    @Override
    public FileTree getFileTree(Project project, File archiveFile) {
        final ReadableResourceInternal resource = ((AbstractProject) project).getFileResolver().resolveResource(archiveFile);
        return project.tarTree(new XZArchiver(resource));
    }
}
