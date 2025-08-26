import org.joml.Matrix4f;
import java.util.Arrays;
import java.util.List;

public class MatrixAnalysis {
    public static void main(String[] args) {
        // 分析 chalk board 的變換矩陣
        List<Double> chalkBoardTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        
        System.out.println("=== 分析 chalk board 變換矩陣 ===");
        Matrix4f chalkMatrix = listToMatrix4f(chalkBoardTransforms);
        printMatrix("chalk board 變換矩陣", chalkMatrix);
        
        // 分析預期的第一個 item_display 結果
        List<Double> expectedTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.9658446719, 0.965442044, 0.8008294014,
            0.0, 0.257992044, 0.2594946719, 0.1496755161,
            0.0, 0.0, 0.0, 1.0
        );
        
        System.out.println("\n=== 分析預期的第一個 item_display 變換 ===");
        Matrix4f expectedMatrix = listToMatrix4f(expectedTransforms);
        printMatrix("預期變換矩陣", expectedMatrix);
        
        // 分析 Group 1 的本地變換
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        
        System.out.println("\n=== 分析 Group 1 變換矩陣 ===");
        Matrix4f group1Matrix = listToMatrix4f(group1Transforms);
        printMatrix("Group 1 變換矩陣", group1Matrix);
        
        // 分析 item_display 的本地變換
        List<Double> itemDisplayTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        
        System.out.println("\n=== 分析 item_display 變換矩陣 ===");
        Matrix4f itemMatrix = listToMatrix4f(itemDisplayTransforms);
        printMatrix("item_display 變換矩陣", itemMatrix);
        
        // 嘗試不同的組合方式
        System.out.println("\n=== 測試不同的矩陣組合 ===");
        
        // 方式1：chalk * group1 * item  
        Matrix4f result1 = new Matrix4f(chalkMatrix).mul(group1Matrix).mul(itemMatrix);
        printMatrix("chalk * group1 * item", result1);
        
        // 方式2：item * group1 * chalk
        Matrix4f result2 = new Matrix4f(itemMatrix).mul(group1Matrix).mul(chalkMatrix);
        printMatrix("item * group1 * chalk", result2);
        
        // 方式3：chalk * (group1 * item)
        Matrix4f temp = new Matrix4f(group1Matrix).mul(itemMatrix);
        Matrix4f result3 = new Matrix4f(chalkMatrix).mul(temp);
        printMatrix("chalk * (group1 * item)", result3);
        
        // 檢查是否與預期結果相符
        System.out.println("\n=== 檢查相似度 ===");
        checkSimilarity("result1", result1, expectedMatrix);
        checkSimilarity("result2", result2, expectedMatrix);
        checkSimilarity("result3", result3, expectedMatrix);
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
    }
    
    private static float getMatrixElement(Matrix4f matrix, int index) {
        switch(index) {
            case 0: return matrix.m00(); case 1: return matrix.m01(); 
            case 2: return matrix.m02(); case 3: return matrix.m03();
            case 4: return matrix.m10(); case 5: return matrix.m11(); 
            case 6: return matrix.m12(); case 7: return matrix.m13();
            case 8: return matrix.m20(); case 9: return matrix.m21(); 
            case 10: return matrix.m22(); case 11: return matrix.m23();
            case 12: return matrix.m30(); case 13: return matrix.m31(); 
            case 14: return matrix.m32(); case 15: return matrix.m33();
            default: return 0f;
        }
    }
}
