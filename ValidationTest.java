import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Arrays;

public class ValidationTest {
    public static void main(String[] args) {
        try {
            BDEngineParser parser = new BDEngineParser();
            List<ProjectElement> elements = parser.parseFromFile("restaurant-sign.json");
            
            System.out.println("=== 驗證所有實體的轉換矩陣 ===");
            
            // 預期的轉換矩陣（從 restaurant-sign.txt 中提取）
            List<List<Double>> expectedTransforms = Arrays.asList(
                // 第一個 item_display
                Arrays.asList(1.0, 0.0, 0.0, 0.0, 0.0, 0.9658446719, 0.965442044, 0.8008294014, 
                             0.0, 0.257992044, 0.2594946719, 0.1496755161, 0.0, 0.0, 0.0, 1.0),
                
                // 第一個 block_display (bell)  
                Arrays.asList(0.0, -1.0, 0.0, 0.5625, 0.9659258263, 0.0, -0.2588190451, 0.1455857129, 
                             0.2588190451, 0.0, 0.9659258263, -0.5433332773, 0.0, 0.0, 0.0, 1.0),
                             
                // 第二個 block_display (bell)
                Arrays.asList(0.0, -1.0, 0.0, 1.1875, 0.9659258263, 0.0, -0.2588190451, 0.1455857129, 
                             0.2588190451, 0.0, 0.9659258263, -0.5433332773, 0.0, 0.0, 0.0, 1.0),
                             
                // 第三個 block_display (bell)
                Arrays.asList(0.7575, 0.0, 0.0, -0.37875, 0.0, 0.7071067812, -0.7071067812, 0.733113164, 
                             0.0, 0.7071067812, 0.7071067812, -0.7458051597, 0.0, 0.0, 0.0, 1.0),
                             
                // 第四個 block_display (bell)
                Arrays.asList(0.0, 0.0, 0.9975, -0.18640625, 0.9659258263, 0.2588190451, 0.0, -0.2102904741, 
                             -0.2588190451, 0.9659258263, 0.0, -0.3318814049, 0.0, 0.0, 0.0, 1.0),
                             
                // 第五個 block_display (bell)
                Arrays.asList(0.0, 0.0, 0.9975, -0.81109375, 0.9659258263, 0.2588190451, 0.0, -0.2102904741, 
                             -0.2588190451, 0.9659258263, 0.0, -0.3318814049, 0.0, 0.0, 0.0, 1.0),
                             
                // 第二個 item_display
                Arrays.asList(-1.0, 0.0, 0.0, 0.0, 0.0, 0.9658446719, 0.965442044, 0.8008294014, 
                             0.0, -0.257992044, -0.2594946719, 0.3032578128, 0.0, 0.0, 0.0, 1.0)
            );
            
            // 收集實際的實體
            List<ProjectElement> actualEntities = collectDisplayEntities(elements);
            
            System.out.printf("發現 %d 個顯示實體\n", actualEntities.size());
            
            // 逐一驗證
            for (int i = 0; i < Math.min(actualEntities.size(), expectedTransforms.size()); i++) {
                ProjectElement entity = actualEntities.get(i);
                List<Double> expected = expectedTransforms.get(i);
                
                System.out.printf("\n=== 實體 %d: %s ===\n", i + 1, entity.getName());
                
                Matrix4f actualMatrix = entity.getWorldTransform();
                Matrix4f expectedMatrix = listToMatrix4f(expected);
                
                System.out.println("實際變換:");
                printMatrix(actualMatrix);
                
                System.out.println("預期變換:");
                printMatrix(expectedMatrix);
                
                double similarity = calculateSimilarity(actualMatrix, expectedMatrix);
                System.out.printf("相似度: %.6f (0.0 = 完全匹配)\n", similarity);
                
                if (similarity < 0.001) {
                    System.out.println("✅ 匹配成功!");
                } else {
                    System.out.println("❌ 不匹配");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static List<ProjectElement> collectDisplayEntities(List<ProjectElement> elements) {
        java.util.List<ProjectElement> result = new java.util.ArrayList<>();
        for (ProjectElement element : elements) {
            collectDisplayEntitiesRecursive(element, result);
        }
        return result;
    }
    
    private static void collectDisplayEntitiesRecursive(ProjectElement element, List<ProjectElement> result) {
        // 如果是顯示實體（item_display 或 block_display）
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
        System.out.printf("[%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f]\n",
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
