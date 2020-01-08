# OSRS Gamepack Downloader 
[![Actions Status](https://github.com/runetech/osrs-gamepack-downloader/workflows/Release%20Binary/badge.svg)](https://github.com/runetech/osrs-gamepack-downloader/actions) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/8d6c1deeb57847b4a79f5652eb0ce06c)](https://www.codacy.com/gh/runetech/osrs-gamepack-downloader?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=runetech/osrs-gamepack-downloader&amp;utm_campaign=Badge_Grade)[![License: WTFPL](https://img.shields.io/badge/License-WTFPL-brightgreen.svg)](http://www.wtfpl.net/about/)

Simple OldSchool RuneScape gamepack downloader written in [Kotlin](https://kotlinlang.org). A collection of
downloaded gamepacks can be found [here](https://github.com/runetech/osrs-gamepacks/).

What does this program do?

- Downloads and parses the official OldSchool [jav_config.ws](https://oldschool.runescape.com/jav_config.ws) to get the gamepack URL.
- Downloads the gamepack from the parsed  URL (codebase + initial_jar)
- Analyzes the gamepack's bytecode to determine the actual revision.
- Writes the gamepack to the provided (or current) directory.
- Fixes the dates (created, modified, accessed) to reflect when the gamepack was built.

## Prerequisites

- Java SE 8 or higher, available [here](https://oracle.com/technetwork/java/javase/overview/index.html).

## Building

Use the included `gradlew` (Linux/OSX) or `gradlew.bat` (Windows) files to let gradle build the file.

```bash
./gradlew build
```

The executable will be located at `build/libs/osrs-gamepack-downloader.jar`.

## Library Usage

You can add a binary as a library for your project and use the methods to download/load/verify gamepacks. 

### Java

To download the latest gamepack and load it into a class map (`HashMap<String, ClassNode>`), modify it and then save it:

```java
import dev.mahabal.runetech.Gamepack;
import dev.mahabal.runetech.JavConfig;
import dev.mahabal.runetech.RemoteGamepack;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Paths;
import java.util.HashMap;

public class Application {

    public static void main(String[] args) {
        // download and load the latest gamepack
        final Gamepack gamepack = new RemoteGamepack(new JavConfig().getProperties());
        // print out the gamepack revision
        System.out.printf("Gamepack Revision: %,d%n", gamepack.getRevision());
        // convert gamepack to a class map
        final HashMap<String, ClassNode> classMap = gamepack.getClassMap();
        // iterate the class map
        classMap.forEach((name, node) -> {
            // do something to the gamepack
        });
        // write the gamepack (with changes) to the desired file.
        gamepack.dump(Paths.get("./"), "osrs-${revision}.jar");
    }

}
```

## Standalone Usage

Double clicking the `osrs-gamepack-downloader.jar` file will download the latest
gamepack to the same directory and name it accordingly.

### CLI

```bash
java -jar osrs-gamepack-downloader.jar [OPTIONS]
```

```bash
[OPTIONS]

    -r. --revision
        prints out the current gamepack revision to the console

    -d, --directory
        the EXACT output directory to save the gamepack. [default = "./"]

    -f, --file-name
        the format for the gamepack's name. [default = "osrs-${revision}.jar"]

    -p, --properties
        prints the contents of the jav_config.ws file to stdout

    --dry-run
        skip writing the gamepack to the filesystem

```

## Examples

To just print out the current gamepack revision:

```bash
java -jar osrs-gamepack-downloader -r --dry-run
```

## Contact

**Discord:** Matthew#0001
