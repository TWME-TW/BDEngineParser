import org.joml.Matrix4f;
import java.util.Arrays;
import java.util.List;

public class DetailedAnalysis {
    public static void main(String[] args) {
        System.out.println("=== 逐層分析變換層次 ===");
        
        // 根據 JSON 數據手動建立正確的變換鏈
        Matrix4f identity = new Matrix4f().identity();
        System.out.println("1. 根元素 (restaurant sign) - identity matrix");
        printMatrix("identity", identity);
        
        // chalk board 的 transforms
        List<Double> chalkTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f chalkMatrix = listToMatrix4f(chalkTransforms);
        System.out.println("\n2. chalk board 的 local transform");
        printMatrix("chalkMatrix", chalkMatrix);
        
        // 應用到目前為止的變換
        Matrix4f worldAfterChalk = new Matrix4f(identity).mul(chalkMatrix);
        System.out.println("\n3. 到 chalk board 為止的 world transform");
        printMatrix("worldAfterChalk", worldAfterChalk);
        
        // chalk board 的 defaultTransform - 根據JSON，它有rotation.x = 0.2617993877991494
        // 這相當於 15 度
        Matrix4f chalkDefault = new Matrix4f().identity()
            .translate(0f, 0f, 0f)  // position: [0, 0, 0]
            .rotateX(0.2617993877991494f)  // rotation.x
            .scale(1f, 1f, 1f);  // scale: [1, 1, 1]
        
        System.out.println("\n4. chalk board 的 defaultTransform");
        printMatrix("chalkDefault", chalkDefault);
        
        // 應用 defaultTransform
        Matrix4f worldAfterChalkDefault = new Matrix4f(worldAfterChalk).mul(chalkDefault);
        System.out.println("\n5. 應用 chalk defaultTransform 後");
        printMatrix("worldAfterChalkDefault", worldAfterChalkDefault);
        
        // Group 11 - identity transform + defaultTransform position: [0, 1, -0.125]
        Matrix4f group11Transform = new Matrix4f().identity();
        Matrix4f group11Default = new Matrix4f().identity()
            .translate(0f, 1f, -0.125f);
            
        Matrix4f worldAfterGroup11 = new Matrix4f(worldAfterChalkDefault)
            .mul(group11Transform)
            .mul(group11Default);
        System.out.println("\n6. 應用 Group 11 後");
        printMatrix("worldAfterGroup11", worldAfterGroup11);
        
        // Group 2 transforms
        List<Double> group2Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.10937500000000044,
            0.0, 1.4138, 0.0, 0.040409375,
            0.0, 0.0, 0.0011, 0.0624828125,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f group2Matrix = listToMatrix4f(group2Transforms);
        
        // Group 2 defaultTransform: position [0, -0.9375, 0.0625], scale [1, 1.4138, 0.0011]
        Matrix4f group2Default = new Matrix4f().identity()
            .translate(0f, -0.9375f, 0.0625f)
            .scale(1f, 1.4138f, 0.0011f);
            
        Matrix4f worldAfterGroup2 = new Matrix4f(worldAfterGroup11)
            .mul(group2Matrix)
            .mul(group2Default);
        System.out.println("\n7. 應用 Group 2 後");
        printMatrix("worldAfterGroup2", worldAfterGroup2);
        
        // Group 1 transforms 和 defaultTransform
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f group1Matrix = listToMatrix4f(group1Transforms);
        
        // Group 1 defaultTransform: rotation.z = 0 (默認)
        Matrix4f group1Default = new Matrix4f().identity();
        
        Matrix4f worldAfterGroup1 = new Matrix4f(worldAfterGroup2)
            .mul(group1Matrix)
            .mul(group1Default);
        System.out.println("\n8. 應用 Group 1 後");
        printMatrix("worldAfterGroup1", worldAfterGroup1);
        
        // 最終的 item_display transform
        List<Double> itemTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f itemMatrix = listToMatrix4f(itemTransforms);
        
        Matrix4f finalResult = new Matrix4f(worldAfterGroup1).mul(itemMatrix);
        System.out.println("\n9. 最終的 item_display transform");
        printMatrix("finalResult", finalResult);
        
        // 比較預期結果
        List<Double> expectedTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.9658446719, 0.965442044, 0.8008294014,
            0.0, 0.257992044, 0.2594946719, 0.1496755161,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f expectedMatrix = listToMatrix4f(expectedTransforms);
        System.out.println("\n10. 預期結果");
        printMatrix("expected", expectedMatrix);
        
        // 檢查相似度
        checkSimilarity("finalResult", finalResult, expectedMatrix);
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
        System.out.printf("[%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f]\n",
            matrix.m00(), matrix.m01(), matrix.m02(), matrix.m03(),
            matrix.m10(), matrix.m11(), matrix.m12(), matrix.m13(),
            matrix.m20(), matrix.m21(), matrix.m22(), matrix.m23(),
            matrix.m30(), matrix.m31(), matrix.m32(), matrix.m33());
    }
    
    private static void checkSimilarity(String name, Matrix4f actual, Matrix4f expected) {
        float maxDiff = 0;
        for (int i = 0; i < 16; i++) {
            float diff = Math.abs(getMatrixElement(actual, i) - getMatrixElement(expected, i));
            maxDiff = Math.max(maxDiff, diff);
        }
        System.out.printf("%s 與預期的最大差異: %.6f\n", name, maxDiff);
        
        // 詳細比較每個元素
        System.out.println("詳細差異:");
        for (int i = 0; i < 16; i++) {
            float actualVal = getMatrixElement(actual, i);
            float expectedVal = getMatrixElement(expected, i);
            float diff = actualVal - expectedVal;
            System.out.printf("  [%2d]: actual=%.6f, expected=%.6f, diff=%.6f\n", 
                i, actualVal, expectedVal, diff);
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
