import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;

import java.util.List;

/**
 * 完整的 API 功能演示程式
 * 展示 BDEngineParser 的所有主要功能，包括新的 .bdengine 檔案支援
 */
public class ComprehensiveAPIDemo {
    public static void main(String[] args) {
        System.out.println("=== BDEngineParser 完整 API 功能演示 ===\n");
        
        BDEngineParser parser = new BDEngineParser();
        
        // 1. 測試 JSON 檔案解析
        testJsonParsing(parser);
        
        // 2. 測試 BDEngine 檔案解析
        testBDEngineParsing(parser);
        
        // 3. 比較兩種格式的結果
        compareFormats(parser);
        
        System.out.println("\n=== API 演示完成 ===");
    }
    
    private static void testJsonParsing(BDEngineParser parser) {
        System.out.println("--- 1. JSON 檔案解析測試 ---");
        try {
            // 原始解析（不計算變換）
            List<ProjectElement> rawElements = parser.parseFromFileRaw("twme-skin.json");
            System.out.println("✅ JSON 原始解析成功，根元素: " + rawElements.size());
            
            // 完整解析（包含變換計算）
            List<ProjectElement> fullElements = parser.parseFromFile("twme-skin.json");
            System.out.println("✅ JSON 完整解析成功，根元素: " + fullElements.size());
            
            // 檢查變換計算
            long transformedCount = countTransformedElements(fullElements);
            System.out.println("✅ 已計算變換的元素數量: " + transformedCount);
            
        } catch (BDEngineParsingException e) {
            System.err.println("❌ JSON 解析失敗: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testBDEngineParsing(BDEngineParser parser) {
        System.out.println("--- 2. BDEngine 檔案解析測試 ---");
        try {
            // 原始解析（不計算變換）
            List<ProjectElement> rawElements = parser.parseBDEngineFileRaw("twme-skin.bdengine");
            System.out.println("✅ BDEngine 原始解析成功，根元素: " + rawElements.size());
            
            // 完整解析（包含變換計算）
            List<ProjectElement> fullElements = parser.parseBDEngineFile("twme-skin.bdengine");
            System.out.println("✅ BDEngine 完整解析成功，根元素: " + fullElements.size());
            
            // 檢查變換計算
            long transformedCount = countTransformedElements(fullElements);
            System.out.println("✅ 已計算變換的元素數量: " + transformedCount);
            
        } catch (BDEngineParsingException e) {
            System.err.println("❌ BDEngine 解析失敗: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void compareFormats(BDEngineParser parser) {
        System.out.println("--- 3. 格式比較測試 ---");
        try {
            List<ProjectElement> jsonElements = parser.parseFromFile("twme-skin.json");
            List<ProjectElement> bdengineElements = parser.parseBDEngineFile("twme-skin.bdengine");
            
            // 基本統計比較
            int jsonTotal = parser.getTotalElementCount(jsonElements);
            int bdengineTotal = parser.getTotalElementCount(bdengineElements);
            
            System.out.println("總元素數量比較:");
            System.out.println("  JSON: " + jsonTotal);
            System.out.println("  BDEngine: " + bdengineTotal);
            System.out.println("  " + (jsonTotal == bdengineTotal ? "✅ 一致" : "❌ 不一致"));
            
            // 顯示實體數量比較
            int jsonDisplayEntities = countDisplayEntities(jsonElements);
            int bdengineDisplayEntities = countDisplayEntities(bdengineElements);
            
            System.out.println("顯示實體數量比較:");
            System.out.println("  JSON: " + jsonDisplayEntities);
            System.out.println("  BDEngine: " + bdengineDisplayEntities);
            System.out.println("  " + (jsonDisplayEntities == bdengineDisplayEntities ? "✅ 一致" : "❌ 不一致"));
            
            // 變換計算數量比較
            long jsonTransformed = countTransformedElements(jsonElements);
            long bdengineTransformed = countTransformedElements(bdengineElements);
            
            System.out.println("已變換元素數量比較:");
            System.out.println("  JSON: " + jsonTransformed);
            System.out.println("  BDEngine: " + bdengineTransformed);
            System.out.println("  " + (jsonTransformed == bdengineTransformed ? "✅ 一致" : "❌ 不一致"));
            
        } catch (BDEngineParsingException e) {
            System.err.println("❌ 比較測試失敗: " + e.getMessage());
        }
    }
    
    // 輔助方法：計算已變換的元素數量
    private static long countTransformedElements(List<ProjectElement> elements) {
        return elements.stream()
                .mapToLong(ComprehensiveAPIDemo::countTransformedElementsRecursive)
                .sum();
    }
    
    private static long countTransformedElementsRecursive(ProjectElement element) {
        long count = (element.getWorldTransform() != null) ? 1 : 0;
        if (element.getChildren() != null) {
            count += element.getChildren().stream()
                    .mapToLong(ComprehensiveAPIDemo::countTransformedElementsRecursive)
                    .sum();
        }
        return count;
    }
    
    // 輔助方法：計算顯示實體數量
    private static int countDisplayEntities(List<ProjectElement> elements) {
        return elements.stream()
                .mapToInt(ComprehensiveAPIDemo::countDisplayEntitiesRecursive)
                .sum();
    }
    
    private static int countDisplayEntitiesRecursive(ProjectElement element) {
        int count = 0;
        
        if (Boolean.TRUE.equals(element.getIsItemDisplay()) ||
            Boolean.TRUE.equals(element.getIsBlockDisplay()) ||
            Boolean.TRUE.equals(element.getIsTextDisplay())) {
            count = 1;
        }
        
        if (element.getChildren() != null) {
            count += element.getChildren().stream()
                    .mapToInt(ComprehensiveAPIDemo::countDisplayEntitiesRecursive)
                    .sum();
        }
        
        return count;
    }
}
