package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.TextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * TEXT 服務實作
 * 導出純文字文件
 */
@Slf4j
@Service
public class TextServiceImpl implements TextService {
    
    @Override
    public Path exportText(List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.info("開始導出純文字: {}", outputPath);
        
        // 創建輸出目錄（如果不存在）
        Files.createDirectories(outputPath.getParent());
        
        // 使用 BufferedWriter 寫入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            for (int i = 0; i < ocrResults.size(); i++) {
                OcrResult result = ocrResults.get(i);
                
                // 寫入頁面標記
                if (ocrResults.size() > 1) {
                    writer.write(String.format("===== 第 %d 頁 =====\n", i + 1));
                    writer.newLine();
                }
                
                // 寫入 OCR 文字
                List<OcrResult.TextPosition> textPositions = result.getTextPositions();
                if (textPositions != null && !textPositions.isEmpty()) {
                    for (OcrResult.TextPosition pos : textPositions) {
                        String text = pos.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            writer.write(text);
                            writer.newLine();
                        }
                    }
                }
                
                // 頁面之間添加空行
                if (i < ocrResults.size() - 1) {
                    writer.newLine();
                    writer.newLine();
                }
            }
        }
        
        log.info("✅ 純文字導出完成: {}", outputPath);
        log.info("文件大小: {} bytes", Files.size(outputPath));
        
        return outputPath;
    }
}
