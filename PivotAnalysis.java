import org.joml.Matrix4f;
import java.util.Arrays;
import java.util.List;

public class PivotAnalysis {
    public static void main(String[] args) {
        System.out.println("=== 分析 Pivot 和 DefaultTransform 的關係 ===");
        
        // 預期結果
        List<Double> expected1 = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.9658446719, 0.965442044, 0.8008294014,
            0.0, 0.257992044, 0.2594946719, 0.1496755161,
            0.0, 0.0, 0.0, 1.0
        );
        Matrix4f expectedMatrix = listToMatrix4f(expected1);
        
        // 從之前的分析，我們知道只使用 transforms 得到：
        // [1,0,0,0,0,0.9658,-0.2580,0.6142,0,-0.9654,0.2595,0.2644,0,0,0,1]
        // 與預期結果對比：
        // [1,0,0,0,0,0.9658, 0.9654,0.8008,0, 0.2580,0.2595,0.1497,0,0,0,1]
        
        // 關鍵差異：
        // m12: -0.2580 vs 0.9654  (差異: 1.2234)
        // m13:  0.6142 vs 0.8008  (差異: 0.1866) 
        // m21: -0.9654 vs 0.2580  (差異: 1.2234)
        // m23:  0.2644 vs 0.1497  (差異: 0.1147)
        
        System.out.println("觀察到的模式：m12 和 m21 的符號似乎需要翻轉");
        System.out.println("這可能表示需要一個額外的旋轉變換");
        
        // 讓我檢查是否需要一個繞某軸的 180 度旋轉
        Matrix4f result = buildTransformChain();
        System.out.println("原始結果:");
        printMatrix("original", result);
        
        // 嘗試不同的修正變換
        System.out.println("\n=== 嘗試修正變換 ===");
        
        // 1. 繞X軸旋轉180度
        Matrix4f correctionX = new Matrix4f().rotateX((float)Math.PI);
        Matrix4f resultX = new Matrix4f(result).mul(correctionX);
        System.out.println("加上繞X軸180度旋轉:");
        printMatrix("resultX", resultX);
        checkSimilarity("X軸180度", resultX, expectedMatrix);
        
        // 2. 繞Y軸旋轉180度  
        Matrix4f correctionY = new Matrix4f().rotateY((float)Math.PI);
        Matrix4f resultY = new Matrix4f(result).mul(correctionY);
        System.out.println("\n加上繞Y軸180度旋轉:");
        printMatrix("resultY", resultY);
        checkSimilarity("Y軸180度", resultY, expectedMatrix);
        
        // 3. 繞Z軸旋轉180度
        Matrix4f correctionZ = new Matrix4f().rotateZ((float)Math.PI);
        Matrix4f resultZ = new Matrix4f(result).mul(correctionZ);
        System.out.println("\n加上繞Z軸180度旋轉:");
        printMatrix("resultZ", resultZ);
        checkSimilarity("Z軸180度", resultZ, expectedMatrix);
        
        // 4. 嘗試在不同位置應用修正
        System.out.println("\n=== 在變換鏈的不同位置應用修正 ===");
        
        // 在 Group 1 之後應用修正
        Matrix4f resultEarly = buildTransformChainWithCorrection();
        System.out.println("在 Group 1 後應用繞X軸180度旋轉:");
        printMatrix("resultEarly", resultEarly);
        checkSimilarity("早期修正", resultEarly, expectedMatrix);
        
        // 5. 檢查是否需要調整變換順序
        System.out.println("\n=== 檢查變換順序 ===");
        Matrix4f resultReverse = buildReverseTransformChain();
        System.out.println("反向變換順序:");
        printMatrix("resultReverse", resultReverse);
        checkSimilarity("反向順序", resultReverse, expectedMatrix);
    }
    
    private static Matrix4f buildTransformChain() {
        Matrix4f result = new Matrix4f().identity();
        
        // chalk board
        List<Double> chalkTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(chalkTransforms));
        
        // Group 2
        List<Double> group2Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.10937500000000044,
            0.0, 1.4138, 0.0, 0.040409375,
            0.0, 0.0, 0.0011, 0.0624828125,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group2Transforms));
        
        // Group 1
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group1Transforms));
        
        // item_display
        List<Double> itemTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(itemTransforms));
        
        return result;
    }
    
    private static Matrix4f buildTransformChainWithCorrection() {
        Matrix4f result = new Matrix4f().identity();
        
        // chalk board
        List<Double> chalkTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(chalkTransforms));
        
        // Group 2
        List<Double> group2Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.10937500000000044,
            0.0, 1.4138, 0.0, 0.040409375,
            0.0, 0.0, 0.0011, 0.0624828125,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group2Transforms));
        
        // Group 1
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group1Transforms));
        
        // 在這裡應用修正
        Matrix4f correction = new Matrix4f().rotateX((float)Math.PI);
        result.mul(correction);
        
        // item_display
        List<Double> itemTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(itemTransforms));
        
        return result;
    }
    
    private static Matrix4f buildReverseTransformChain() {
        Matrix4f result = new Matrix4f().identity();
        
        // 嘗試反向順序：item * group1 * group2 * chalk
        
        // item_display
        List<Double> itemTransforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.265625,
            0.0, 1.0, 0.0, 0.515625,
            0.0, 0.0, 1.0, 0.265625,
            0.0, 0.0, 0.0, 1.0
        );
        result = listToMatrix4f(itemTransforms);
        
        // Group 1
        List<Double> group1Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.0,
            0.0, 0.7071067811865475, 0.7071067811865476, -0.006472086912079608,
            0.0, -0.7071067811865476, 0.7071067811865475, 0.015624999999999998,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group1Transforms));
        
        // Group 2
        List<Double> group2Transforms = Arrays.asList(
            1.0, 0.0, 0.0, 0.10937500000000044,
            0.0, 1.4138, 0.0, 0.040409375,
            0.0, 0.0, 0.0011, 0.0624828125,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(group2Transforms));
        
        // chalk board
        List<Double> chalkTransforms = Arrays.asList(
            1.0, 0.0, 0.0, -0.37500000000000044,
            0.0, 0.9659258262890683, -0.25881904510252074, 0.03235238063781509,
            0.0, 0.25881904510252074, 0.9659258262890683, -0.12074072828613354,
            0.0, 0.0, 0.0, 1.0
        );
        result.mul(listToMatrix4f(chalkTransforms));
        
        return result;
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
        System.out.printf("%s:\n[%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f]\n", name,
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
        System.out.printf("%s - 最大差異: %.6f, 總差異: %.6f\n", name, maxDiff, totalDiff);
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
