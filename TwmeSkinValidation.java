import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.twme.bdengineparser.internal.WorldTransformCalculator;
import dev.twme.bdengineparser.model.ProjectElement;

public class TwmeSkinValidation {
    
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
    // 來自 twme-skin.txt 的預期變換矩陣
    private static final float[][] expectedTransforms = {
        // Head (玩家頭顱)
        {0.937f, 0f, 0f, 0f, 0f, 0.9050724992f, 0.2425134453f, 1.5460362496f, 0f, -0.2425134453f, 0.9050724992f, -0.1381317226f, 0f, 0f, 0f, 1f},
        
        // Body - 第1個組件 (主體下部)
        {0.937f, 0f, 0f, -1.1e-9f, 0f, 0.4685f, 0f, 1.0935f, 0f, 0f, 0.4685f, -0.016875f, 0f, 0f, 0f, 1f},
        
        // Body - 第2個組件 (主體上部)
        {0.937f, 0f, 0f, -1.1e-9f, 0f, 0.937f, 0f, 0.85925f, 0f, 0f, 0.4685f, -0.016875f, 0f, 0f, 0f, 1f},
        
        // Left leg - 第1個組件
        {0.4525362496f, 0.117125f, 0.0313835492f, -0.1100878626f, 0f, 0.1212567226f, -0.4525362496f, 0.36876446f, -0.1212567226f, 0.4371164508f, 0.117125f, 0.0104421959f, 0f, 0f, 0f, 1f},
        
        // Left leg - 第2個組件
        {0.4525362496f, 0.23425f, 0.0313835492f, -0.1686503626f, 0f, 0.2425134453f, -0.4525362496f, 0.3081360987f, -0.1212567226f, 0.8742329017f, 0.117125f, -0.2081160296f, 0f, 0f, 0f, 1f},
        
        // Right leg - 第1個組件
        {0.4525362496f, -0.117125f, -0.0313835492f, 0.1094822829f, 0f, 0.1212567226f, -0.4525362496f, 0.36876446f, 0.1212567226f, 0.4371164508f, 0.117125f, 0.0105937668f, 0f, 0f, 0f, 1f},
        
        // Right leg - 第2個組件
        {0.4525362496f, -0.23425f, -0.0313835492f, 0.1680447829f, 0f, 0.2425134453f, -0.4525362496f, 0.3081360987f, 0.1212567226f, 0.8742329017f, 0.117125f, -0.2079644587f, 0f, 0f, 0f, 1f},
        
        // Right hand - 第1個組件 (上臂)
        {0.2028664508f, -0.1656397635f, -0.1656397635f, 0.2551918517f, 0f, 0.331279527f, -0.331279527f, 1.000425332f, 0.117125f, 0.2868964861f, 0.2868964861f, 0.041046635f, 0f, 0f, 0f, 1f},
        
        // Right hand - 第2個組件 (前臂窄部分)
        {0.1014332254f, -0.1656397635f, -0.1656397635f, 0.3302524385f, 0f, 0.331279527f, -0.331279527f, 1.000425332f, 0.0585625f, 0.2868964861f, 0.2868964861f, 0.084382885f, 0f, 0f, 0f, 1f},
        
        // Right hand - 第3個組件 (主要手臂)
        {0.2028664508f, -0.331279527f, -0.1656397635f, 0.3380117335f, 0f, 0.662559054f, -0.331279527f, 0.8347855685f, 0.117125f, 0.5737929722f, 0.2868964861f, -0.1024016081f, 0f, 0f, 0f, 1f},
        
        // Right hand - 第4個組件 (前臂主體)
        {0.1014332254f, -0.331279527f, -0.1656397635f, 0.4130723203f, 0f, 0.662559054f, -0.331279527f, 0.8347855685f, 0.0585625f, 0.5737929722f, 0.2868964861f, -0.0590653581f, 0f, 0f, 0f, 1f},
        
        // Left hand - 第1個組件 (上臂)
        {0.2028664508f, 0.1656397635f, 0.1656397635f, -0.2549232055f, 0f, 0.331279527f, -0.331279527f, 1.000425332f, -0.117125f, 0.2868964861f, 0.2868964861f, 0.0412296428f, 0f, 0f, 0f, 1f},
        
        // Left hand - 第2個組件 (前臂窄部分)
        {0.1014332254f, 0.1656397635f, 0.1656397635f, -0.3299837923f, 0f, 0.331279527f, -0.331279527f, 1.000425332f, -0.0585625f, 0.2868964861f, 0.2868964861f, 0.0845658928f, 0f, 0f, 0f, 1f},
        
        // Left hand - 第3個組件 (主要手臂)
        {0.2028664508f, 0.331279527f, 0.1656397635f, -0.3377430873f, 0f, 0.662559054f, -0.331279527f, 0.8347855685f, -0.117125f, 0.5737929722f, 0.2868964861f, -0.1022186003f, 0f, 0f, 0f, 1f},
        
        // Left hand - 第4個組件 (前臂主體)
        {0.1014332254f, 0.331279527f, 0.1656397635f, -0.4128036741f, 0f, 0.662559054f, -0.331279527f, 0.8347855685f, -0.0585625f, 0.5737929722f, 0.2868964861f, -0.0588823503f, 0f, 0f, 0f, 1f}
    };

    // 每個預期實體的名稱，用於識別
    private static final String[] expectedNames = {
        "player_head[display=none] - Head",
        "player_head[display=none] - Body lower",
        "player_head[display=none] - Body upper", 
        "player_head[display=none] - Left leg upper",
        "player_head[display=none] - Left leg lower",
        "player_head[display=none] - Right leg upper",
        "player_head[display=none] - Right leg lower",
        "player_head[display=none] - Right hand upper arm",
        "player_head[display=none] - Right hand forearm narrow",
        "player_head[display=none] - Right hand main",
        "player_head[display=none] - Right hand forearm main",
        "player_head[display=none] - Left hand upper arm",
        "player_head[display=none] - Left hand forearm narrow",
        "player_head[display=none] - Left hand main",
        "player_head[display=none] - Left hand forearm main"
    };

    public static void main(String[] args) {
        System.out.println("=== 驗證 twme-skin.json 的轉換計算 ===");
        
        try {
            // 解析 BDEngine 專案檔案
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ProjectElement>>(){}.getType();
            List<ProjectElement> projectElements = gson.fromJson(
                new FileReader("twme-skin.json"), listType
            );
            
            System.out.println("成功解析 twme-skin.json");
            
            // 找出所有顯示實體
            List<ProjectElement> displayEntities = collectDisplayEntities(projectElements);
            System.out.println("發現 " + displayEntities.size() + " 個顯示實體");
            
            if (displayEntities.size() != expectedTransforms.length) {
                System.out.println("⚠️ 警告：預期 " + expectedTransforms.length + " 個實體，但找到 " + displayEntities.size() + " 個");
            }
            
            System.out.println("\n=== 詳細驗證結果 ===\n");
            
            boolean allMatched = true;
            
            for (int i = 0; i < Math.min(displayEntities.size(), expectedTransforms.length); i++) {
                ProjectElement entity = displayEntities.get(i);
                float[] expected = expectedTransforms[i];
                String name = (i < expectedNames.length) ? expectedNames[i] : entity.getName();
                
                // 計算世界變換
                WorldTransformCalculator calculator = new WorldTransformCalculator();
                calculator.calculateWorldTransforms(projectElements);
                Matrix4f worldTransform = entity.getWorldTransform();
                
                // 轉換為陣列以便比較
                float[] actual = new float[16];
                worldTransform.get(actual);
                
                System.out.println("實體 " + (i + 1) + ": " + name);
                System.out.print("實際變換:\n[");
                for (int j = 0; j < 16; j++) {
                    System.out.printf("%.10f", actual[j]);
                    if (j < 15) System.out.print(",");
                }
                System.out.println("]");
                
                System.out.print("預期變換:\n[");
                for (int j = 0; j < 16; j++) {
                    System.out.printf("%.10f", expected[j]);
                    if (j < 15) System.out.print(",");
                }
                System.out.println("]");
                
                // 計算差異
                double totalDiff = 0.0;
                for (int j = 0; j < 16; j++) {
                    double diff = Math.abs(actual[j] - expected[j]);
                    totalDiff += diff;
                }
                
                System.out.printf("總差異: %.8f\n", totalDiff);
                
                boolean matches = totalDiff < 0.0001; // 允許小的浮點數誤差
                if (matches) {
                    System.out.println("✅ 匹配成功!");
                } else {
                    System.out.println("❌ 匹配失敗!");
                    allMatched = false;
                }
                System.out.println();
            }
            
            System.out.println("=== 總結 ===");
            if (allMatched && displayEntities.size() == expectedTransforms.length) {
                System.out.println("所有 " + displayEntities.size() + " 個實體都已驗證");
            } else if (allMatched) {
                System.out.println("所有測試的實體都匹配，但數量不符");
            } else {
                System.out.println("部分實體驗證失敗");
            }
            
        } catch (IOException e) {
            System.err.println("無法讀取檔案: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("運行時錯誤: " + e.getMessage());
        }
    }
}
