package dev.twme.bdengineparser.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the brightness settings for sky and blocks in the BD Engine.
 */
public class Brightness {
    @SerializedName("sky")
    private int sky;
    @SerializedName("block")
    private int block;

    /**
     * Gets the sky brightness.
     * @return the sky brightness value
     */
    public int getSky() {
        return sky;
    }

    /**
     * Sets the sky brightness.
     *
     * @param sky the sky brightness value
     */
    public void setSky(int sky) {
        this.sky = sky;
    }

    /**
     * Gets the block brightness.
     *
     * @return the block brightness value
     */
    public int getBlock() {
        return block;
    }

    /**
     * Sets the block brightness.
     *
     * @param block the block brightness value
     */
    public void setBlock(int block) {
        this.block = block;
    }
}
