package com.ocr.jpeg2pdf.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 上傳的圖片資訊
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageInfo {
    
    /**
     * 圖片 ID
     */
    private String id;
    
    /**
     * 原始檔名
     */
    private String originalFilename;
    
    /**
     * 儲存檔名
     */
    private String savedFilename;
    
    /**
     * 檔案路徑
     */
    private String path;
    
    /**
     * 檔案大小 (bytes)
     */
    private long size;
    
    /**
     * 內容類型
     */
    private String contentType;
    
    /**
     * 圖片寬度
     */
    private int width;
    
    /**
     * 圖片高度
     */
    private int height;
    
    /**
     * 建立時間
     */
    private LocalDateTime createdAt;
    
    /**
     * OCR 結果 (執行 OCR 後才有)
     */
    private OcrResult ocrResult;
}
