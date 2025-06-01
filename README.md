# BDEngine Parser Library

A Java library for parsing BDEngine project structures into Java objects.

## Overview

BDEngineParser is a utility library designed to parse BDEngine project files. It converts JSON-formatted BDEngine project data into Java objects that can be easily manipulated in your application. The library also calculates world transforms for all elements in the project structure.

## Features

- Parse BDEngine project files from:
  - File paths
  - Input streams
  - JSON strings
- Automatic calculation of world transforms
- Raw parsing options (without transform calculation)
- Customizable parsing with custom Gson instances
- Comprehensive error handling
- Simple and intuitive API

## Installation

### Maven

Add the following dependency to your pom.xml:


```xml
<repository>
  <id>twme-repo</id>
  <name>TWME Repository</name>
  <url>https://repo.twme.dev/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>dev.twme</groupId>
    <artifactId>BDEngineParser</artifactId>
    <version>2.1</version>
</dependency>
```

## Usage

### Basic Usage

```java
import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;

import java.util.List;

public class Example {
    public static void main(String[] args) {
        try {
            // Create a parser instance
            BDEngineParser parser = new BDEngineParser();
            
            // Parse from file with automatic world transform calculation
            List<ProjectElement> elements = parser.parseFromFile("path/to/bdengine/project.json");
            
            // Process the elements
            for (ProjectElement element : elements) {
                System.out.println("Element: " + element);
                System.out.println("World Transform: " + element.getWorldTransform());
            }
        } catch (BDEngineParsingException e) {
            System.err.println("Error parsing BDEngine project: " + e.getMessage());
        }
    }
}
```

### Parsing from Different Sources

```java
// Parse from file with world transform calculation
List<ProjectElement> fromFile = parser.parseFromFile("path/to/project.json");

// Parse from input stream with world transform calculation
try (InputStream is = new FileInputStream("path/to/project.json")) {
    List<ProjectElement> fromStream = parser.parseFromInputStream(is);
}

// Parse from JSON string with world transform calculation
String jsonString = "[{...}]";  // Your BDEngine JSON data
List<ProjectElement> fromJson = parser.parseJsonString(jsonString);
```

### Raw Parsing (Without World Transform Calculation)

```java
// Parse from file without calculating world transforms
List<ProjectElement> fromFileRaw = parser.parseFromFileRaw("path/to/project.json");

// Parse from input stream without calculating world transforms
try (InputStream is = new FileInputStream("path/to/project.json")) {
    List<ProjectElement> fromStreamRaw = parser.parseFromInputStreamRaw(is);
}

// Parse from JSON string without calculating world transforms
String jsonString = "[{...}]";  // Your BDEngine JSON data
List<ProjectElement> fromJsonRaw = parser.parseJsonStringRaw(jsonString);

// Calculate world transforms manually later if needed
parser.calculateWorldTransformsForElements(fromJsonRaw);
```

### Using Custom Gson Instance

```java
Gson customGson = new GsonBuilder()
    .setPrettyPrinting()
    .serializeNulls()
    .create();

BDEngineParser parser = new BDEngineParser(customGson);
List<ProjectElement> elements = parser.parseFromFile("path/to/project.json");
```

## Error Handling

The library uses `BDEngineParsingException` to handle various parsing errors:

- Invalid file paths
- File read errors
- JSON syntax errors
- Null or empty inputs

## Requirements

- Java 17 or higher
- Gson 2.13.1 or higher
- JOML 1.10.8 or higher

## License

[LICENSE](LICENSE)