import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;
import org.joml.Matrix4f;

import java.util.List;
import java.util.ArrayList;

public class BDEngineFileTransformTest {
    
    // 遞迴收集所有顯示實體的方法
    private static void collectDisplayEntitiesRecursive(ProjectElement element, List<ProjectElement> result) {
        if (element.getIsItemDisplay() != null && element.getIsItemDisplay()) {
            result.add(element);
        }
        
        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                collectDisplayEntitiesRecursive(child, result);
            }
        }
    }
    
    // 從專案元素列表中收集所有顯示實體
    private static List<ProjectElement> collectDisplayEntities(List<ProjectElement> projectElements) {
        List<ProjectElement> displayEntities = new ArrayList<>();
        for (ProjectElement element : projectElements) {
            collectDisplayEntitiesRecursive(element, displayEntities);
        }
        return displayEntities;
    }
    
    public static void main(String[] args) {
        System.out.println("=== 驗證 BDEngine 檔案的變換計算一致性 ===\n");
        
        BDEngineParser parser = new BDEngineParser();
        
        try {
            // 解析 .bdengine 檔案
            System.out.println("正在解析 twme-skin.bdengine...");
            List<ProjectElement> elementsFromBDEngine = parser.parseBDEngineFile("twme-skin.bdengine");
            List<ProjectElement> displayEntitiesFromBDEngine = collectDisplayEntities(elementsFromBDEngine);
            System.out.println("從 .bdengine 檔案找到 " + displayEntitiesFromBDEngine.size() + " 個顯示實體");
            
            // 解析對應的 JSON 檔案
            System.out.println("正在解析 twme-skin.json...");
            List<ProjectElement> elementsFromJson = parser.parseFromFile("twme-skin.json");
            List<ProjectElement> displayEntitiesFromJson = collectDisplayEntities(elementsFromJson);
            System.out.println("從 JSON 檔案找到 " + displayEntitiesFromJson.size() + " 個顯示實體");
            
            if (displayEntitiesFromBDEngine.size() != displayEntitiesFromJson.size()) {
                System.out.println("❌ 顯示實體數量不匹配！");
                return;
            }
            
            System.out.println("\n=== 變換矩陣比較 ===\n");
            
            boolean allMatched = true;
            int entityCount = displayEntitiesFromBDEngine.size();
            
            for (int i = 0; i < entityCount; i++) {
                ProjectElement bdEngineEntity = displayEntitiesFromBDEngine.get(i);
                ProjectElement jsonEntity = displayEntitiesFromJson.get(i);
                
                Matrix4f bdEngineTransform = bdEngineEntity.getWorldTransform();
                Matrix4f jsonTransform = jsonEntity.getWorldTransform();
                
                if (bdEngineTransform == null || jsonTransform == null) {
                    System.out.println("實體 " + (i + 1) + ": ❌ 變換矩陣為 null");
                    allMatched = false;
                    continue;
                }
                
                // 轉換為陣列以便比較
                float[] bdEngineArray = new float[16];
                float[] jsonArray = new float[16];
                bdEngineTransform.get(bdEngineArray);
                jsonTransform.get(jsonArray);
                
                // 計算差異
                double totalDiff = 0.0;
                for (int j = 0; j < 16; j++) {
                    double diff = Math.abs(bdEngineArray[j] - jsonArray[j]);
                    totalDiff += diff;
                }
                
                System.out.print("實體 " + (i + 1) + " (" + bdEngineEntity.getName() + "): ");
                System.out.printf("總差異: %.8f", totalDiff);
                
                boolean matches = totalDiff < 0.0001; // 允許小的浮點數誤差
                if (matches) {
                    System.out.println(" ✅");
                } else {
                    System.out.println(" ❌");
                    allMatched = false;
                    
                    // 顯示詳細差異資訊
                    System.out.println("  BDEngine 變換:");
                    printMatrix(bdEngineArray);
                    System.out.println("  JSON 變換:");
                    printMatrix(jsonArray);
                }
            }
            
            System.out.println("\n=== 總結 ===");
            if (allMatched) {
                System.out.println("🎉 所有 " + entityCount + " 個實體的變換矩陣完全一致！");
                System.out.println("✅ BDEngine 檔案格式支援功能完全正常！");
                System.out.println("✅ 變換計算在兩種檔案格式中保持一致！");
            } else {
                System.out.println("❌ 部分實體的變換矩陣不一致，請檢查實作。");
            }
            
        } catch (BDEngineParsingException e) {
            System.err.println("❌ 解析錯誤: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("原因: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ 未知錯誤: " + e.getMessage());
        }
    }
    
    private static void printMatrix(float[] matrix) {
        System.out.print("    [");
        for (int i = 0; i < 16; i++) {
            System.out.printf("%.6f", matrix[i]);
            if (i < 15) System.out.print(", ");
        }
        System.out.println("]");
    }
}
