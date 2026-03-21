package com.ocr.jpeg2pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

import java.io.File;

public class TestTransparentText {
    public static void main(String[] args) throws Exception {
        System.out.println("Testing PDFBox 2.x Transparent Text");
        
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 24);
                
                // Test 1: FILL mode (visible)
                contentStream.setRenderingMode(RenderingMode.FILL);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Test 1: FILL (visible)");
                contentStream.endText();
                
                // Test 2: NEITHER mode (invisible)
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 24);
                contentStream.setRenderingMode(RenderingMode.NEITHER);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText("Test 2: NEITHER (invisible)");
                contentStream.endText();
                
                System.out.println("Created test PDF with both visible and invisible text");
            }
            
            File output = new File("test_transparency.pdf");
            doc.save(output);
            System.out.println("Saved to: " + output.getAbsolutePath());
        }
    }
}
