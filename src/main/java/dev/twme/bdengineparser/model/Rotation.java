package dev.twme.bdengineparser.model;

/**
 * Represents a rotation in the BD Engine.
 * This class encapsulates the rotation values around the x, y, and z axes.
 */
public class Rotation {
    private double x;
    private double y;
    private double z;

    /**
     * Gets the rotation around the x-axis.
     * @return the rotation value around the x-axis
     */
    public double getX() { return x; }

    /**
     * Sets the rotation around the x-axis.
     * @param x the rotation value to set for the x-axis
     */
    public void setX(double x) { this.x = x; }

    /**
     * Gets the rotation around the y-axis.
     * @return the rotation value around the y-axis
     */
    public double getY() { return y; }

    /**
     * Sets the rotation around the y-axis.
     * @param y the rotation value to set for the y-axis
     */
    public void setY(double y) { this.y = y; }

    /**
     * Gets the rotation around the z-axis.
     * @return the rotation value around the z-axis
     */
    public double getZ() { return z; }

    /**
     * Sets the rotation around the z-axis.
     * @param z the rotation value to set for the z-axis
     */
    public void setZ(double z) { this.z = z; }
}
