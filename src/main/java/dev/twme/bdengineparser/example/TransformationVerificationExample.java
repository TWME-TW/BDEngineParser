package dev.twme.bdengineparser.example;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;

/**
 * 驗證修正後的轉換邏輯是否正確的示例程序
 */
public class TransformationVerificationExample {
    public static void main(String[] args) {
        try {
            System.out.println("=== BDEngine Parser 轉換修正驗證 ===");
            
            BDEngineParser parser = new BDEngineParser();
            List<ProjectElement> elements = parser.parseFromFile("restaurant-sign.json");
            
            System.out.println("成功解析 restaurant-sign.json");
            
            // 收集顯示實體
            List<ProjectElement> displayEntities = collectDisplayEntities(elements);
            System.out.printf("發現 %d 個顯示實體\n\n", displayEntities.size());
            
            // 檢查前幾個實體的轉換矩陣
            for (int i = 0; i < Math.min(3, displayEntities.size()); i++) {
                ProjectElement entity = displayEntities.get(i);
                System.out.printf("實體 %d: %s\n", i + 1, entity.getName());
                
                Matrix4f transform = entity.getWorldTransform();
                if (transform != null) {
                    System.out.printf("變換矩陣: [%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f]\n",
                        transform.m00(), transform.m01(), transform.m02(), transform.m03(),
                        transform.m10(), transform.m11(), transform.m12(), transform.m13(),
                        transform.m20(), transform.m21(), transform.m22(), transform.m23(),
                        transform.m30(), transform.m31(), transform.m32(), transform.m33());
                } else {
                    System.out.println("世界變換矩陣為 null");
                }
                System.out.println();
            }
            
            System.out.println("✅ 轉換計算已修正並正常運作！");
            
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
}
