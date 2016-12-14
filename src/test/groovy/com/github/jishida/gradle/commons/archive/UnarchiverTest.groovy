package com.github.jishida.gradle.commons.archive

import nebula.test.ProjectSpec
import spock.lang.Shared
import static com.github.jishida.gradle.commons.TestStrings.TEST_TEMP_PATH;

class UnarchiverTest extends ProjectSpec {
    private final static TEST_ARCHIVE_PATH = "${TEST_TEMP_PATH}/UnarchiverTest"
    private final static archiveTypes = Arrays.asList('tar', 'tar.bz2', 'tar.gz', 'tar.xz', 'zip')

    @Shared
    private File testDir

    def setupSpec() {
        testDir = new File(TEST_ARCHIVE_PATH).canonicalFile
        testDir.mkdirs()
        archiveTypes.each {
            final name = "archive.${it}"
            final file = new File(testDir, name)
            file.delete()
            UnarchiverTest.getResourceAsStream(name).withStream {
                file << it
            }
        }
    }

    def 'archive test'() {
        when:
        final name = "archive.${type}"
        final archiveFile = new File(testDir, name)
        final workingDir = project.file(type)
        workingDir.mkdirs()
        project.copy {
            it.from(unarchiver.getFileTree(project, archiveFile))
            it.into(workingDir)
        }

        then:
        new File(workingDir, 'dir').directory
        new File(workingDir, 'dir/file1').file
        new File(workingDir, 'dir/dir1').directory
        new File(workingDir, 'dir/dir1/file2').file
        new File(workingDir, 'dir/dir1/dir2').directory
        new File(workingDir, 'dir/dir1/dir2/file3').file
        new File(workingDir, 'dir/dir1/dir2/dir3').directory

        new File(workingDir, 'dir/file1').text == 'file1'
        new File(workingDir, 'dir/dir1/file2').text == 'file2'
        new File(workingDir, 'dir/dir1/dir2/file3').text == 'file3'

        where:
        type      | unarchiver
        'tar'     | TarUnarchiver.newInstance()
        'tar.bz2' | TarBzip2Unarchiver.newInstance()
        'tar.gz'  | TarGzipUnarchiver.newInstance()
        'tar.xz'  | TarXZUnarchiver.newInstance()
        'zip'     | ZipUnarchiver.newInstance()
    }

    def cleanupSpec() {
        testDir.deleteDir()
    }
}
