package dev.twme.bdengineparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dev.twme.bdengineparser.exception.BDEngineParsingException;
import dev.twme.bdengineparser.model.ProjectElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * BDEngineParser is a utility class for parsing BD Engine project files.
 */
public class BDEngineParser {

    private final Gson gson;

    /**
     * Constructs a BDEngineParser with a default Gson instance.
     */
    public BDEngineParser() {
        this.gson = new GsonBuilder().create();
    }

    /**
     * Constructs a BDEngineParser with a custom Gson instance.
     *
     * @param gson the Gson instance to use for parsing
     */
    public BDEngineParser(Gson gson) {
        this.gson = gson;
    }

    /**
     * Parses a BD Engine project file from the specified file path.
     *
     * @param filePath the path to the BD Engine project file
     * @return a list of ProjectElement objects parsed from the file
     * @throws BDEngineParsingException if there is an error reading or parsing the file
     */
    public List<ProjectElement> parseFromFile(String filePath) throws BDEngineParsingException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        try {
            Path path = Paths.get(filePath);
            String jsonContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return parseJsonString(jsonContent);
        } catch (InvalidPathException e) {
            throw new BDEngineParsingException("Invalid file path: " + filePath, e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading file: " + filePath, e);
        }
    }

    /**
     * Parses a BD Engine project file from the specified InputStream.
     *
     * @param inputStream the InputStream containing the BD Engine project data
     * @return a list of ProjectElement objects parsed from the InputStream
     * @throws BDEngineParsingException if there is an error reading or parsing the InputStream
     */
    public List<ProjectElement> parseFromInputStream(InputStream inputStream) throws BDEngineParsingException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<ProjectElement>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (JsonSyntaxException e) {
            throw new BDEngineParsingException("Error parsing JSON from input stream: Invalid JSON syntax.", e);
        } catch (IOException e) {
            throw new BDEngineParsingException("Error reading from input stream.", e);
        }
    }

    /**
     * Parses a JSON string representing a BD Engine project.
     *
     * @param jsonString the JSON string to parse
     * @return a list of ProjectElement objects parsed from the JSON string
     * @throws BDEngineParsingException if there is an error parsing the JSON string
     */
    public List<ProjectElement> parseJsonString(String jsonString) throws BDEngineParsingException {
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
}
