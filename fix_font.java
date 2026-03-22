import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

public class FixFont {
    public static void main(String[] args) throws Exception {
        String filePath = "D:/Projects/JPEG2PDF_OFD_Java_2/src/main/java/com/ocr/jpeg2pdf/service/impl/OfdLayoutDirectServiceImpl.java";
        
        // Read file
        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        
        // Replace SANS_SERIF with SERIF
        String oldText1 = "java.awt.Font.SANS_SERIF";
        String newText1 = "java.awt.Font.SERIF";
        content = content.replace(oldText1, newText1);
        
        // Remove safety gate comment (remove the if statement and brackets)
        String oldText2 = "if (letterSpacing < -0.5) {";
        String newText2 = "// Safety gate removed - let math formula work 100%";
        
        // Find and replace the comment section more precisely
        int safetyGateIndex = content.indexOf(oldText2);
        if (safetyGateIndex != -1) {
            int commentStart = content.indexOf("// 防呆：防止极端情况下字距变成严重的负数挤压");
            if (commentStart != -1) {
                int safetyGateEnd = content.indexOf("}", safetyGateIndex + 40);
                String commentSection = content.substring(commentStart, safetyGateEnd + 1);
                content = content.substring(0, commentStart) + "\n                            " + newText2 + "\n" + content.substring(safetyGateEnd + 1);
            }
        }
        
        // Write file
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        
        System.out.println("File fixed successfully!");
        System.out.println("- Changed SANS_SERIF to SERIF");
        System.out.println("- Removed safety gate mechanism");
        System.out.println("- Updated comments");
    }
}