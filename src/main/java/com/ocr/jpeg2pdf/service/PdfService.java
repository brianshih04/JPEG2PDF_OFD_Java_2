package com.ocr.jpeg2pdf.service;

import com.ocr.jpeg2pdf.model.OcrResult;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

/**
 * PDF 服務介面
 */
public interface PdfService {
    
    /**
     * 生成 Searchable PDF
     * 
     * @param images 圖片列表
     * @param ocrResults OCR 結果列表
     * @param outputPath 輸出路徑
     * @return 輸出檔案路徑
     */
    Path generateSearchablePdf(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception;
    
    /**
     * 合併多個 PDF
     * 
     * @param pdfPaths PDF 路徑列表
     * @param outputPath 輸出路徑
     * @return 輸出檔案路徑
     */
    Path mergePdfs(List<Path> pdfPaths, Path outputPath) throws Exception;
}
