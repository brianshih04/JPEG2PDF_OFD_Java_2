package com.ocr.jpeg2pdf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.model.OcrResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 文件夾監控服務
 */
@Slf4j
@Service
public class WatchFolderService {
    
    @Autowired
    private AppConfig config;
    
    @Autowired
    private OcrService ocrService;
    
    @Autowired
    private OfdService ofdService;
    
    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private TextService textService;
    
    private Thread watchThread;
    private WatchService watchService;
    private final AtomicBoolean isWatching = new AtomicBoolean(false);
    
    /**
     * 啟動監控
     */
    public synchronized boolean startWatching() {
        if (isWatching.get()) {
            log.warn("監控已在運行中");
            return false;
        }
        
        try {
            Path watchPath = Paths.get(config.getWatchFolder());
            
            // 創建監控資料夾（如果不存在）
            if (!Files.exists(watchPath)) {
                Files.createDirectories(watchPath);
                log.info("已創建監控資料夾: {}", watchPath);
            }
            
            // 創建 WatchService
            watchService = FileSystems.getDefault().newWatchService();
            watchPath.register(watchService, 
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
            
            isWatching.set(true);
            
            // 啟動監控線程
            watchThread = new Thread(() -> {
                log.info("📂 開始監控資料夾: {}", watchPath);
                
                while (isWatching.get()) {
                    try {
                        WatchKey key = watchService.take();
                        
                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();
                            
                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }
                            
                            Path fileName = (Path) event.context();
                            Path filePath = watchPath.resolve(fileName);
                            
                            // 只處理圖片文件
                            if (isImageFile(fileName.toString())) {
                                log.info("檢測到新文件: {}", fileName);
                                
                                // 等待文件寫入完成
                                Thread.sleep(1000);
                                
                                // 處理圖片
                                processImage(filePath);
                            }
                        }
                        
                        key.reset();
                    } catch (InterruptedException e) {
                        log.info("監控線程被中斷");
                        break;
                    } catch (Exception e) {
                        log.error("監控錯誤: {}", e.getMessage());
                    }
                }
                
                log.info("監控已停止");
            });
            
            watchThread.setDaemon(true);
            watchThread.start();
            
            log.info("✅ 監控已啟動");
            return true;
            
        } catch (Exception e) {
            log.error("啟動監控失敗: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 停止監控
     */
    public synchronized boolean stopWatching() {
        if (!isWatching.get()) {
            log.warn("監控未在運行");
            return false;
        }
        
        try {
            isWatching.set(false);
            
            if (watchThread != null) {
                watchThread.interrupt();
                watchThread = null;
            }
            
            if (watchService != null) {
                watchService.close();
                watchService = null;
            }
            
            log.info("✅ 監控已停止");
            return true;
            
        } catch (Exception e) {
            log.error("停止監控失敗: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 獲取監控狀態
     */
    public boolean isWatching() {
        return isWatching.get();
    }
    
    /**
     * 處理圖片
     */
    private void processImage(Path imagePath) {
        try {
            log.info("🔄 開始處理圖片: {}", imagePath.getFileName());
            
            // 讀取圖片
            BufferedImage image = ImageIO.read(imagePath.toFile());
            
            if (image == null) {
                log.error("❌ 無法讀取圖片: {}", imagePath);
                return;
            }
            
            log.info("✅ 圖片讀取成功，開始 OCR...");
            
            // 執行 OCR
            OcrResult result = ocrService.recognize(image, config.getDefaultLanguage());
            
            if (result == null) {
                log.error("❌ OCR 識別失敗");
                return;
            }
            
            log.info("✅ OCR 完成，開始導出文件...");
            
            // 生成時間戳
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            String baseName = getBaseName(imagePath.getFileName().toString());
            String outputBase = config.getOutputFolder() + "/" + baseName + "_" + timestamp;
            
            // 準備列表參數
            List<BufferedImage> images = java.util.Collections.singletonList(image);
            List<OcrResult> results = java.util.Collections.singletonList(result);
            
            // 導出 OFD
            String ofdPath = outputBase + ".ofd";
            ofdService.generateSearchableOfd(images, results, Paths.get(ofdPath));
            log.info("✅ OFD 已導出: {}", ofdPath);
            
            // 導出 PDF
            String pdfPath = outputBase + ".pdf";
            pdfService.generateSearchablePdf(images, results, Paths.get(pdfPath));
            log.info("✅ PDF 已導出: {}", pdfPath);
            
            // 導出文字
            String txtPath = outputBase + ".txt";
            textService.exportText(results, Paths.get(txtPath));
            log.info("✅ TXT 已導出: {}", txtPath);
            
            // 移動已處理的圖片到 Save 文件夾
            Path savePath = Paths.get(config.getSaveFolder(), imagePath.getFileName().toString());
            Files.createDirectories(savePath.getParent());
            Files.move(imagePath, savePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("✅ 處理完成，圖片已移動到: {}", savePath);
            
        } catch (Exception e) {
            log.error("❌ 處理圖片失敗: {}", e.getMessage(), e);
            
            // 錯誤處理：移動到錯誤文件夾（可選）
            try {
                Path errorPath = Paths.get(config.getSaveFolder(), "errors", imagePath.getFileName().toString());
                Files.createDirectories(errorPath.getParent());
                Files.move(imagePath, errorPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("錯誤圖片已移動到: {}", errorPath);
            } catch (Exception moveError) {
                log.error("移動錯誤圖片失敗: {}", moveError.getMessage());
            }
        }
    }
    
    /**
     * 導出 OFD（已廢棄，直接調用 ofdService）
     */
    @Deprecated
    private void exportOfd(OcrResult result, String outputPath) throws Exception {
        // 這裡調用實際的 OFD 導出邏輯
        // 由於這是簡化版，實際需要調用 OfdLayoutDirectServiceImpl
        log.info("導出 OFD: {}", outputPath);
        
        // TODO: 調用實際的 OFD 導出服務
    }
    
    /**
     * 判斷是否為圖片文件
     */
    private boolean isImageFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") || 
               lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") ||
               lowerName.endsWith(".bmp") ||
               lowerName.endsWith(".tiff");
    }
    
    /**
     * 獲取文件基本名稱（無副檔名）
     */
    private String getBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
