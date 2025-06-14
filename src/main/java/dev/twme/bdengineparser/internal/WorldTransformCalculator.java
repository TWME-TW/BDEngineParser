package dev.twme.bdengineparser.internal;

import dev.twme.bdengineparser.model.ProjectElement;
import org.joml.Matrix4f;

import java.util.List;

/**
 * WorldTransformCalculator is responsible for calculating the world transforms of ProjectElements.
 * It traverses the hierarchy of elements, applying local transformations to compute the world transform
 * relative to the world origin (0,0,0).
 */
public class WorldTransformCalculator {
    /**
     * Calculates the world transforms for a list of root ProjectElements and their children.
     * The calculated world transform is stored in each ProjectElement's 'worldTransform' field.
     * These transforms are relative to the world origin (0,0,0).
     * @param rootElements The list of root ProjectElements. If null or empty, the method does nothing.
     */
    public void calculateWorldTransforms(List<ProjectElement> rootElements) {
        if (rootElements == null) return;
        Matrix4f initialParentTransform = new Matrix4f().identity(); // World origin
        for (ProjectElement rootElement : rootElements) {
            calculateTransformRecursive(rootElement, initialParentTransform);
        }
    }

    /**
     * Recursively calculates the world transform for a ProjectElement and its children.
     * @param element The ProjectElement to calculate the world transform for.
     * @param parentWorldTransform The world transform of the parent element, used to compute the child's world transform.
     */
    private void calculateTransformRecursive(ProjectElement element, Matrix4f parentWorldTransform) {
        if (element == null) return;

        Matrix4f localMatrix = TransformUtils.listToMatrix4f(element.getTransforms());
        Matrix4f currentElementWorldTransform = new Matrix4f(parentWorldTransform).mul(localMatrix);

        element.setWorldTransform(new Matrix4f(currentElementWorldTransform)); // Store a copy

        Matrix4f parentTransformForChildren = new Matrix4f(currentElementWorldTransform);

        if (element.getIsCollection() != null && element.getIsCollection() && element.getDefaultTransform() != null) {
            Matrix4f defaultTransformMatrix = TransformUtils.defaultTransformToMatrix4f(element.getDefaultTransform());
            parentTransformForChildren.mul(defaultTransformMatrix);
        }

        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                calculateTransformRecursive(child, parentTransformForChildren);
            }
        }
    }
}
