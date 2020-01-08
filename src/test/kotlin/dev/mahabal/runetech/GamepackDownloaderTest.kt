package dev.mahabal.runetech

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths

internal class GamepackDownloaderTest {

    private val revision = RemoteGamepack(JavConfig().properties).revision
    private val mainClass = Main()
    private val output = ByteArrayOutputStream()

    init {
        System.setOut(PrintStream(output))
    }

    @Test
    fun `Test revision checker`() {
        mainClass.main(arrayOf("-r", "--dry-run"))
        // remove the new lines from the output and compare it to the stored revision
        assertEquals(revision, output.toString().replace(Regex("[\\r\\n]+"), "").toInt())
    }


    @Test
    fun `Test Default Dumper`() {
        mainClass.main(arrayOf())
        val dumped = Paths.get("./").resolve("osrs-$revision.jar")
        assertTrue(Files.isRegularFile(dumped))
        Files.deleteIfExists(dumped)
    }

    @AfterEach
    fun resetOutput() = output.reset()

    @Test
    fun `Test properties dumper`() {
        mainClass.main(arrayOf("-p", "--dry-run"))
        assertTrue(output.toString().contains("codebase="))
        assertTrue(output.toString().contains("initial_jar="))
        assertTrue(output.toString().contains("initial_class="))
    }


}