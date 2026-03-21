package com.ocr.jpeg2pdf.service;

import com.ocr.jpeg2pdf.model.OcrResult;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

/**
 * OFD 服務介面
 */
public interface OfdService {
    
    /**
     * 生成 Searchable OFD
     * 
     * @param images 圖片列表
     * @param ocrResults OCR 結果列表
     * @param outputPath 輸出路徑
     * @return 輸出檔案路徑
     */
    Path generateSearchableOfd(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception;
}
