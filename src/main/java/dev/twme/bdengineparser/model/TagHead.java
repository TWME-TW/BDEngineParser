package dev.twme.bdengineparser.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the head of a tag in the BD Engine.
 * This class encapsulates the value of the tag head, which is typically a string.
 */
public class TagHead {
    @SerializedName("Value")
    private String value;

    /**
     * Gets the value of the tag head.
     * @return the value of the tag head
     */
    public String getValue() { return value; }

    /**
     * Sets the value of the tag head.
     * @param value the value to set for the tag head
     */
    public void setValue(String value) { this.value = value; }
}