# BDEngine Parser Library

A comprehensive Java library for parsing BDEngine project structures and native .bdengine files into Java objects.

## Overview

BDEngineParser is a powerful utility library designed to parse both traditional JSON-formatted BDEngine project files and native .bdengine files (base64-encoded, gzip-compressed format). It converts project data into structured Java objects that can be easily manipulated in your application, with automatic calculation of world transforms for all elements in the project hierarchy.

## Features

- **Multiple Input Formats Support**:
  - JSON-formatted project files
  - Native .bdengine files (base64-encoded, gzip-compressed)
  - File paths, input streams, and direct strings
- **Transform Calculation**:
  - Automatic world transform calculation for all elements
  - Raw parsing options (without transform calculation)
  - Custom axis-angle rotation matrix creation utilities
- **Flexible API**:
  - Customizable parsing with custom Gson instances
  - Element counting utilities
  - Comprehensive error handling
- **Performance Optimized**:
  - Efficient parsing of large project hierarchies
  - Memory-conscious design for complex nested structures

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
    <version>2.2.1</version>
</dependency>
```

## Usage

### Basic Usage

### Parsing Traditional JSON Files

```java
import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;

import java.util.List;

public class BasicExample {
    public static void main(String[] args) {
        try {
            // Create a parser instance
            BDEngineParser parser = new BDEngineParser();
            
            // Parse from JSON file with automatic world transform calculation
            List<ProjectElement> elements = parser.parseFromFile("path/to/bdengine/project.json");
            
            // Process the elements
            for (ProjectElement element : elements) {
                System.out.println("Element: " + element);
                System.out.println("World Transform: " + element.getWorldTransform());
                System.out.println("Total elements in project: " + parser.getTotalElementCount(elements));
            }
        } catch (BDEngineParsingException e) {
            System.err.println("Error parsing BDEngine project: " + e.getMessage());
        }
    }
}
```

### Parsing Native .bdengine Files

```java
public class BDEngineFileExample {
    public static void main(String[] args) {
        try {
            BDEngineParser parser = new BDEngineParser();
            
            // Parse native .bdengine file (base64-encoded, gzip-compressed)
            List<ProjectElement> elements = parser.parseBDEngineFile("path/to/project.bdengine");
            
            // Or parse from base64 string directly
            String base64Data = "H4sIAAAAAAAAA..."; // Your base64-encoded data
            List<ProjectElement> elementsFromString = parser.parseBDEngineString(base64Data);
            
            System.out.println("Parsed " + parser.getTotalElementCount(elements) + " elements");
        } catch (BDEngineParsingException e) {
            System.err.println("Error parsing .bdengine file: " + e.getMessage());
        }
    }
}
```

### Parsing from Different Sources

```java
// Parse JSON files with world transform calculation
List<ProjectElement> fromFile = parser.parseFromFile("path/to/project.json");

// Parse .bdengine files with world transform calculation  
List<ProjectElement> fromBDEngine = parser.parseBDEngineFile("path/to/project.bdengine");

// Parse from input stream with world transform calculation
try (InputStream is = new FileInputStream("path/to/project.json")) {
    List<ProjectElement> fromStream = parser.parseFromInputStream(is);
}

// Parse from .bdengine input stream
try (InputStream is = new FileInputStream("path/to/project.bdengine")) {
    List<ProjectElement> fromBDEngineStream = parser.parseBDEngineInputStream(is);
}

// Parse from JSON string with world transform calculation
String jsonString = "[{...}]";  // Your BDEngine JSON data
List<ProjectElement> fromJson = parser.parseJsonString(jsonString);

// Parse from base64-encoded, gzip-compressed string (.bdengine format)
String base64String = "H4sIAAAAAAAAA...";  // Your .bdengine data
List<ProjectElement> fromBDEngineString = parser.parseBDEngineString(base64String);
```

### Raw Parsing (Without World Transform Calculation)

```java
// Parse JSON files without calculating world transforms
List<ProjectElement> fromFileRaw = parser.parseFromFileRaw("path/to/project.json");

// Parse .bdengine files without calculating world transforms
List<ProjectElement> fromBDEngineRaw = parser.parseBDEngineFileRaw("path/to/project.bdengine");

// Parse from input stream without calculating world transforms
try (InputStream is = new FileInputStream("path/to/project.json")) {
    List<ProjectElement> fromStreamRaw = parser.parseFromInputStreamRaw(is);
}

// Parse from .bdengine input stream without calculating world transforms
try (InputStream is = new FileInputStream("path/to/project.bdengine")) {
    List<ProjectElement> fromBDEngineStreamRaw = parser.parseBDEngineInputStreamRaw(is);
}

// Parse from JSON string without calculating world transforms
String jsonString = "[{...}]";  // Your BDEngine JSON data
List<ProjectElement> fromJsonRaw = parser.parseJsonStringRaw(jsonString);

// Parse from base64 string without calculating world transforms
String base64String = "H4sIAAAAAAAAA...";  // Your .bdengine data
List<ProjectElement> fromBDEngineStringRaw = parser.parseBDEngineStringRaw(base64String);

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

### Advanced Features

```java
// Count total elements in a project (including all children recursively)
int totalElements = parser.getTotalElementCount(elements);
System.out.println("Total elements: " + totalElements);

// Create custom rotation matrices for advanced transformations
Matrix4f customRotation = parser.createAxisAngleRotationMatrix(1.0f, 0.0f, 0.0f, Math.PI/4);
Vector3f axis = new Vector3f(0, 1, 0); // Y-axis rotation
Matrix4f yRotation = parser.createAxisAngleRotationMatrix(axis, Math.PI/2);

// Access detailed element properties
for (ProjectElement element : elements) {
    System.out.println("Name: " + element.getName());
    System.out.println("Is Collection: " + element.getIsCollection());
    System.out.println("Is Item Display: " + element.getIsItemDisplay());
    System.out.println("Is Block Display: " + element.getIsBlockDisplay());
    System.out.println("Is Text Display: " + element.getIsTextDisplay());
    System.out.println("Children count: " + (element.getChildren() != null ? element.getChildren().size() : 0));
    
    if (element.getBrightness() != null) {
        System.out.println("Brightness settings available");
    }
    
    if (element.getOptions() != null) {
        System.out.println("Text options available");
    }
}
```

## File Format Support

The library supports two main input formats:

### 1. JSON Format

Traditional JSON-formatted BDEngine project files containing element definitions, transformations, and hierarchical structure.

### 2. Native .bdengine Format

Native .bdengine files use a compressed format:

- **Base64 encoding** of the outer layer
- **Gzip compression** of the JSON data
- **Optimized** for storage and transmission

Both formats contain the same structural data and are automatically handled by the appropriate parsing methods.

## Error Handling

The library uses `BDEngineParsingException` to handle various parsing errors:

- Invalid file paths
- File read errors  
- JSON syntax errors
- Null or empty inputs
- Base64 decoding errors (for .bdengine files)
- Gzip decompression errors (for .bdengine files)
- Invalid transformation matrices

## Requirements

- Java 17 or higher
- Gson 2.13.1 or higher
- JOML 1.10.8 or higher

## License

[LICENSE](LICENSE)
