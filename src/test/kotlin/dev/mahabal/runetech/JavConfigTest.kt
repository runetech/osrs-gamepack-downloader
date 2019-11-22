package dev.mahabal.runetech

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class JavConfigTest {

    private val javConfig = JavConfig()

    @Test
    fun `Test properties conversion`() = assertFalse(javConfig.properties.isEmpty)

    @Test
    fun `Check for codebase`() = assertTrue(javConfig.properties.containsKey("codebase"))

    @Test
    fun `Check for initial_jar`() = assertTrue(javConfig.properties.containsKey("initial_jar"))

}