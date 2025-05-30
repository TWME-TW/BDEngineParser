# BDEngine Parser Library

A Java library for parsing BDEngine project structures into Java objects.

## Overview

BDEngineParser is a utility library designed to parse BDEngine project files. It converts JSON-formatted BDEngine project data into Java objects that can be easily manipulated in your application.

## Features

- Parse BDEngine project files from:
  - File paths
  - Input streams
  - JSON strings
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
    <version>1.0</version>
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
            
            // Parse from file
            List<ProjectElement> elements = parser.parseFromFile("path/to/bdengine/project.json");
            
            // Process the elements
            for (ProjectElement element : elements) {
                System.out.println("Element: " + element);
            }
        } catch (BDEngineParsingException e) {
            System.err.println("Error parsing BDEngine project: " + e.getMessage());
        }
    }
}
```

### Parsing from Different Sources

```java
// Parse from file
List<ProjectElement> fromFile = parser.parseFromFile("path/to/project.json");

// Parse from input stream
try (InputStream is = new FileInputStream("path/to/project.json")) {
    List<ProjectElement> fromStream = parser.parseFromInputStream(is);
}

// Parse from JSON string
String jsonString = "[{...}]";  // Your BDEngine JSON data
List<ProjectElement> fromJson = parser.parseJsonString(jsonString);
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

## License

[LICENSE](LICENSE)