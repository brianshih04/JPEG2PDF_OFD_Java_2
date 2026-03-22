import java.nio.file.Files;
import java.nio.file.Paths;

public class CheckFile {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("D:/Projects/JPEG2PDF_OFD_Java_2/src/main/java/com/ocr/jpeg2pdf/service/impl/OfdLayoutDirectServiceImpl.java")), "UTF-8");
        int start = content.indexOf("java.awt.Font awtFont = new java.awt.Font(java.awt.Font.SANS_SERIF");
        if (start != -1) {
            String snippet = content.substring(start, Math.min(start + 500, content.length()));
            System.out.println("=== Current content ===");
            System.out.println(snippet);
        } else {
            System.out.println("Pattern not found!");
        }
    }
}