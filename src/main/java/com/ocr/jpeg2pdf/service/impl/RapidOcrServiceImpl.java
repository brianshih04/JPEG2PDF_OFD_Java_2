package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.service.OcrService;
import io.github.mymonstercat.ocr.InferenceEngine;
import io.github.mymonstercat.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * RapidOcr-Java 服務實作
 * 
 * 使用 io.github.mymonstercat:rapidocr
 * GitHub: https://github.com/MyMonsterCat/RapidOcr-Java
 * 
 * 特點：
 * - 純 Java 調用 RapidOcr (基於 PaddleOCR)
 * - 支援 Mac、Windows、Linux
 * - 動態庫整合在 jar 中
 * - 支援 PP-OCRv4 模型
 */
@Slf4j
@Service
public class RapidOcrServiceImpl implements OcrService {
    
    private final AppConfig config;
    private boolean initialized = false;
    private InferenceEngine engine;
    
    public RapidOcrServiceImpl(AppConfig config) {
        this.config = config;
    }
    
    @Override
    public void initialize() throws Exception {
        log.info("初始化 RapidOcr-Java...");
        
        // 初始化引擎（使用 PP-OCRv4 模型）
        engine = InferenceEngine.getInstance(Model.ONNX_PPOCR_V4);
        
        initialized = true;
        log.info("RapidOcr-Java 初始化完成");
    }
    
    @Override
    public com.ocr.jpeg2pdf.model.OcrResult recognize(BufferedImage image, String language) throws Exception {
        if (!initialized) {
            initialize();
        }
        
        log.info("執行 OCR 辨識, 語言: {}, 圖片尺寸: {}x{}", language, image.getWidth(), image.getHeight());
        
        try {
            // 儲存圖片到暫存檔（RapidOcr 需要 file path）
            File tempFile = File.createTempFile("ocr_", ".png");
            ImageIO.write(image, "PNG", tempFile);
            
            // 執行 OCR
            com.benjaminwan.ocrlibrary.OcrResult rapidResult = engine.runOcr(tempFile.getAbsolutePath());
            
            // 解析結果
            List<com.ocr.jpeg2pdf.model.OcrResult.TextPosition> positions = new ArrayList<>();
            StringBuilder fullText = new StringBuilder();
            double totalConfidence = 0.0;
            int lineCount = 0;
            
            // 使用 getTextBlocks() 獲取真實座標
            java.util.ArrayList<com.benjaminwan.ocrlibrary.TextBlock> textBlocks = rapidResult.getTextBlocks();
            
            if (textBlocks != null && !textBlocks.isEmpty()) {
                log.info("獲取到 {} 個文字方塊（真實座標）", textBlocks.size());
                
                for (com.benjaminwan.ocrlibrary.TextBlock block : textBlocks) {
                    String text = block.getText();
                    if (text == null || text.trim().isEmpty()) continue;
                    
                    fullText.append(text).append("\n");
                    lineCount++;
                    totalConfidence += block.getBoxScore();
                    
                    // 獲取四個座標點（四邊形）
                    java.util.ArrayList<com.benjaminwan.ocrlibrary.Point> boxPoints = block.getBoxPoint();
                    
                    if (boxPoints != null && boxPoints.size() >= 4) {
                        // 轉換座標
                        List<com.ocr.jpeg2pdf.model.OcrResult.Point> box = new ArrayList<>();
                        for (com.benjaminwan.ocrlibrary.Point p : boxPoints) {
                            box.add(new com.ocr.jpeg2pdf.model.OcrResult.Point(p.getX(), p.getY()));
                        }
                        
                        // 計算邊界框
                        double minX = boxPoints.stream().mapToInt(com.benjaminwan.ocrlibrary.Point::getX).min().orElse(0);
                        double minY = boxPoints.stream().mapToInt(com.benjaminwan.ocrlibrary.Point::getY).min().orElse(0);
                        double maxX = boxPoints.stream().mapToInt(com.benjaminwan.ocrlibrary.Point::getX).max().orElse(0);
                        double maxY = boxPoints.stream().mapToInt(com.benjaminwan.ocrlibrary.Point::getY).max().orElse(0);
                        
                        double width = maxX - minX;
                        double height = maxY - minY;
                        
                        // 估算字體大小（基於高度）
                        double fontSize = height * 0.8;  // 文字高度約為行高的 80%
                        
                        log.debug("文字方塊: '{}', 座標=({},{}), 大小={}x{}, 字體={}", 
                            text.substring(0, Math.min(text.length(), 20)), minX, minY, width, height, fontSize);
                        
                        positions.add(new com.ocr.jpeg2pdf.model.OcrResult.TextPosition(
                            text,
                            block.getBoxScore(),
                            minX, minY, width, height,
                            fontSize,
                            box
                        ));
                    }
                }
                
                // 計算平均信心度
                totalConfidence = lineCount > 0 ? totalConfidence / lineCount : 0.0;
            } else {
                log.warn("RapidOcr 沒有返回文字方塊，使用舊方法");
                
                // 舊方法：僅使用 getStrRes()
                String strRes = rapidResult.getStrRes();
                if (strRes != null && !strRes.trim().isEmpty()) {
                    String[] lines = strRes.split("\n");
                    int y = 100;
                    for (String line : lines) {
                        if (line.trim().isEmpty()) continue;
                        
                        fullText.append(line).append("\n");
                        lineCount++;
                        
                        List<com.ocr.jpeg2pdf.model.OcrResult.Point> box = new ArrayList<>();
                        box.add(new com.ocr.jpeg2pdf.model.OcrResult.Point(100, y));
                        box.add(new com.ocr.jpeg2pdf.model.OcrResult.Point(400, y));
                        box.add(new com.ocr.jpeg2pdf.model.OcrResult.Point(400, y + 30));
                        box.add(new com.ocr.jpeg2pdf.model.OcrResult.Point(100, y + 30));
                        
                        positions.add(new com.ocr.jpeg2pdf.model.OcrResult.TextPosition(
                            line, 0.95, 100, y, 300, 30, 24, box
                        ));
                        
                        y += 40;
                    }
                    totalConfidence = lineCount > 0 ? 0.95 : 0.0;
                }
            }
            
            // 清理暫存檔
            tempFile.delete();
            
            log.info("OCR 完成: {} 行, 信心度: {}", lineCount, totalConfidence);
            
            return new com.ocr.jpeg2pdf.model.OcrResult(
                "rapidocr-" + System.currentTimeMillis(),
                fullText.toString().trim(),
                totalConfidence,
                lineCount,
                positions,
                null
            );
            
        } catch (Exception e) {
            log.error("OCR 失敗", e);
            throw new RuntimeException("OCR 辨識失敗: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<com.ocr.jpeg2pdf.model.OcrResult> batchRecognize(List<BufferedImage> images, String language) throws Exception {
        List<com.ocr.jpeg2pdf.model.OcrResult> results = new ArrayList<>();
        
        log.info("批量 OCR 辨識: {} 張圖片", images.size());
        
        for (int i = 0; i < images.size(); i++) {
            try {
                com.ocr.jpeg2pdf.model.OcrResult result = recognize(images.get(i), language);
                results.add(result);
                log.info("圖片 {}/{} OCR 完成: {} 行, 信心度: {}", 
                    i + 1, images.size(), result.getLineCount(), result.getConfidence());
            } catch (Exception e) {
                log.error("圖片 {} OCR 失敗", i, e);
                results.add(new com.ocr.jpeg2pdf.model.OcrResult(
                    "error-" + i,
                    "",
                    0.0,
                    0,
                    new ArrayList<>(),
                    e.getMessage()
                ));
            }
        }
        
        return results;
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
