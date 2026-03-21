import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

import java.io.File;

public class TestTransparentText {
    public static void main(String[] args) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 18.0f);
                contentStream.setRenderingMode(RenderingMode.FILL);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Test 1 VISIBLE");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 18.0f);
                contentStream.setRenderingMode(RenderingMode.NEITHER);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Test 2 NEITHER");
                contentStream.endText();
            }
            
            File output = new File("P:/OCR/Output/test_transparency.pdf");
            doc.save(output);
            System.out.println("Created: " + output.getAbsolutePath());
        }
    }
}
