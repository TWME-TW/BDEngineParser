package dev.twme.bdengineparser.internal;

import dev.twme.bdengineparser.model.ProjectElement;
import org.joml.Matrix4f;

import java.util.List;

/**
 * WorldTransformCalculator is responsible for calculating the world transforms of ProjectElements.
 * It traverses the hierarchy of elements, applying local transformations to compute the world transform
 * relative to the world origin (0,0,0).
 * 
 * IMPORTANT: This class was fixed to use the correct matrix multiplication order.
 * The correct order is: localMatrix * parentWorldTransform (not parentWorldTransform * localMatrix).
 * This ensures that transformations are applied from the innermost element outward, which matches
 * how Minecraft's display entity transformation system works.
 * 
 * Additionally, defaultTransform is no longer applied as the analysis showed that the 'transforms'
 * field already contains all necessary transformation information including any default transforms.
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

        // Create the local transform matrix from the element's transforms
        Matrix4f localMatrix = TransformUtils.listToMatrix4f(element.getTransforms());
        
        // The correct order is: localMatrix * parentWorldTransform (reversed from before)
        Matrix4f currentElementWorldTransform = new Matrix4f(localMatrix).mul(parentWorldTransform);

        // Store the computed world transform for this element
        element.setWorldTransform(new Matrix4f(currentElementWorldTransform));

        // For children, we don't use defaultTransform anymore as the analysis shows
        // that transforms already contain all necessary transformation information
        Matrix4f parentTransformForChildren = new Matrix4f(currentElementWorldTransform);

        // Process children recursively
        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                calculateTransformRecursive(child, parentTransformForChildren);
            }
        }
    }
}
