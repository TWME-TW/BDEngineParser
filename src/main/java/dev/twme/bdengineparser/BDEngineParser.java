package dev.twme.bdengineparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets; // Assumes this is public in internal package
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dev.twme.bdengineparser.exception.BDEngineParsingException;
import dev.twme.bdengineparser.internal.TransformUtils;
import dev.twme.bdengineparser.internal.WorldTransformCalculator;
import dev.twme.bdengineparser.model.ProjectElement;

/**
 * BDEngineParser is a utility class for parsing BDEngine project files and calculating world transforms.
 */
public class BDEngineParser {

    private final Gson gson;
    private final WorldTransformCalculator transformCalculator;

    /**
     * Constructs a BDEngineParser with a default Gson instance.
     */
    public BDEngineParser() {
        this.gson = new GsonBuilder().create();
        this.transformCalculator = new WorldTransformCalculator();
    }

    /**
     * Constructs a BDEngineParser with a custom Gson instance.
     *
     * @param gson the Gson instance to use for parsing
     */
    public BDEngineParser(Gson gson) {
        this.gson = gson;
        this.transformCalculator = new WorldTransformCalculator();
    }

    // --- Raw Parsing Methods (without automatic transform calculation) ---

    /**
     * Parses a BDEngine project file from the specified file path without calculating world transforms.
     *
     * @param filePath the path to the BDEngine project file
     * @return a list of ProjectElement objects parsed from the file
     * @throws BDEngineParsingException if there is an error reading or parsing the file
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public List<ProjectElement> parseFromFileRaw(String filePath) throws BDEngineParsingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        try {
            Path path = Paths.get(filePath);
            String jsonContent = Files.readString(path);
            return parseJsonStringRaw(jsonContent);
        } catch (InvalidPathException e) {
            throw new BDEngineParsingException("Invalid file path: " + filePath, e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading file: " + filePath, e);
        }
    }

    /**
     * Parses a BDEngine project file from the specified InputStream without calculating world transforms.
     *
     * @param inputStream the InputStream containing the BDEngine project data
     * @return a list of ProjectElement objects parsed from the InputStream
     * @throws BDEngineParsingException if there is an error reading or parsing the InputStream
     * @throws IllegalArgumentException if inputStream is null
     */
    public List<ProjectElement> parseFromInputStreamRaw(InputStream inputStream) throws BDEngineParsingException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<ProjectElement>>() {}.getType();
            List<ProjectElement> elements = gson.fromJson(reader, listType);
            if (elements == null) { // Check for null result, e.g. if JSON string is "null"
                throw new BDEngineParsingException("Parsed result is null. The JSON content might represent a null value.");
            }
            return elements;
        } catch (JsonSyntaxException e) {
            throw new BDEngineParsingException("Error parsing JSON from input stream: Invalid JSON syntax.", e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading from input stream.", e);
        }
    }

    /**
     * Parses a JSON string representing a BDEngine project without calculating world transforms.
     *
     * @param jsonString the JSON string to parse
     * @return a list of ProjectElement objects parsed from the JSON string
     * @throws BDEngineParsingException if there is an error parsing the JSON string
     * @throws IllegalArgumentException if jsonString is null
     */
    public List<ProjectElement> parseJsonStringRaw(String jsonString) throws BDEngineParsingException {
        if (jsonString == null) {
            throw new IllegalArgumentException("JSON string cannot be null.");
        }
        try {
            Type listType = new TypeToken<List<ProjectElement>>() {}.getType();
            List<ProjectElement> elements = gson.fromJson(jsonString, listType);
            if (elements == null) {
                throw new BDEngineParsingException("Parsed result is null. The JSON might represent a null value or be empty in an unexpected way.");
            }
            return elements;
        } catch (JsonSyntaxException e) {
            throw new BDEngineParsingException("Error parsing JSON string: Invalid JSON syntax.", e);
        }
    }

    // --- BDEngine File Format Methods ---

    /**
     * Parses a BDEngine file (.bdengine) from the specified file path without calculating world transforms.
     * The .bdengine format contains base64-encoded, gzip-compressed JSON data.
     *
     * @param filePath the path to the .bdengine file
     * @return a list of ProjectElement objects parsed from the file
     * @throws BDEngineParsingException if there is an error reading or parsing the file
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public List<ProjectElement> parseBDEngineFileRaw(String filePath) throws BDEngineParsingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        try {
            Path path = Paths.get(filePath);
            String base64Content = Files.readString(path, StandardCharsets.UTF_8).trim();
            return parseBDEngineStringRaw(base64Content);
        } catch (InvalidPathException e) {
            throw new BDEngineParsingException("Invalid file path: " + filePath, e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading .bdengine file: " + filePath, e);
        }
    }

    /**
     * Parses a base64-encoded, gzip-compressed JSON string (BDEngine format) without calculating world transforms.
     *
     * @param base64String the base64-encoded string containing gzip-compressed JSON data
     * @return a list of ProjectElement objects parsed from the decompressed JSON
     * @throws BDEngineParsingException if there is an error decoding, decompressing, or parsing the data
     * @throws IllegalArgumentException if base64String is null
     */
    public List<ProjectElement> parseBDEngineStringRaw(String base64String) throws BDEngineParsingException {
        if (base64String == null) {
            throw new IllegalArgumentException("Base64 string cannot be null.");
        }
        
        try {
            // Step 1: Base64 decode
            byte[] compressedData = Base64.getDecoder().decode(base64String);
            
            // Step 2: Gzip decompress
            String jsonString;
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedData));
                 Reader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                
                StringBuilder jsonBuilder = new StringBuilder();
                char[] buffer = new char[8192];
                int charsRead;
                while ((charsRead = reader.read(buffer)) != -1) {
                    jsonBuilder.append(buffer, 0, charsRead);
                }
                jsonString = jsonBuilder.toString();
            }
            
            // Step 3: Parse JSON using existing method
            return parseJsonStringRaw(jsonString);
            
        } catch (IllegalArgumentException e) {
            throw new BDEngineParsingException("Invalid base64 encoding in BDEngine data.", e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error decompressing gzip data in BDEngine format.", e);
        }
    }

    /**
     * Parses a BDEngine file (.bdengine) from the specified InputStream without calculating world transforms.
     * The .bdengine format contains base64-encoded, gzip-compressed JSON data.
     *
     * @param inputStream the InputStream containing the .bdengine file data
     * @return a list of ProjectElement objects parsed from the file
     * @throws BDEngineParsingException if there is an error reading or parsing the InputStream
     * @throws IllegalArgumentException if inputStream is null
     */
    public List<ProjectElement> parseBDEngineInputStreamRaw(InputStream inputStream) throws BDEngineParsingException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            StringBuilder base64Builder = new StringBuilder();
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                base64Builder.append(buffer, 0, charsRead);
            }
            String base64Content = base64Builder.toString().trim();
            return parseBDEngineStringRaw(base64Content);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading .bdengine data from input stream.", e);
        }
    }

    // --- Combined Parsing and Transform Calculation Methods ---

    /**
     * Parses a BDEngine project file from the specified file path AND calculates world transforms for all elements.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param filePath the path to the BDEngine project file
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error reading or parsing the file
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public List<ProjectElement> parseFromFile(String filePath) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseFromFileRaw(filePath);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }

    /**
     * Parses a BDEngine project file from the specified InputStream AND calculates world transforms for all elements.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param inputStream the InputStream containing the BDEngine project data
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error reading or parsing the InputStream
     * @throws IllegalArgumentException if inputStream is null
     */
    public List<ProjectElement> parseFromInputStream(InputStream inputStream) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseFromInputStreamRaw(inputStream);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }

    /**
     * Parses a JSON string representing a BDEngine project AND calculates world transforms for all elements.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param jsonString the JSON string to parse
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error parsing the JSON string
     * @throws IllegalArgumentException if jsonString is null
     */
    public List<ProjectElement> parseJsonString(String jsonString) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseJsonStringRaw(jsonString);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }

    /**
     * Parses a BDEngine file (.bdengine) from the specified file path AND calculates world transforms for all elements.
     * The .bdengine format contains base64-encoded, gzip-compressed JSON data.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param filePath the path to the .bdengine file
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error reading or parsing the file
     * @throws IllegalArgumentException if filePath is null or empty
     */
    public List<ProjectElement> parseBDEngineFile(String filePath) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseBDEngineFileRaw(filePath);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }

    /**
     * Parses a base64-encoded, gzip-compressed JSON string (BDEngine format) AND calculates world transforms for all elements.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param base64String the base64-encoded string containing gzip-compressed JSON data
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error decoding, decompressing, or parsing the data
     * @throws IllegalArgumentException if base64String is null
     */
    public List<ProjectElement> parseBDEngineString(String base64String) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseBDEngineStringRaw(base64String);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }

    /**
     * Parses a BDEngine file (.bdengine) from the specified InputStream AND calculates world transforms for all elements.
     * The .bdengine format contains base64-encoded, gzip-compressed JSON data.
     * The calculated transforms are stored in each {@link ProjectElement#getWorldTransform()}.
     *
     * @param inputStream the InputStream containing the .bdengine file data
     * @return a list of ProjectElement objects with their world transforms calculated
     * @throws BDEngineParsingException if there is an error reading or parsing the InputStream
     * @throws IllegalArgumentException if inputStream is null
     */
    public List<ProjectElement> parseBDEngineInputStream(InputStream inputStream) throws BDEngineParsingException {
        List<ProjectElement> rootElements = parseBDEngineInputStreamRaw(inputStream);
        calculateWorldTransformsForElements(rootElements);
        return rootElements;
    }


    // --- Standalone Transform Calculation Method ---

    /**
     * Calculates world transforms for a pre-parsed list of ProjectElements.
     * This method modifies the {@link ProjectElement} instances in the list by setting their
     * world transform via {@link ProjectElement#setWorldTransform(org.joml.Matrix4f)}.
     * The calculated transforms are relative to the world origin (0,0,0).
     *
     * @param rootElements The list of root ProjectElements. If null or empty, the method does nothing.
     */
    public void calculateWorldTransformsForElements(List<ProjectElement> rootElements) {
        if (rootElements != null && !rootElements.isEmpty()) {
            this.transformCalculator.calculateWorldTransforms(rootElements);
        }
    }

    /**
     * Creates a 4x4 matrix representing a rotation around an arbitrary axis.
     * This can be used by clients of the library to construct custom transformations
     * that can then be multiplied with the world transforms calculated by this parser.
     *
     * @param axisX The x-component of the rotation axis.
     * @param axisY The y-component of the rotation axis.
     * @param axisZ The z-component of the rotation axis.
     * @param angleRad The angle of rotation in radians.
     * @return A new {@link Matrix4f} representing the specified axis-angle rotation.
     * @throws IllegalArgumentException if the axis vector has zero length.
     */
    public Matrix4f createAxisAngleRotationMatrix(float axisX, float axisY, float axisZ, float angleRad) {
        // We call the static method from our internal TransformUtils
        return TransformUtils.createRotationAroundAxisMatrix(axisX, axisY, axisZ, angleRad);
    }

    /**
     * Creates a 4x4 matrix representing a rotation around an arbitrary axis.
     * This can be used by clients of the library to construct custom transformations
     * that can then be multiplied with the world transforms calculated by this parser.
     *
     * @param axis The axis of rotation (will be normalized if not already).
     * @param angleRad The angle of rotation in radians.
     * @return A new {@link Matrix4f} representing the specified axis-angle rotation.
     * @throws IllegalArgumentException if axis is null or a zero vector.
     */
    public Matrix4f createAxisAngleRotationMatrix(Vector3f axis, float angleRad) {
        // We call the static method from our internal TransformUtils
        return TransformUtils.createRotationAroundAxisMatrix(axis, angleRad);
    }

    /**
     * Calculates the total number of elements (including all children recursively)
     * in a given list of root {@link ProjectElement}s.
     *
     * @param rootElements The list of root ProjectElement objects.
     * @return The total count of all elements. Returns 0 if the list is null or empty.
     */
    public int getTotalElementCount(List<ProjectElement> rootElements) {
        if (rootElements == null || rootElements.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (ProjectElement element : rootElements) {
            count += countElementsRecursive(element);
        }
        return count;
    }

    /**
     * Helper method to recursively count an element and its children.
     * @param element The current ProjectElement.
     * @return The count of this element plus all its descendants.
     */
    private int countElementsRecursive(ProjectElement element) {
        if (element == null) {
            return 0;
        }
        int count = 1; // Count the current element
        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                count += countElementsRecursive(child); // Add count of children
            }
        }
        return count;
    }
}