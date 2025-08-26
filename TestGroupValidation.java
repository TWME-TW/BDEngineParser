import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix4f;

import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;

public class TestGroupValidation {
    public static void main(String[] args) {
        try {
            System.out.println("=== 驗證 test-group.json 的轉換計算 ===");
            
            BDEngineParser parser = new BDEngineParser();
            List<ProjectElement> elements = parser.parseFromFile("test-group.json");
            
            System.out.println("成功解析 test-group.json");
            
            // 收集顯示實體
            List<ProjectElement> displayEntities = collectDisplayEntities(elements);
            System.out.printf("發現 %d 個顯示實體\n\n", displayEntities.size());
            
            // 從 text-group.txt 中提取的預期轉換矩陣
            List<List<Double>> expectedTransforms = Arrays.asList(
                // 第一個 block_display: chiseled_red_sandstone
                Arrays.asList(1.0, 0.0, 0.0, 0.2071067812, 0.0, 1.0, 0.0, 2.125, 
                             0.0, 0.0, 1.0, 0.5, 0.0, 0.0, 0.0, 1.0),
                
                // 第二個 block_display: chiseled_sandstone
                Arrays.asList(0.7071067812, 0.0, -0.7071067812, 0.2071067812, 0.0, 1.0, 0.0, 1.4375, 
                             0.7071067812, 0.0, 0.7071067812, 0.5, 0.0, 0.0, 0.0, 1.0),
                
                // 第三個 block_display: stripped_spruce_wood[axis=x]
                Arrays.asList(0.7071067812, 0.0, 0.7071067812, 0.0, 0.0, 1.0, 0.0, 0.0, 
                             -0.7071067812, 0.0, 0.7071067812, 0.0, 0.0, 0.0, 0.0, 1.0),
                
                // 第四個 block_display: acacia_wood[axis=x]
                Arrays.asList(0.7071067812, 0.0, 0.7071067812, 0.0, 0.0, 1.0, 0.0, 0.375, 
                             -0.7071067812, 0.0, 0.7071067812, 0.0, 0.0, 0.0, 0.0, 1.0),
                
                // item_display: player_head[display=none]
                Arrays.asList(1.0, 0.0, 0.0, -0.5625, 0.0, 1.0, 0.0, 2.25, 
                             0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0)
            );
            
            System.out.println("=== 詳細驗證結果 ===");
            
            // 逐一驗證每個實體
            for (int i = 0; i < Math.min(displayEntities.size(), expectedTransforms.size()); i++) {
                ProjectElement entity = displayEntities.get(i);
                List<Double> expected = expectedTransforms.get(i);
                
                System.out.printf("\n實體 %d: %s\n", i + 1, entity.getName());
                
                Matrix4f actualMatrix = entity.getWorldTransform();
                Matrix4f expectedMatrix = listToMatrix4f(expected);
                
                System.out.println("實際變換:");
                printMatrix(actualMatrix);
                
                System.out.println("預期變換:");
                printMatrix(expectedMatrix);
                
                double similarity = calculateSimilarity(actualMatrix, expectedMatrix);
                System.out.printf("總差異: %.8f\n", similarity);
                
                if (similarity < 0.0001) {
                    System.out.println("✅ 匹配成功!");
                } else {
                    System.out.println("❌ 不匹配 - 詳細差異:");
                    printDetailedDifferences(actualMatrix, expectedMatrix);
                }
            }
            
            // 總結
            System.out.println("\n=== 總結 ===");
            if (displayEntities.size() == expectedTransforms.size()) {
                System.out.printf("所有 %d 個實體都已驗證\n", displayEntities.size());
            } else {
                System.out.printf("實體數量不匹配：發現 %d 個，預期 %d 個\n", 
                    displayEntities.size(), expectedTransforms.size());
            }
            
        } catch (Exception e) {
            System.err.println("❌ 發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static List<ProjectElement> collectDisplayEntities(List<ProjectElement> elements) {
        List<ProjectElement> result = new ArrayList<>();
        for (ProjectElement element : elements) {
            collectDisplayEntitiesRecursive(element, result);
        }
        return result;
    }
    
    private static void collectDisplayEntitiesRecursive(ProjectElement element, List<ProjectElement> result) {
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
    
    private static Matrix4f listToMatrix4f(List<Double> transformList) {
        return new Matrix4f(
            transformList.get(0).floatValue(), transformList.get(1).floatValue(), 
            transformList.get(2).floatValue(), transformList.get(3).floatValue(),
            transformList.get(4).floatValue(), transformList.get(5).floatValue(), 
            transformList.get(6).floatValue(), transformList.get(7).floatValue(),
            transformList.get(8).floatValue(), transformList.get(9).floatValue(), 
            transformList.get(10).floatValue(), transformList.get(11).floatValue(),
            transformList.get(12).floatValue(), transformList.get(13).floatValue(), 
            transformList.get(14).floatValue(), transformList.get(15).floatValue()
        );
    }
    
    private static void printMatrix(Matrix4f matrix) {
        System.out.printf("[%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f,%.10f]\n",
            matrix.m00(), matrix.m01(), matrix.m02(), matrix.m03(),
            matrix.m10(), matrix.m11(), matrix.m12(), matrix.m13(),
            matrix.m20(), matrix.m21(), matrix.m22(), matrix.m23(),
            matrix.m30(), matrix.m31(), matrix.m32(), matrix.m33());
    }
    
    private static double calculateSimilarity(Matrix4f actual, Matrix4f expected) {
        double totalDiff = 0;
        for (int i = 0; i < 16; i++) {
            double diff = Math.abs(getMatrixElement(actual, i) - getMatrixElement(expected, i));
            totalDiff += diff;
        }
        return totalDiff;
    }
    
    private static void printDetailedDifferences(Matrix4f actual, Matrix4f expected) {
        for (int i = 0; i < 16; i++) {
            float actualVal = getMatrixElement(actual, i);
            float expectedVal = getMatrixElement(expected, i);
            float diff = Math.abs(actualVal - expectedVal);
            
            if (diff > 0.0001) {
                int row = i / 4;
                int col = i % 4;
                System.out.printf("  [%d,%d]: actual=%.8f, expected=%.8f, diff=%.8f\n", 
                    row, col, actualVal, expectedVal, diff);
            }
        }
    }
    
    private static float getMatrixElement(Matrix4f matrix, int index) {
        return switch(index) {
            case 0 -> matrix.m00(); case 1 -> matrix.m01(); 
            case 2 -> matrix.m02(); case 3 -> matrix.m03();
            case 4 -> matrix.m10(); case 5 -> matrix.m11(); 
            case 6 -> matrix.m12(); case 7 -> matrix.m13();
            case 8 -> matrix.m20(); case 9 -> matrix.m21(); 
            case 10 -> matrix.m22(); case 11 -> matrix.m23();
            case 12 -> matrix.m30(); case 13 -> matrix.m31(); 
            case 14 -> matrix.m32(); case 15 -> matrix.m33();
            default -> 0f;
        };
    }
}
