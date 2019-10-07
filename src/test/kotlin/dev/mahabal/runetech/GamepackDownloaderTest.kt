package dev.mahabal.runetech

import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GamepackDownloaderTest {

    private val javConfig = JavConfig().properties

    @Test
    fun `Validate Configuration`() {
        assertTrue(javConfig.isNotEmpty(), "Configuration was empty")
    }

    @Test
    fun `Check Codebase`() {
        val codebase = javConfig.getProperty("codebase")
        assertFalse(codebase.isBlank(), "Codebase was null or blank!")
    }

    @Test
    fun `Check Initial Class`() {
        val initialClass = javConfig.getProperty("initial_class")
        assertFalse(initialClass.isBlank(), "Initial class was null or blank!")
    }

    @Test
    fun `Check Initial Jar`() {
        val initialJar = javConfig.getProperty("initial_jar")
        assertFalse(initialJar.isBlank(), "Initial class was null or blank!")
    }

    @Test
    fun `Check Gamepack URL`() {
        val gamepackURL = javConfig.getProperty("codebase") + javConfig.getProperty("initial_jar")
        assertEquals(200,
                Jsoup.connect(gamepackURL)
                        .ignoreContentType(true)
                        .execute()
                        .statusCode(),
                "Unable to establish a successful connection to $gamepackURL")
    }

    @Test
    fun `Validate Revision`() {
        val gamepack = Gamepack(javConfig)
        assertTrue(gamepack.revision in 0..255,
                "Could not determine a valid gamepack value. Retrieved: ${gamepack.revision}")
    }

    @Test
    fun `Print jav_config`() {
        javConfig.forEach { t, u -> println("$t : $u") }
    }

}