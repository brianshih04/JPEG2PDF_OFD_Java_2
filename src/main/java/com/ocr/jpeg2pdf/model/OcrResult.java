package com.ocr.jpeg2pdf.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * OCR 辨識結果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResult {
    
    /**
     * 圖片 ID
     */
    private String imageId;
    
    /**
     * 辨識出的完整文字
     */
    private String text;
    
    /**
     * 平均信心度 (0.0 - 1.0)
     */
    private double confidence;
    
    /**
     * 文字行數
     */
    private int lineCount;
    
    /**
     * 詳細位置資訊
     */
    private List<TextPosition> textPositions;
    
    /**
     * 錯誤訊息
     */
    private String error;
    
    /**
     * 文字位置資訊
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextPosition {
        private String text;
        private double confidence;
        private double x;
        private double y;
        private double width;
        private double height;
        private double fontSize;
        private List<Point> box;
    }
    
    /**
     * 座標點
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private double x;
        private double y;
    }
}
