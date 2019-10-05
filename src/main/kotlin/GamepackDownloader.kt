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
import kotlin.collections.HashMap

typealias ClassMap = HashMap<String, ClassNode>

data class JavConfig(val javConfigUrl: String = "https://oldschool.runescape.com/jav_config.ws") {
    val properties: Properties by lazy {
        val config = Properties()
        config.load(Jsoup.connect(javConfigUrl).get().wholeText().byteInputStream())
        config
    }
}

class Gamepack(private val javConfig: Properties) {
    private val bytes = Jsoup.connect(javConfig.getProperty("codebase") + javConfig.getProperty("initial_jar"))
            .maxBodySize(0).ignoreContentType(true)
            .execute().bodyAsBytes()
    private val classMap = bytes.asClassMap()
    val revision: Int by lazy {
        var revision = -1
        val client = classMap[javConfig.getProperty("initial_class").replace(".class", "")]
        if (client != null) {
            for (method in client.methods.filter { mn -> mn.access.and(Opcodes.ACC_STATIC) == 0 }) {
                for (ain in method.instructions.toArray()) {
                    if (ain.opcode != Opcodes.SIPUSH) continue
                    var sipush = ain as IntInsnNode
                    if (sipush.operand != 765 || sipush.next.opcode != Opcodes.SIPUSH) continue
                    sipush = sipush.next as IntInsnNode
                    if (sipush.operand != 503 || sipush.next.opcode != Opcodes.SIPUSH) continue
                    val rev = sipush.next as IntInsnNode
                    revision = rev.operand
                }
            }
        }
        revision
    }

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
    override fun run() {
        val outputPath = Paths.get(output)
        val gamepack = Gamepack(JavConfig().properties)
        if (rev) {
            println(gamepack.revision)
        } else {
            gamepack.dump(outputPath, fileNameFormat)
        }
    }
}

fun main(args: Array<String>) = Main().main(args)