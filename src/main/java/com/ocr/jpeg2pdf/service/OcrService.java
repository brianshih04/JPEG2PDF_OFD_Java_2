package com.ocr.jpeg2pdf.service;

import com.ocr.jpeg2pdf.model.OcrResult;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * OCR 服務介面
 */
public interface OcrService {
    
    /**
     * 初始化 OCR 引擎
     */
    void initialize() throws Exception;
    
    /**
     * 辨識圖片中的文字
     * 
     * @param image 圖片
     * @param language 語言 (chinese_cht, ch, en, japan)
     * @return OCR 結果
     */
    OcrResult recognize(BufferedImage image, String language) throws Exception;
    
    /**
     * 批量辨識
     * 
     * @param images 圖片列表
     * @param language 語言
     * @return OCR 結果列表
     */
    List<OcrResult> batchRecognize(List<BufferedImage> images, String language) throws Exception;
    
    /**
     * 檢查是否已初始化
     */
    boolean isInitialized();
}
