package dev.twme.bdengineparser;

import dev.twme.bdengineparser.model.ProjectElement;
import org.joml.Matrix4f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify that the transformation calculation fixes work correctly
 * with the restaurant-sign.json test case.
 */
class TransformationTest {

    @Test
    @DisplayName("驗證 restaurant-sign.json 的轉換計算是否正確")
    void testRestaurantSignTransformations() throws Exception {
        // 解析測試檔案
        BDEngineParser parser = new BDEngineParser();
        List<ProjectElement> elements = parser.parseFromFile("restaurant-sign.json");
        
        assertNotNull(elements, "解析結果不應為 null");
        assertFalse(elements.isEmpty(), "解析結果不應為空");
        
        // 收集所有顯示實體
        List<ProjectElement> displayEntities = collectDisplayEntities(elements);
        assertEquals(7, displayEntities.size(), "應該有 7 個顯示實體");
        
        // 驗證第一個 item_display 的變換矩陣
        ProjectElement firstItemDisplay = displayEntities.get(0);
        assertTrue(firstItemDisplay.getIsItemDisplay(), "第一個實體應該是 item_display");
        
        Matrix4f actualMatrix = firstItemDisplay.getWorldTransform();
        assertNotNull(actualMatrix, "世界變換矩陣不應為 null");
        
        // 預期的變換矩陣值 (從 restaurant-sign.txt 中提取)
        Matrix4f expectedMatrix = new Matrix4f(
            1f, 0f, 0f, 0f,
            0f, 0.9658446719f, 0.965442044f, 0.8008294014f,
            0f, 0.257992044f, 0.2594946719f, 0.1496755161f,
            0f, 0f, 0f, 1f
        );
        
        // 檢查矩陣是否匹配（允許小誤差）
        assertMatrixEquals(expectedMatrix, actualMatrix, 0.0001f, 
            "第一個 item_display 的變換矩陣應該匹配預期結果");
        
        // 驗證第一個 block_display (bell) 的變換矩陣
        ProjectElement firstBell = displayEntities.get(1);
        assertTrue(firstBell.getIsBlockDisplay(), "第二個實體應該是 block_display");
        assertTrue(firstBell.getName().contains("bell"), "應該是 bell 實體");
        
        Matrix4f actualBellMatrix = firstBell.getWorldTransform();
        Matrix4f expectedBellMatrix = new Matrix4f(
            0f, -1f, 0f, 0.5625f,
            0.9659258263f, 0f, -0.2588190451f, 0.1455857129f,
            0.2588190451f, 0f, 0.9659258263f, -0.5433332773f,
            0f, 0f, 0f, 1f
        );
        
        assertMatrixEquals(expectedBellMatrix, actualBellMatrix, 0.0001f,
            "第一個 bell 的變換矩陣應該匹配預期結果");
    }
    
    /**
     * 收集所有顯示實體（item_display 和 block_display）
     */
    private List<ProjectElement> collectDisplayEntities(List<ProjectElement> elements) {
        java.util.List<ProjectElement> result = new java.util.ArrayList<>();
        for (ProjectElement element : elements) {
            collectDisplayEntitiesRecursive(element, result);
        }
        return result;
    }
    
    private void collectDisplayEntitiesRecursive(ProjectElement element, List<ProjectElement> result) {
        // 如果是顯示實體
        if ((element.getIsItemDisplay() != null && element.getIsItemDisplay()) ||
            (element.getIsBlockDisplay() != null && element.getIsBlockDisplay())) {
            result.add(element);
        }
        
        // 遞迴處理子元素
        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                collectDisplayEntitiesRecursive(child, result);
            }
        }
    }
    
    /**
     * 檢查兩個矩陣是否在指定誤差範圍內相等
     */
    private void assertMatrixEquals(Matrix4f expected, Matrix4f actual, float tolerance, String message) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float expectedVal = getMatrixElement(expected, i, j);
                float actualVal = getMatrixElement(actual, i, j);
                float diff = Math.abs(expectedVal - actualVal);
                
                if (diff > tolerance) {
                    fail(String.format("%s - 元素 [%d,%d]: 預期 %.6f, 實際 %.6f, 差異 %.6f (超過容許誤差 %.6f)",
                        message, i, j, expectedVal, actualVal, diff, tolerance));
                }
            }
        }
    }
    
    private float getMatrixElement(Matrix4f matrix, int row, int col) {
        return switch (row * 4 + col) {
            case 0 -> matrix.m00(); case 1 -> matrix.m01(); case 2 -> matrix.m02(); case 3 -> matrix.m03();
            case 4 -> matrix.m10(); case 5 -> matrix.m11(); case 6 -> matrix.m12(); case 7 -> matrix.m13();
            case 8 -> matrix.m20(); case 9 -> matrix.m21(); case 10 -> matrix.m22(); case 11 -> matrix.m23();
            case 12 -> matrix.m30(); case 13 -> matrix.m31(); case 14 -> matrix.m32(); case 15 -> matrix.m33();
            default -> throw new IllegalArgumentException("Invalid matrix indices: " + row + ", " + col);
        };
    }
}
