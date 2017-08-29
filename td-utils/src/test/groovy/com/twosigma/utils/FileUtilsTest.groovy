package com.twosigma.utils

import org.junit.Test

import java.nio.file.Files

/**
 * @author mykola
 */
class FileUtilsTest {
    @Test
    void "should read text content from a file"() {
        def testFile = new File("dummy.txt")
        testFile.deleteOnExit()

        Files.write(testFile.toPath(), ["content of a file \u275e"])
        assert FileUtils.fileTextContent(testFile.toPath()) == "content of a file ❞"
    }
}
