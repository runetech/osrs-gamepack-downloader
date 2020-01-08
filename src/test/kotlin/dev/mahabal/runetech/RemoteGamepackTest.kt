package dev.mahabal.runetech

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

internal class RemoteGamepackTest {

    private val gamepackName = "gamepack.jar"
    private val gamepack = RemoteGamepack(JavConfig().properties)

    /** Technically this check also validates the initial_class property in jav_config */
    @Test
    fun `Revision Check`() = assertTrue(gamepack.revision in 0..512)

    @Test
    fun `Test Dump`() {
        val path = Paths.get("./")
        gamepack.dump(path, gamepackName)
        assertTrue(Files.isRegularFile(path.resolve(gamepackName)))
        Files.deleteIfExists(path.resolve(gamepackName))
    }

}