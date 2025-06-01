package dev.twme.bdengineparser.internal;

import dev.twme.bdengineparser.model.DefaultTransform;
import dev.twme.bdengineparser.model.Rotation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * Utility class for transforming data structures into JOML Matrix4f objects.
 * Provides methods to convert lists of doubles and DefaultTransform objects
 * into Matrix4f representations, applying the correct transformation order.
 */
public class TransformUtils {
    /**
     * Converts a list of 16 doubles into a Matrix4f.
     * Assumes column-major order for the list.
     * @param transformList the list of doubles representing a 4x4 matrix
     * @return a Matrix4f constructed from the list
     */
    public static Matrix4f listToMatrix4f(List<Double> transformList) {
        if (transformList == null || transformList.size() != 16) {
            // Or throw a more specific internal exception if preferred
            throw new IllegalArgumentException("Transform list must contain 16 elements for a 4x4 matrix.");
        }
        return new Matrix4f(
                transformList.get(0).floatValue(), transformList.get(1).floatValue(), transformList.get(2).floatValue(), transformList.get(3).floatValue(),
                transformList.get(4).floatValue(), transformList.get(5).floatValue(), transformList.get(6).floatValue(), transformList.get(7).floatValue(),
                transformList.get(8).floatValue(), transformList.get(9).floatValue(), transformList.get(10).floatValue(), transformList.get(11).floatValue(),
                transformList.get(12).floatValue(), transformList.get(13).floatValue(), transformList.get(14).floatValue(), transformList.get(15).floatValue()
        );
    }

    /**
     * Converts a DefaultTransform object (position, rotation, scale) into a Matrix4f.
     * Applies transformations in Scale -> Rotate -> Translate order.
     * @param dt the DefaultTransform object to convert
     * @return a Matrix4f representing the transformation
     */
    public static Matrix4f defaultTransformToMatrix4f(DefaultTransform dt) {
        if (dt == null) {
            // Should not happen if called correctly, but good for robustness
            return new Matrix4f().identity();
        }

        Matrix4f matrix = new Matrix4f().identity();

        // 1. Scale
        List<Double> scaleList = dt.getScale();
        if (scaleList != null && scaleList.size() == 3) {
            matrix.scale(
                    scaleList.get(0).floatValue(),
                    scaleList.get(1).floatValue(),
                    scaleList.get(2).floatValue()
            );
        }

        // 2. Rotation
        Rotation rot = dt.getRotation();
        if (rot != null) {
            // IMPORTANT: Verify the meaning of rot.x, rot.y, rot.z from your data source.
            // Assuming they are Euler angles in RADIANS and you want an INTRINSIC YXZ order.
            // (Yaw around Y, then Pitch around new X, then Roll around new Z)
            matrix.rotateY((float) rot.getY());
            matrix.rotateX((float) rot.getX());
            matrix.rotateZ((float) rot.getZ());
            // If they are degrees, convert: (float) Math.toRadians(rot.getY())
            // If the order is different, adjust the calls.
        }

        // 3. Translation
        List<Double> posList = dt.getPosition();
        if (posList != null && posList.size() == 3) {
            matrix.translate(
                    posList.get(0).floatValue(),
                    posList.get(1).floatValue(),
                    posList.get(2).floatValue()
            );
        }
        return matrix;
    }

    /**
     * Creates a new rotation matrix that rotates around a given axis by a specified angle.
     *
     * @param axisX The x-component of the rotation axis.
     * @param axisY The y-component of the rotation axis.
     * @param axisZ The z-component of the rotation axis.
     * @param angleRad The angle of rotation in radians.
     * @return A new Matrix4f representing ONLY the specified axis-angle rotation.
     * @throws IllegalArgumentException if the axis vector has zero length (after normalization attempt).
     */
    public static Matrix4f createRotationAroundAxisMatrix(float axisX, float axisY, float axisZ, float angleRad) {
        // JOML's rotation(angle, x, y, z) expects the axis to be implicitly normalized if its length is not 0.
        // However, it's good practice to handle the zero-length axis case explicitly.
        float lengthSq = axisX * axisX + axisY * axisY + axisZ * axisZ;
        if (lengthSq < 0.00001f) { // Check for zero vector before normalization attempt by JOML
            throw new IllegalArgumentException("Rotation axis cannot be a zero vector.");
        }
        // Create a new identity matrix and then set its rotation.
        // The rotation(angle, x, y, z) method sets the matrix to be this rotation.
        return new Matrix4f().identity().rotation(angleRad, axisX, axisY, axisZ);
    }

    /**
     * Creates a new rotation matrix that rotates around a given axis by a specified angle.
     *
     * @param axis The axis of rotation. JOML's rotation(angle, Vector3fc) will handle normalization
     *             if the vector is not zero length.
     * @param angleRad The angle of rotation in radians.
     * @return A new Matrix4f representing ONLY the specified axis-angle rotation.
     * @throws IllegalArgumentException if axis is null or a zero vector.
     */
    public static Matrix4f createRotationAroundAxisMatrix(Vector3f axis, float angleRad) {
        if (axis == null || axis.lengthSquared() < 0.00001f) {
            throw new IllegalArgumentException("Rotation axis cannot be null or a zero vector.");
        }
        // Create a new identity matrix and then set its rotation.
        // The rotation(angle, Vector3fc) method sets the matrix to be this rotation.
        // JOML will normalize the axis if its length is not 0.
        return new Matrix4f().identity().rotation(angleRad, axis);
    }
}
