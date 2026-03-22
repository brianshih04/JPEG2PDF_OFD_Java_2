package com.ocr.jpeg2pdf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * 應用程式設定（跨平台支援）
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    /**
     * 輸出資料夾（跨平台）
     */
    private String outputFolder = getDefaultOutputFolder();
    
    /**
     * Watch 資料夾（跨平台）
     */
    private String watchFolder = getDefaultWatchFolder();
    
    /**
     * 已處理資料夾（跨平台）
     */
    private String saveFolder = getDefaultSaveFolder();
    
    /**
     * 暫存資料夾（跨平台）
     */
    private String uploadFolder = getDefaultTempFolder();
    
    /**
     * OCR 引擎類型 (paddleocr, tesseract)
     */
    private String ocrEngine = "paddleocr";
    
    /**
     * 預設語言
     */
    private String defaultLanguage = "chinese_cht";
    
    /**
     * PDF 字體路徑（跨平台自動檢測）
     */
    private String fontPath = getDefaultFontPath();
    
    /**
     * OFD 字體路徑（跨平台自動檢測）
     */
    private String ofdFontPath = getDefaultFontPath();
    
    /**
     * 獲取默認輸出目錄（跨平台）
     */
    private static String getDefaultOutputFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        if (os.contains("win")) {
            return "C:/OCR/Output";  // Windows
        } else if (os.contains("mac")) {
            return home + "/Documents/OCR/Output";  // macOS
        } else {
            return home + "/OCR/Output";  // Linux
        }
    }
    
    /**
     * 獲取默認 Watch 目錄（跨平台）
     */
    private static String getDefaultWatchFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        if (os.contains("win")) {
            return "C:/OCR/Watch";
        } else if (os.contains("mac")) {
            return home + "/Documents/OCR/Watch";
        } else {
            return home + "/OCR/Watch";
        }
    }
    
    /**
     * 獲取默認 Save 目錄（跨平台）
     */
    private static String getDefaultSaveFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        if (os.contains("win")) {
            return "C:/OCR/Save";
        } else if (os.contains("mac")) {
            return home + "/Documents/OCR/Save";
        } else {
            return home + "/OCR/Save";
        }
    }
    
    /**
     * 獲取默認暫存目錄（跨平台）
     */
    private static String getDefaultTempFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        if (os.contains("win")) {
            return "P:/OCR/temp";
        } else if (os.contains("mac")) {
            return home + "/Documents/OCR/temp";
        } else {
            return home + "/OCR/temp";
        }
    }
    
    /**
     * 獲取默認字體路徑（跨平台）
     */
    private static String getDefaultFontPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        if (os.contains("win")) {
            // Windows: 嘗試多個可能的中文字體
            String[] fontPaths = {
                "C:/Windows/Fonts/simsun.ttc",
                "C:/Windows/Fonts/msyh.ttc",  // 微软雅黑
                "C:/Windows/Fonts/simhei.ttf"  // 黑体
            };
            
            for (String path : fontPaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            return "C:/Windows/Fonts/simsun.ttc";  // 默認
            
        } else if (os.contains("mac")) {
            // macOS: 嘗試多個可能的中文字體
            String[] fontPaths = {
                "/System/Library/Fonts/STHeiti Light.ttc",  // 黑体
                "/System/Library/Fonts/PingFang.ttc",  // 苹方
                "/Library/Fonts/Arial Unicode.ttf",
                home + "/Library/Fonts/simsun.ttc"
            };
            
            for (String path : fontPaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            return "/System/Library/Fonts/STHeiti Light.ttc";  // 默認
            
        } else {
            // Linux: 嘗試多個可能的中文字體
            String[] fontPaths = {
                "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc",  // 文泉驿
                "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc",  // Noto
                "/usr/share/fonts/truetype/arphic/uming.ttc",  // AR PL UMing
                "/usr/local/share/fonts/simsun.ttc",
                home + "/.fonts/simsun.ttc"
            };
            
            for (String path : fontPaths) {
                if (new java.io.File(path).exists()) {
                    return path;
                }
            }
            return "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc";  // 默認
        }
    }
}
