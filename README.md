# OSRS Gamepack Downloader 
[![Actions Status](https://github.com/runetech/osrs-gamepack-downloader/workflows/Release%20Binary/badge.svg)](https://github.com/runetech/osrs-gamepack-downloader/actions) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/8d6c1deeb57847b4a79f5652eb0ce06c)](https://www.codacy.com/gh/runetech/osrs-gamepack-downloader?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=runetech/osrs-gamepack-downloader&amp;utm_campaign=Badge_Grade)

Simple OldSchool RuneScape gamepack downloader written in [Kotlin](https://kotlinlang.org). A collection of
downloaded gamepacks can be found [here](https://github.com/runetech/osrs-gamepacks).

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

## Usage

Double clicking the `osrs-gamepack-downloader.jar` file will download the latest
gamepack to the same directory and name it accordingly.

### Command line

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
