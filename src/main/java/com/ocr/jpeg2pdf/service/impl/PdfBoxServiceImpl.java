package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.awt.Color;

/**
 * PDFBox 服務實作
 * 使用 Apache PDFBox 生成 Searchable PDF
 */
@Slf4j
@Service
public class PdfBoxServiceImpl implements PdfService {
    
    private final AppConfig config;
    
    public PdfBoxServiceImpl(AppConfig config) {
        this.config = config;
    }
    
    @Override
    public Path generateSearchablePdf(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.info("生成 Searchable PDF: {}", outputPath);
        
        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                OcrResult ocrResult = i < ocrResults.size() ? ocrResults.get(i) : null;
                
                // 載入字體（每次都載入，因為 document 不同）
                PDFont font = loadFont(document);
                
                // 建立頁面（與圖片同尺寸）
                float width = image.getWidth();
                float height = image.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));
                document.addPage(page);
                
                // 轉換圖片為 bytes
                byte[] imageBytes = imageToBytes(image);
                
                // 建立 PDImageXObject
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                    document,
                    imageBytes,
                    "image"
                );
                
                // 繪製內容
                try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, 
                    PDPageContentStream.AppendMode.APPEND, 
                    true, 
                    true
                )) {
                    // 1. 繪製圖片（全頁）
                    contentStream.drawImage(pdImage, 0, 0, width, height);
                    
                    // 2. 插入透明文字層（可搜尋）
                    if (ocrResult != null && ocrResult.getTextPositions() != null) {
                        log.info("開始繪製 {} 個透明文字", ocrResult.getTextPositions().size());
                        int textCount = 0;
                        for (OcrResult.TextPosition tp : ocrResult.getTextPositions()) {
                            drawInvisibleText(contentStream, tp, page, font, height);
                            textCount++;
                        }
                        log.info("第 {} 頁繪製了 {} 個透明文字", i + 1, textCount);
                    } else {
                        log.warn("第 {} 頁沒有 OCR 結果或文字位置資訊", i + 1);
                    }
                }
                
                log.info("第 {} 頁處理完成", i + 1);
            }
            
            // 儲存 PDF
            document.save(outputPath.toFile());
            log.info("PDF 已儲存: {} ({} 頁)", outputPath, images.size());
            return outputPath;
        }
    }
    
    /**
     * 載入字體
     */
    private PDFont loadFont(PDDocument document) throws IOException {
        File fontFile = new File(config.getFontPath());
        if (fontFile.exists()) {
            try {
                PDType0Font font = PDType0Font.load(document, fontFile);
                log.info("字體載入成功: {}", config.getFontPath());
                return font;
            } catch (Exception e) {
                log.warn("字體載入失敗: {}, 使用標準字體 Helvetica", e.getMessage());
            }
        } else {
            log.warn("字體檔案不存在: {}, 使用標準字體 Helvetica", config.getFontPath());
        }
        // 使用 PDFBox 標準字體 Helvetica（支援基本拉丁字符）
        log.info("使用 PDFBox 標準字體 Helvetica");
        return PDType1Font.HELVETICA;
    }
    
    /**
     * 繪製透明文字（可搜尋但不可見）
     * 
     * PDF 座標系統：左下角為原點，Y 軸向上
     * 圖片座標系統：左上角為原點，Y 軸向下
     * 需要轉換 Y 座標
     */
    private void drawInvisibleText(
        PDPageContentStream contentStream, 
        OcrResult.TextPosition tp, 
        PDPage page,
        PDFont font,
        float pageHeight
    ) throws IOException {
        
        // 計算 PDF 座標
        // 圖片 Y + 高度 = 文字底部在圖片座標系的位置
        // 轉換為 PDF 座標：pageHeight - (y + height) = PDF Y
        float pdfX = (float) tp.getX();
        float pdfY = pageHeight - (float) (tp.getY() + tp.getHeight());
        float fontSize = (float) tp.getFontSize();
        
        // 如果字體大小無效，使用預設值
        if (fontSize <= 0) {
            fontSize = 24.0f;  // 預設字體大小
            log.warn("字體大小無效（{}），使用預設值: {}", tp.getFontSize(), fontSize);
        }
        
        // 如果字體為 null，使用預設字體大小估算
        if (font == null) {
            // 沒有字體時，無法繪製文字
            log.debug("跳過文字（無字體）: {}", tp.getText());
            return;
        }
        
        log.debug("繪製文字: '{}' at ({}, {}), 字體大小: {}", tp.getText(), pdfX, pdfY, fontSize);
        
        try {
            // 使用渲染模式 3 (NEITHER) - 文字完全透明
            // 這會讓文字既不填充也不描邊，視覺上完全不可見
            // 但仍然存在於 PDF 中，可以被提取和搜尋
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setRenderingMode(RenderingMode.NEITHER);
            
            // 移動到正確位置
            contentStream.newLineAtOffset(pdfX, pdfY);
            
            // 顯示文字（透明）
            contentStream.showText(tp.getText());
            contentStream.endText();
            
            log.debug("繪製透明文字: {} at ({}, {})", tp.getText(), pdfX, pdfY);
            
        } catch (Exception e) {
            log.warn("繪製文字失敗: {} - {}", tp.getText(), e.getMessage());
        }
    }
    
    /**
     * 圖片轉 byte 陣列
     */
    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean written = javax.imageio.ImageIO.write(image, "PNG", baos);
        if (!written) {
            // 嘗試 JPEG
            baos.reset();
            written = javax.imageio.ImageIO.write(image, "JPEG", baos);
        }
        if (!written) {
            throw new IOException("無法轉換圖片格式");
        }
        return baos.toByteArray();
    }
    
    @Override
    public Path generateSearchablePdfForOfd(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.info("生成 PDF (可见文字，用于 OFD 转换): {}", outputPath);
        
        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                OcrResult ocrResult = i < ocrResults.size() ? ocrResults.get(i) : null;
                
                // 创建页面（使用图片尺寸）
                float width = image.getWidth();
                float height = image.getHeight();
                PDRectangle pageSize = new PDRectangle(width, height);
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                
                // 载入字体
                PDFont font = loadFont(document);
                
                // 绘制内容
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // 1. 绘制图片
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageToBytes(image), "image");
                    contentStream.drawImage(pdImage, 0, 0, width, height);
                    
                    // 2. 插入可见文字层（用于 OFD）
                    if (ocrResult != null && ocrResult.getTextPositions() != null) {
                        log.info("開始繪製 {} 個可見文字", ocrResult.getTextPositions().size());
                        int textCount = 0;
                        for (OcrResult.TextPosition tp : ocrResult.getTextPositions()) {
                            drawVisibleText(contentStream, tp, page, font, height);
                            textCount++;
                        }
                        log.info("第 {} 頁繪製了 {} 個可見文字", i + 1, textCount);
                    } else {
                        log.warn("第 {} 頁沒有 OCR 結果或文字位置資訊", i + 1);
                    }
                }
                
                log.info("第 {} 頁處理完成", i + 1);
            }
            
            // 儲存 PDF
            document.save(outputPath.toFile());
            log.info("PDF 已儲存: {} ({} 頁)", outputPath, images.size());
            return outputPath;
        }
    }
    
    /**
     * 繪製可見文字（用於 OFD 轉換）
     */
    private void drawVisibleText(
        PDPageContentStream contentStream, 
        OcrResult.TextPosition tp, 
        PDPage page,
        PDFont font,
        float pageHeight
    ) throws IOException {
        
        // 計算 PDF 座標
        float pdfX = (float) tp.getX();
        float pdfY = pageHeight - (float) (tp.getY() + tp.getHeight());
        float fontSize = (float) tp.getFontSize();
        
        // 如果字體大小無效，使用預設值
        if (fontSize <= 0) {
            fontSize = 24.0f;
            log.warn("字體大小無效（{}），使用預設值: {}", tp.getFontSize(), fontSize);
        }
        
        if (font == null) {
            log.debug("跳過文字（無字體）: {}", tp.getText());
            return;
        }
        
        log.debug("繪製可見文字: '{}' at ({}, {}), 字體大小: {}", tp.getText(), pdfX, pdfY, fontSize);
        
        try {
            // 使用渲染模式 0 (FILL) - 可見文字
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setRenderingMode(RenderingMode.FILL);  // 可見文字
            contentStream.newLineAtOffset(pdfX, pdfY);
            contentStream.showText(tp.getText());
            contentStream.endText();
            
        } catch (Exception e) {
            log.warn("繪製文字失敗: {} - {}", tp.getText(), e.getMessage());
        }
    }
    
    @Override
    public Path mergePdfs(List<Path> pdfPaths, Path outputPath) throws Exception {
        log.info("合併 {} 個 PDF 到 {}", pdfPaths.size(), outputPath);
        
        try (PDDocument mergedDoc = new PDDocument()) {
            for (Path pdfPath : pdfPaths) {
                try (PDDocument srcDoc = PDDocument.load(pdfPath.toFile())) {
                    // 複製所有頁面
                    for (PDPage page : srcDoc.getPages()) {
                        mergedDoc.addPage(page);
                    }
                }
            }
            mergedDoc.save(outputPath.toFile());
        }
        
        log.info("PDF 合併完成: {}", outputPath);
        return outputPath;
    }
}
