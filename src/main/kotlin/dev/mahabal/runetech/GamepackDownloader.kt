package dev.mahabal.runetech

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.nio.file.Paths

/** Handles all CLI arguments and executes the downloader accordingly */
class Main : CliktCommand() {
    private val fileNameFormat:
            String by option("-f", "--file-name", help = "format for naming gamepack. default = osrs-\${revision}.jar")
            .default("osrs-\${revision}.jar")
    private val directory by option("-d", "--directory", help = "the full path to the DIRECTORY to save the gamepack").default("./")
    private val rev by option("-r", "--revision", help = "prints out the current gamepack revision ").flag()
    private val dryRun by option("--dry-run", help = "nothing will be saved to the filesystem").flag()
    private val printProperties by option("-p", "--properties", help = "prints the contents of the jav_config.ws file").flag()
    override fun run() {
        val properties = JavConfig().properties
        if (printProperties) {
            properties.entries.sortedBy { entry -> entry.key.toString() }
                    .forEach { entry -> println("${entry.key}=${entry.value}") }
            // if we are skipping the download and we don't care about revision
            // we can just return here to save time
            if (!rev && dryRun) return
        }
        val outputPath = Paths.get(directory)
        val gamepack = Gamepack(properties)
        if (rev)
            println(gamepack.revision)
        if (!dryRun)
            gamepack.dump(outputPath, fileNameFormat)
    }
}

/** Main entry point for the program. Passes arguments to [Main] for handling */
fun main(args: Array<String>) = Main().main(args)