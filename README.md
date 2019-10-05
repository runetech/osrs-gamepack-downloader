# OSRS Gamepack Downloader

Simple OldSchool RuneScape gamepack downloader written in [Kotlin](https://kotlinlang.org).


## Prerequisites

- Java SE 8 or higher, available [here](https://oracle.com/technetwork/java/javase/overview/index.html).

## Building

Use the included `gradlew` (Linux/OSX) or `gradlew.bat` (Windows) files to let gradle build the file.

```
./gradlew build
```

The executable will be located at `build/libs/osrs-gamepack-downloader.jar`. 


## Usage

Double clicking the `osrs-gamepack-downloader.jar` file will download the latest 
gamepack to the same directory and name it accordingly.

#### Command line:

```
java -jar osrs-gamepack-downloader.jar [OPTIONS]
```
```
[OPTIONS]

    -r. --revision  
        prints out the current gamepack revision to the console

    -d, --directory
        the EXACT output directory to save the gamepack. [default = "./"]

    -f, --file-name     
        the format for the gamepack's name. [default = "osrs-${revision}.jar"]

```

## Contact

**Discord:** Matthew#0001
