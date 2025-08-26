# BDEngine 檔案格式支援

BDEngineParser v2.2.1 新增了對 `.bdengine` 檔案格式的支援。

## 檔案格式說明

`.bdengine` 檔案包含經過以下處理的 BDEngine 專案資料：

1. 原始 JSON 資料
2. 使用 gzip 壓縮
3. 使用 base64 編碼

## 新增的 API 方法

### 原始解析方法（不計算變換）

#### `parseBDEngineFileRaw(String filePath)`
從檔案路徑解析 `.bdengine` 檔案，不計算世界變換。

```java
BDEngineParser parser = new BDEngineParser();
List<ProjectElement> elements = parser.parseBDEngineFileRaw("path/to/file.bdengine");
```

#### `parseBDEngineStringRaw(String base64String)`
從 base64 字符串解析 BDEngine 格式資料，不計算世界變換。

```java
String base64Data = "H4sI..."; // base64 編碼的 gzip 壓縮資料
List<ProjectElement> elements = parser.parseBDEngineStringRaw(base64Data);
```

#### `parseBDEngineInputStreamRaw(InputStream inputStream)`
從 InputStream 解析 `.bdengine` 資料，不計算世界變換。

```java
try (InputStream is = Files.newInputStream(path)) {
    List<ProjectElement> elements = parser.parseBDEngineInputStreamRaw(is);
}
```

### 完整解析方法（包含變換計算）

#### `parseBDEngineFile(String filePath)`
從檔案路徑解析 `.bdengine` 檔案，**並自動計算所有世界變換**。

```java
BDEngineParser parser = new BDEngineParser();
List<ProjectElement> elements = parser.parseBDEngineFile("path/to/file.bdengine");

// 所有元素的世界變換已自動計算並存儲在 getWorldTransform() 中
for (ProjectElement element : elements) {
    Matrix4f worldTransform = element.getWorldTransform();
    // 使用變換矩陣...
}
```

#### `parseBDEngineString(String base64String)`
從 base64 字符串解析 BDEngine 格式資料，**並自動計算所有世界變換**。

```java
String base64Data = "H4sI...";
List<ProjectElement> elements = parser.parseBDEngineString(base64Data);
```

#### `parseBDEngineInputStream(InputStream inputStream)`
從 InputStream 解析 `.bdengine` 資料，**並自動計算所有世界變換**。

```java
try (InputStream is = Files.newInputStream(path)) {
    List<ProjectElement> elements = parser.parseBDEngineInputStream(is);
}
```

## 使用範例

### 基本使用

```java
import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import dev.twme.bdengineparser.exception.BDEngineParsingException;
import org.joml.Matrix4f;

public class Example {
    public static void main(String[] args) {
        BDEngineParser parser = new BDEngineParser();
        
        try {
            // 解析 .bdengine 檔案（包含變換計算）
            List<ProjectElement> elements = parser.parseBDEngineFile("project.bdengine");
            
            System.out.println("解析了 " + elements.size() + " 個根元素");
            System.out.println("總元素數量: " + parser.getTotalElementCount(elements));
            
            // 遍歷所有元素並檢查變換
            traverseElements(elements);
            
        } catch (BDEngineParsingException e) {
            System.err.println("解析錯誤: " + e.getMessage());
        }
    }
    
    private static void traverseElements(List<ProjectElement> elements) {
        for (ProjectElement element : elements) {
            System.out.println("元素: " + element.getName());
            
            // 檢查世界變換
            Matrix4f worldTransform = element.getWorldTransform();
            if (worldTransform != null) {
                System.out.println("  世界變換已計算");
            }
            
            // 檢查是否為顯示實體
            if (Boolean.TRUE.equals(element.getIsItemDisplay())) {
                System.out.println("  -> 物品顯示實體");
            } else if (Boolean.TRUE.equals(element.getIsBlockDisplay())) {
                System.out.println("  -> 方塊顯示實體");
            } else if (Boolean.TRUE.equals(element.getIsTextDisplay())) {
                System.out.println("  -> 文字顯示實體");
            }
            
            // 遞歸處理子元素
            if (element.getChildren() != null) {
                traverseElements(element.getChildren());
            }
        }
    }
}
```

### 錯誤處理

```java
try {
    List<ProjectElement> elements = parser.parseBDEngineFile("project.bdengine");
    // 處理元素...
} catch (BDEngineParsingException e) {
    // 處理 BDEngine 特定錯誤
    System.err.println("BDEngine 解析錯誤: " + e.getMessage());
    
    // 檢查原因
    if (e.getCause() != null) {
        System.err.println("原因: " + e.getCause().getMessage());
    }
} catch (IllegalArgumentException e) {
    // 處理參數錯誤
    System.err.println("參數錯誤: " + e.getMessage());
}
```

## 效能和記憶體考量

- `.bdengine` 檔案通常比對應的 JSON 檔案小 60-80%
- 解壓縮過程會暫時使用額外記憶體
- 對於大型專案，建議使用 InputStream 方法以控制記憶體使用

## 與現有 API 的相容性

新的 BDEngine 檔案支援完全相容於現有的 JSON 解析功能：

- 相同的 `ProjectElement` 模型
- 相同的變換計算邏輯
- 相同的錯誤處理機制
- 相同的世界變換結果

這意味著您可以無縫地在 JSON 和 BDEngine 檔案格式之間切換，而不需要修改處理 `ProjectElement` 的程式碼。
