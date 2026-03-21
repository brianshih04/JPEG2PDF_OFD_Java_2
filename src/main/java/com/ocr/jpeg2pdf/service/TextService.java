package com.ocr.jpeg2pdf.service;

import com.ocr.jpeg2pdf.model.OcrResult;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

/**
 * TEXT 服務介面
 */
public interface TextService {
    
    /**
     * 匯出純文字
     * 
     * @param ocrResults OCR 結果列表
     * @param outputPath 輸出路徑
     * @return 輸出檔案路徑
     */
    Path exportText(List<OcrResult> ocrResults, Path outputPath) throws Exception;
}
