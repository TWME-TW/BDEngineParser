package dev.twme.bdengineparser.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the text options for an entity in the BD Engine.
 * This class encapsulates various text styling options such as color, alpha, background color,
 * and text formatting options like bold, italic, underline, and strike-through.
 */
public class TextOptions {
    @SerializedName("color")
    private String color;
    @SerializedName("alpha")
    private double alpha;
    @SerializedName("backgroundColor")
    private String backgroundColor;
    @SerializedName("backgroundAlpha")
    private double backgroundAlpha;
    @SerializedName("bold")
    private boolean bold;
    @SerializedName("italic")
    private boolean italic;
    @SerializedName("underline")
    private boolean underline;

    @SerializedName("strikeThrough")
    private boolean strikeThrough;

    @SerializedName("lineLength")
    private int lineLength;
    @SerializedName("align")
    private String align;
    @SerializedName("obfuscated")
    private boolean obfuscated;

    /**
     * Gets the color of the text.
     * @return the color as a string
     */
    public String getColor() { return color; }

    /**
     * Sets the color of the text.
     * @param color the color to set as a string
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Gets the alpha value of the text.
     * @return the alpha value as a double
     */
    public double getAlpha() { return alpha; }

    /**
     * Sets the alpha value of the text.
     * @param alpha the alpha value to set as a double
     */
    public void setAlpha(double alpha) { this.alpha = alpha; }

    /**
     * Gets the background color of the text.
     * @return the background color as a string
     */
    public String getBackgroundColor() { return backgroundColor; }

    /**
     * Sets the background color of the text.
     * @param backgroundColor the background color to set as a string
     */
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    /**
     * Gets the alpha value of the background.
     * @return the background alpha value as a double
     */
    public double getBackgroundAlpha() { return backgroundAlpha; }

    /**
     * Sets the alpha value of the background.
     * @param backgroundAlpha the background alpha value to set as a double
     */
    public void setBackgroundAlpha(double backgroundAlpha) { this.backgroundAlpha = backgroundAlpha; }

    /**
     * Gets whether the text is bold.
     * @return true if the text is bold, false otherwise
     */
    public boolean isBold() { return bold; }

    /**
     * Sets whether the text is bold.
     * @param bold true to set the text as bold, false otherwise
     */
    public void setBold(boolean bold) { this.bold = bold; }

    /**
     * Gets whether the text is italic.
     * @return true if the text is italic, false otherwise
     */
    public boolean isItalic() { return italic; }

    /**
     * Sets whether the text is italic.
     * @param italic true to set the text as italic, false otherwise
     */
    public void setItalic(boolean italic) { this.italic = italic; }

    /**
     * Gets whether the text is underlined.
     * @return true if the text is underlined, false otherwise
     */
    public boolean isUnderline() { return underline; }

    /**
     * Sets whether the text is underlined.
     * @param underline true to set the text as underlined, false otherwise
     */
    public void setUnderline(boolean underline) { this.underline = underline; }

    /**
     * Gets whether the text has a strike-through effect.
     * @return true if the text has a strike-through, false otherwise
     */
    public boolean isStrikeThrough() { return strikeThrough; }

    /**
     * Sets whether the text has a strike-through effect.
     * @param strikeThrough true to set the text as having a strike-through, false otherwise
     */
    public void setStrikeThrough(boolean strikeThrough) { this.strikeThrough = strikeThrough; }

    /**
     * Gets the length of the line for the text.
     * @return the line length as an integer
     */
    public int getLineLength() { return lineLength; }

    /**
     * Sets the length of the line for the text.
     * @param lineLength the line length to set as an integer
     */
    public void setLineLength(int lineLength) { this.lineLength = lineLength; }

    /**
     * Gets the alignment of the text.
     * @return the alignment as a string
     */
    public String getAlign() { return align; }

    /**
     * Sets the alignment of the text.
     * @param align the alignment to set as a string
     */
    public void setAlign(String align) { this.align = align; }

    /**
     * Gets whether the text is obfuscated.
     * @return true if the text is obfuscated, false otherwise
     */
    public boolean isObfuscated() { return obfuscated; }

    /**
     * Sets whether the text is obfuscated.
     * @param obfuscated true to set the text as obfuscated, false otherwise
     */
    public void setObfuscated(boolean obfuscated) { this.obfuscated = obfuscated; }

    /**
     * Returns a string representation of the TextOptions object.
     * @return a string containing the text options
     */
    @Override
    public String toString() {
        return "TextOptions{" +
                "color='" + color + '\'' +
                ", alpha=" + alpha +
                ", bold=" + bold +
                ", italic=" + italic +
                ", align='" + align + '\'' +
                '}';
    }
}