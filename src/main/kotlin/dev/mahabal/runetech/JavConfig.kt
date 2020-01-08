package dev.mahabal.runetech

import org.jsoup.Jsoup
import java.util.*

/**
 * Downloads and parses the OldSchool java configuration file into a properties object for
 * easy access.
 *
 * The [Gamepack] can be downloaded by just accessing: https://oldschool1.runescape.com/gamepack.jar;
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