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
            log.info("🔄 處理圖片: {}", imagePath.getFileName());
            
            // 讀取圖片
            BufferedImage image = ImageIO.read(imagePath.toFile());
            
            if (image == null) {
                log.error("無法讀取圖片: {}", imagePath);
                return;
            }
            
            // 執行 OCR
            OcrResult result = ocrService.recognize(image, config.getDefaultLanguage());
            
            // 導出為默認格式（OFD）
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            String baseName = getBaseName(imagePath.getFileName().toString());
            String outputPath = config.getOutputFolder() + "/" + baseName + "_" + timestamp;
            
            // 導出 OFD
            exportOfd(result, outputPath + ".ofd");
            
            // 移動已處理的圖片
            Path savePath = Paths.get(config.getSaveFolder(), imagePath.getFileName().toString());
            Files.createDirectories(savePath.getParent());
            Files.move(imagePath, savePath, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("✅ 處理完成: {}", savePath.getFileName());
            
        } catch (Exception e) {
            log.error("處理圖片失敗: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 導出 OFD
     */
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
