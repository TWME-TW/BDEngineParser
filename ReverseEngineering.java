import org.joml.Matrix4f;
import java.util.Arrays;
import java.util.List;

public class ReverseEngineering {
    public static void main(String[] args) {
        System.out.println("=== 從 Minecraft 指令反推邏輯 ===");
        
        // 第一個 item_display 的預期變換矩陣
        List<Double> expected1 = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.9658446719, 0.965442044, 0.8008294014,
            0.0, 0.257992044, 0.2594946719, 0.1496755161,
            0.0, 0.0, 0.0, 1.0
        );
        
        // 第一個 block_display (bell) 的預期變換矩陣  
        List<Double> expected2 = Arrays.asList(
            0.0, -1.0, 0.0, 0.5625,
            0.9659258263, 0.0, -0.2588190451, 0.1455857129,
            0.2588190451, 0.0, 0.9659258263, -0.5433332773,
            0.0, 0.0, 0.0, 1.0
        );
        
        Matrix4f expected1Matrix = listToMatrix4f(expected1);
        Matrix4f expected2Matrix = listToMatrix4f(expected2);
        
        System.out.println("預期的 item_display 變換矩陣:");
        printMatrix("expected1", expected1Matrix);
        
        System.out.println("\n預期的第一個 bell 變換矩陣:");
        printMatrix("expected2", expected2Matrix);
        
        // 嘗試分解第一個矩陣
        System.out.println("\n=== 分析第一個矩陣的特徵 ===");
        // 觀察到這個矩陣有旋轉和平移成分
        // m11 ≈ 0.9658, m12 ≈ 0.9654, m13 ≈ 0.8008
        // m21 ≈ 0.2580, m22 ≈ 0.2595, m23 ≈ 0.1497
        
        // 這看起來像是一個繞X軸旋轉約15度的結果
        double angle = Math.atan2(0.257992044, 0.965845); // ≈ 0.2618 radians ≈ 15 degrees
        System.out.printf("推測的旋轉角度: %.4f radians (%.1f degrees)\n", angle, Math.toDegrees(angle));
        
        // 創建一個繞X軸旋轉15度的矩陣
        Matrix4f rotationMatrix = new Matrix4f().identity().rotateX((float)angle);
        System.out.println("\n繞X軸旋轉15度的矩陣:");
        printMatrix("rotationMatrix", rotationMatrix);
        
        // 應用平移
        Matrix4f testMatrix = new Matrix4f(rotationMatrix).translate(0f, 1f, -0.125f);
        System.out.println("\n旋轉+平移 (0, 1, -0.125) 的矩陣:");
        printMatrix("testMatrix", testMatrix);
        
        // 分析第二個 bell 矩陣的特徵
        System.out.println("\n=== 分析第一個 bell 矩陣的特徵 ===");
        // 這個矩陣的第一行是 [0, -1, 0, 0.5625]，表示繞Z軸旋轉90度
        // 同時有其他旋轉和平移成分
        
        // 看起來 transforms 矩陣中已經包含了所有必要的變換信息
        // 問題可能是在 defaultTransform 的處理上
        
        System.out.println("\n=== 直接分析 JSON 中的變換矩陣 ===");
        
        // chalk board 的 transforms 矩陣
        List<Double> chalkTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f chalkMatrix = listToMatrix4f(chalkTransforms);
        System.out.println("chalk board transforms 矩陣:");
        printMatrix("chalkMatrix", chalkMatrix);
        
        // 關鍵洞察：也許 transforms 矩陣已經包含了 defaultTransform 的效果！
        // 讓我檢查這個假設
        
        // 如果我忽略所有的 defaultTransform，只使用 transforms 會如何？
        System.out.println("\n=== 測試：忽略 defaultTransform，只用 transforms ===");
        
        // 1. identity (restaurant sign)
        Matrix4f result = new Matrix4f().identity();
        
        // 2. 乘以 chalk board transforms
        result.mul(chalkMatrix);
        System.out.println("after chalk board:");
        printMatrix("result", result);
        
        // 3. Group 11 - identity
        // (不做任何操作)
        
        // 4. Group 2 transforms
        List<Double> group2Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.10937500000000044,
            0.0, 1.4138, 0.0, 0.040409375,
            0.0, 0.0, 0.0011, 0.0624828125,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f group2Matrix = listToMatrix4f(group2Transforms);
        result.mul(group2Matrix);
        System.out.println("after Group 2:");
        printMatrix("result", result);
        
        // 5. Group 1 transforms
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f group1Matrix = listToMatrix4f(group1Transforms);
        result.mul(group1Matrix);
        System.out.println("after Group 1:");
        printMatrix("result", result);
        
        // 6. item_display transforms
        List<Double> itemTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f itemMatrix = listToMatrix4f(itemTransforms);
        result.mul(itemMatrix);
        System.out.println("final result (transforms only):");
        printMatrix("result", result);
        
        checkSimilarity("transforms only result", result, expected1Matrix);
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
    
    private static void printMatrix(String name, Matrix4f matrix) {
        System.out.printf("%s:\n", name);
        System.out.printf("[%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f]\n",
            matrix.m00(), matrix.m01(), matrix.m02(), matrix.m03(),
            matrix.m10(), matrix.m11(), matrix.m12(), matrix.m13(),
            matrix.m20(), matrix.m21(), matrix.m22(), matrix.m23(),
            matrix.m30(), matrix.m31(), matrix.m32(), matrix.m33());
    }
    
    private static void checkSimilarity(String name, Matrix4f actual, Matrix4f expected) {
        float maxDiff = 0;
        float totalDiff = 0;
        for (int i = 0; i < 16; i++) {
            float diff = Math.abs(getMatrixElement(actual, i) - getMatrixElement(expected, i));
            maxDiff = Math.max(maxDiff, diff);
            totalDiff += diff;
        }
        System.out.printf("%s - 最大差異: %.6f, 總差異: %.6f, 平均差異: %.6f\n", 
            name, maxDiff, totalDiff, totalDiff / 16);
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
