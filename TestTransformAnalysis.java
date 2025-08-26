import dev.twme.bdengineparser.BDEngineParser;
import dev.twme.bdengineparser.model.ProjectElement;
import org.joml.Matrix4f;

import java.util.List;

public class TestTransformAnalysis {
    public static void main(String[] args) {
        try {
            BDEngineParser parser = new BDEngineParser();
            List<ProjectElement> elements = parser.parseFromFile("restaurant-sign.json");
            
            System.out.println("=== 分析 JSON 結構 ===");
            analyzeElements(elements, 0);
            
            System.out.println("\n=== 預期的 Minecraft 指令輸出 ===");
            printExpectedOutput();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void analyzeElements(List<ProjectElement> elements, int depth) {
        String indent = "  ".repeat(depth);
        for (ProjectElement element : elements) {
            System.out.println(indent + "Element: " + element.getName());
            System.out.println(indent + "  isCollection: " + element.getIsCollection());
            System.out.println(indent + "  isBlockDisplay: " + element.getIsBlockDisplay());
            System.out.println(indent + "  isItemDisplay: " + element.getIsItemDisplay());
            
            if (element.getTransforms() != null) {
                System.out.println(indent + "  transforms: " + element.getTransforms());
            }
            
            if (element.getDefaultTransform() != null) {
                System.out.println(indent + "  defaultTransform: " + element.getDefaultTransform());
            }
            
            if (element.getBrightness() != null) {
                System.out.println(indent + "  brightness: sky=" + element.getBrightness().getSky() + 
                                 ", block=" + element.getBrightness().getBlock());
            }
            
            if (element.getNbt() != null && !element.getNbt().isEmpty()) {
                System.out.println(indent + "  nbt: " + element.getNbt());
            }
            
            Matrix4f worldTransform = element.getWorldTransform();
            if (worldTransform != null) {
                System.out.println(indent + "  worldTransform: " + formatMatrix(worldTransform));
            }
            
            if (element.getChildren() != null && !element.getChildren().isEmpty()) {
                System.out.println(indent + "  children (" + element.getChildren().size() + "):");
                analyzeElements(element.getChildren(), depth + 1);
            }
            System.out.println();
        }
    }
    
    private static String formatMatrix(Matrix4f matrix) {
        return String.format("[%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f]",
            matrix.m00(), matrix.m01(), matrix.m02(), matrix.m03(),
            matrix.m10(), matrix.m11(), matrix.m12(), matrix.m13(),
            matrix.m20(), matrix.m21(), matrix.m22(), matrix.m23(),
            matrix.m30(), matrix.m31(), matrix.m32(), matrix.m33());
    }
    
    private static void printExpectedOutput() {
        System.out.println("預期的第一個實體 (item_display):");
        System.out.println("transformation:[1f,0f,0f,0f,0f,0.9658446719f,0.965442044f,0.8008294014f,0f,0.257992044f,0.2594946719f,0.1496755161f,0f,0f,0f,1f]");
        
        System.out.println("\n預期的第一個 block_display (bell):");
        System.out.println("transformation:[0f,-1f,0f,0.5625f,0.9659258263f,0f,-0.2588190451f,0.1455857129f,0.2588190451f,0f,0.9659258263f,-0.5433332773f,0f,0f,0f,1f]");
    }
}
