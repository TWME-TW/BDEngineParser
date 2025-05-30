package dev.twme.bdengineparser.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the default transformation settings for an entity in the BD Engine.
 */
public class DefaultTransform {
    @SerializedName("position")
    private List<Double> position;
    @SerializedName("rotation")
    private Rotation rotation;
    @SerializedName("scale")
    private List<Double> scale;

    /**
     * Gets the position of the entity.
     * @return the position as a list of doubles
     */
    public List<Double> getPosition() { return position; }

    /**
     * Sets the position of the entity.
     * @param position the position as a list of doubles
     */
    public void setPosition(List<Double> position) { this.position = position; }

    /**
     * Gets the rotation of the entity.
     * @return the rotation as a Rotation object
     */
    public Rotation getRotation() { return rotation; }

    /**
     * Sets the rotation of the entity.
     * @param rotation the rotation as a Rotation object
     */
    public void setRotation(Rotation rotation) { this.rotation = rotation; }

    /**
     * Gets the scale of the entity.
     * @return the scale as a list of doubles
     */
    public List<Double> getScale() { return scale; }

    /**
     * Sets the scale of the entity.
     * @param scale the scale as a list of doubles
     */
    public void setScale(List<Double> scale) { this.scale = scale; }
}
