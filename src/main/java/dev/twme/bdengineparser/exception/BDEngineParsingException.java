package dev.twme.bdengineparser.exception;

/**
 * Exception thrown when there is an error parsing BD Engine data.
 */
public class BDEngineParsingException extends Exception {
    /**
     * Constructs a new BDEngineParsingException with the specified detail message.
     *
     * @param message the detail message
     */
    public BDEngineParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a new BDEngineParsingException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public BDEngineParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
