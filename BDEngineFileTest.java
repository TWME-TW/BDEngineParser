import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;

import java.util.List;

public class BDEngineFileTest {
    public static void main(String[] args) {
        System.out.println("=== 測試 BDEngine 檔案支援功能 ===\n");
        
        BDEngineParser parser = new BDEngineParser();
        
        try {
            // 測試解析 .bdengine 檔案
            System.out.println("正在解析 twme-skin.bdengine...");
            List<ProjectElement> elementsFromBDEngine = parser.parseBDEngineFile("twme-skin.bdengine");
            System.out.println("✅ 成功解析 .bdengine 檔案！");
            System.out.println("找到根元素數量: " + elementsFromBDEngine.size());
            
            // 計算總元素數量
            int totalElementsFromBDEngine = parser.getTotalElementCount(elementsFromBDEngine);
            System.out.println("總元素數量 (包含子元素): " + totalElementsFromBDEngine);
            
            // 收集顯示實體
            List<ProjectElement> displayEntitiesFromBDEngine = collectDisplayEntities(elementsFromBDEngine);
            System.out.println("顯示實體數量: " + displayEntitiesFromBDEngine.size());
            
            System.out.println("\n--- 與 JSON 檔案比較 ---");
            
            // 比較結果：解析對應的 JSON 檔案
            System.out.println("正在解析對應的 JSON 檔案 twme-skin.json...");
            List<ProjectElement> elementsFromJson = parser.parseFromFile("twme-skin.json");
            System.out.println("✅ 成功解析 JSON 檔案！");
            
            int totalElementsFromJson = parser.getTotalElementCount(elementsFromJson);
            List<ProjectElement> displayEntitiesFromJson = collectDisplayEntities(elementsFromJson);
            
            // 比較結果
            System.out.println("\n=== 比較結果 ===");
            System.out.println("根元素數量 - BDEngine: " + elementsFromBDEngine.size() + 
                             ", JSON: " + elementsFromJson.size() + 
                             (elementsFromBDEngine.size() == elementsFromJson.size() ? " ✅" : " ❌"));
                             
            System.out.println("總元素數量 - BDEngine: " + totalElementsFromBDEngine + 
                             ", JSON: " + totalElementsFromJson + 
                             (totalElementsFromBDEngine == totalElementsFromJson ? " ✅" : " ❌"));
                             
            System.out.println("顯示實體數量 - BDEngine: " + displayEntitiesFromBDEngine.size() + 
                             ", JSON: " + displayEntitiesFromJson.size() + 
                             (displayEntitiesFromBDEngine.size() == displayEntitiesFromJson.size() ? " ✅" : " ❌"));
            
            // 檢查第一個根元素的基本屬性
            if (!elementsFromBDEngine.isEmpty() && !elementsFromJson.isEmpty()) {
                ProjectElement bdEngineRoot = elementsFromBDEngine.get(0);
                ProjectElement jsonRoot = elementsFromJson.get(0);
                
                System.out.println("\n--- 第一個根元素比較 ---");
                System.out.println("名稱 - BDEngine: '" + bdEngineRoot.getName() + 
                                 "', JSON: '" + jsonRoot.getName() + "'" + 
                                 (bdEngineRoot.getName().equals(jsonRoot.getName()) ? " ✅" : " ❌"));
                                 
                boolean sameCollectionStatus = (bdEngineRoot.getIsCollection() == null && jsonRoot.getIsCollection() == null) ||
                                             (bdEngineRoot.getIsCollection() != null && bdEngineRoot.getIsCollection().equals(jsonRoot.getIsCollection()));
                System.out.println("集合狀態 - BDEngine: " + bdEngineRoot.getIsCollection() + 
                                 ", JSON: " + jsonRoot.getIsCollection() + 
                                 (sameCollectionStatus ? " ✅" : " ❌"));
            }
            
            System.out.println("\n=== 測試完成 ===");
            if (elementsFromBDEngine.size() == elementsFromJson.size() && 
                totalElementsFromBDEngine == totalElementsFromJson &&
                displayEntitiesFromBDEngine.size() == displayEntitiesFromJson.size()) {
                System.out.println("🎉 所有測試通過！BDEngine 檔案支援功能正常運作！");
            } else {
                System.out.println("⚠️ 部分測試失敗，請檢查實作細節。");
            }
            
        } catch (BDEngineParsingException e) {
            System.err.println("❌ 解析錯誤: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("原因: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ 未知錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 遞迴收集所有顯示實體的輔助方法
    private static void collectDisplayEntitiesRecursive(ProjectElement element, List<ProjectElement> result) {
        if (element.getIsItemDisplay() != null && element.getIsItemDisplay()) {
            result.add(element);
        }
        if (element.getIsBlockDisplay() != null && element.getIsBlockDisplay()) {
            result.add(element);
        }
        if (element.getIsTextDisplay() != null && element.getIsTextDisplay()) {
            result.add(element);
        }
        
        if (element.getChildren() != null) {
            for (ProjectElement child : element.getChildren()) {
                collectDisplayEntitiesRecursive(child, result);
            }
        }
    }
    
    private static List<ProjectElement> collectDisplayEntities(List<ProjectElement> projectElements) {
        java.util.List<ProjectElement> displayEntities = new java.util.ArrayList<>();
        for (ProjectElement element : projectElements) {
            collectDisplayEntitiesRecursive(element, displayEntities);
        }
        return displayEntities;
    }
}
