package dev.mahabal.runetech

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.jsoup.Jsoup
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.IntInsnNode
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.BasicFileAttributeView
import java.util.*
import java.util.jar.JarFile

typealias ClassMap = HashMap<String, ClassNode>

/**
 * Downloads and parses the OldSchool java configuration file into a properties object for
 * easy access.
 *
 * The gamepack can be downloaded by just accessing: https://oldschool1.runescape.com/gamepack.jar;
 * however, loading the java configuration and parsing the URL and gamepack name is a more "correct"
 * way of doing it, because it's how the official client does it.
 */
data class JavConfig(val javConfigUrl: String = "https://oldschool.runescape.com/jav_config.ws") {
    val properties: Properties by lazy {
        val config = Properties()
        config.load(Jsoup.connect(javConfigUrl).get().wholeText()
                .replace("msg=", "msg_")
                .replace("param=", "param_")
                .byteInputStream())
        config
    }
}

/**
 * Represents a Gamepack (a JAR file containing all of the client classes)
 */
class Gamepack(private val javConfig: Properties) {
    // download the gamepack and convert it to a ByteArray
    private val bytes = Jsoup.connect(javConfig.getProperty("codebase") + javConfig.getProperty("initial_jar"))
            .maxBodySize(0).ignoreContentType(true)
            .execute().bodyAsBytes()
    // convert the gamepack ByteArray to a ClassMap
    private val classMap = bytes.asClassMap()
    // gets the current revision of the gamepack by analyzing the bytecode
    val revision: Int by lazy {
        var revision = -1
        // get the initial_class of the jar (should be "client.class")
        val client = classMap[javConfig.getProperty("initial_class").replace(".class", "")]
        if (client != null) {
            // in order to determine the revision of the client we are looking for an instanced method
            // call similar to: method(765, 503, $revision, ...);, which means that we will look for
            // two sipush instructions with the operands 765 and 503 respectively. the next IntInsnNode
            // would be the revision.
            for (method in client.methods.filter { mn -> mn.access.and(Opcodes.ACC_STATIC) == 0 }) {
                // iterate over every instruction in the method
                for (ain in method.instructions.toArray()) {
                    // continue if the instruction isn't
                    if (ain.opcode != Opcodes.SIPUSH) continue
                    // convert the instruction to an IntInsnNode
                    var sipush = ain as IntInsnNode
                    // continue if the operand isn't 765 or next insn isn't a SIPUSH
                    if (sipush.operand != 765 || sipush.next.opcode != Opcodes.SIPUSH) continue
                    sipush = sipush.next as IntInsnNode
                    // continue if the operand isn't 503 or next insn isn't a SIPUSH
                    if (sipush.operand != 503 || sipush.next.opcode != Opcodes.SIPUSH) continue
                    val rev = sipush.next as IntInsnNode
                    // this instruction should be the revision, so return the operand
                    revision = rev.operand
                }
            }
        }
        revision
    }

    /**
     * Writes the ByteArray of the Gamepack to the provided path with the given fileName.
     *
     * It will also look at the creation time of the classes within the JAR and update the
     * creation, modification, and access time to the time that the gamepack was compiled
     * on Jagex's build server
     */
    fun dump(directory: Path, fileNameFormat: String) {
        val fileName = fileNameFormat.replace("\${revision}", revision.toString())
        if (!Files.exists(directory))
            Files.createDirectories(directory)
        val outputPath = directory.resolve(fileName)
        Files.write(outputPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        val jar = JarFile(outputPath.toFile())
        val time = jar.getJarEntry(javConfig.getProperty("initial_class")).lastModifiedTime
        val attributes = Files.getFileAttributeView(outputPath, BasicFileAttributeView::class.java)
        attributes.setTimes(time, time, time)
    }

}

/**
 * Adds an extension to convert a byte array of a gamepack to a ClassMap
 */
fun ByteArray.asClassMap(): ClassMap {
    val temp = Paths.get("gamepack.jar")
    val jar = JarFile(Files.write(temp, this, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).toFile())
    val map = ClassMap()
    jar.stream()
            .filter { entry -> entry.name.endsWith(".class") }
            .map { entry -> jar.getInputStream(entry).asClassNode() }
            .forEach { node -> map[node.name] = node }
    jar.close()
    Files.deleteIfExists(temp)
    return map
}

/**
 * Adds an extension to convert an InputStream to an ASM class node
 */
fun InputStream.asClassNode(): ClassNode {
    val clazz = ClassReader(this.readBytes())
    val node = ClassNode()
    clazz.accept(node, ClassReader.EXPAND_FRAMES)
    return node
}

class Main : CliktCommand() {
    private val fileNameFormat:
            String by option("-f", "--file-name", help = "format for naming gamepack. default = osrs-\${revision}.jar")
            .default("osrs-\${revision}.jar")
    private val output by option("-d", "--directory", help = "the full path to the DIRECTORY to save the gamepack").default("./")
    private val rev by option("-r", "--revision", help = "just print out the current gamepack revision ").flag()
    private val printProperties by option("-p", "--properties", help = "prints the contents of the jav_config.ws file").flag()
    override fun run() {
        val properties = JavConfig().properties
        if (printProperties) {
            properties.entries.sortedBy { entry -> entry.key.toString() }
                    .forEach { entry -> println("${entry.key}=${entry.value}") }
            return
        }
        val outputPath = Paths.get(output)
        val gamepack = Gamepack(properties)
        if (rev) {
            println(gamepack.revision)
        } else {
            gamepack.dump(outputPath, fileNameFormat)
        }
    }
}

fun main(args: Array<String>) = Main().main(args)