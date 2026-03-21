package com.ocr.jpeg2pdf.controller;

import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.model.ImageInfo;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.OcrService;
import com.ocr.jpeg2pdf.service.PdfService;
import com.ocr.jpeg2pdf.service.OfdService;
import com.ocr.jpeg2pdf.service.OfdPostProcessService;
import com.ocr.jpeg2pdf.service.impl.OfdrwServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * OCR 轉換控制器
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OcrController {
    
    private final OcrService ocrService;
    private final PdfService pdfService;
    private final OfdService ofdService;
    private final OfdPostProcessService ofdPostProcessService;
    private final AppConfig appConfig;  // 注入配置
    
    // 暫存已上傳的圖片
    private final Map<String, ImageInfo> imageStore = new HashMap<>();
    
    /**
     * 上傳圖片
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImages(
        @RequestParam("files") MultipartFile[] files
    ) {
        log.info("上傳 {} 個檔案", files.length);
        
        List<ImageInfo> images = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                String imageId = UUID.randomUUID().toString();
                String originalFilename = file.getOriginalFilename();
                
                // 儲存到暫存目錄（跨平台）
                Path uploadDir = Paths.get(appConfig.getUploadFolder());
                if (!uploadDir.toFile().exists()) {
                    uploadDir.toFile().mkdirs();
                }
                
                Path savePath = uploadDir.resolve(imageId + getFileExtension(originalFilename));
                file.transferTo(savePath.toFile());
                
                // 讀取圖片資訊
                BufferedImage image = javax.imageio.ImageIO.read(savePath.toFile());
                
                ImageInfo imageInfo = ImageInfo.builder()
                    .id(imageId)
                    .originalFilename(originalFilename)
                    .savedFilename(savePath.getFileName().toString())
                    .path(savePath.toString())
                    .width(image.getWidth())
                    .height(image.getHeight())
                    .createdAt(LocalDateTime.now())
                    .build();
                
                imageStore.put(imageId, imageInfo);
                images.add(imageInfo);
                
                log.info("圖片已儲存: {} -> {}", originalFilename, imageId);
                
            } catch (Exception e) {
                log.error("上傳失敗: {}", file.getOriginalFilename(), e);
                errors.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", errors.isEmpty());
        response.put("uploaded_count", images.size());
        response.put("error_count", errors.size());
        response.put("images", images);
        response.put("errors", errors.isEmpty() ? null : errors);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 執行 OCR
     */
    @PostMapping("/ocr")
    public ResponseEntity<Map<String, Object>> performOcr(
        @RequestBody Map<String, Object> request
    ) {
        @SuppressWarnings("unchecked")
        List<String> imageIds = (List<String>) request.get("image_ids");
        String language = (String) request.getOrDefault("language", "chinese_cht");
        
        log.info("執行 OCR: {} 張圖片, 語言: {}", imageIds.size(), language);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (String imageId : imageIds) {
            try {
                ImageInfo imageInfo = imageStore.get(imageId);
                if (imageInfo == null) {
                    throw new RuntimeException("圖片不存在: " + imageId);
                }
                
                // 讀取圖片
                BufferedImage image = javax.imageio.ImageIO.read(
                    new File(imageInfo.getPath())
                );
                
                // 執行 OCR
                OcrResult ocrResult = ocrService.recognize(image, language);
                
                // 儲存 OCR 結果
                imageInfo.setOcrResult(ocrResult);
                
                Map<String, Object> result = new HashMap<>();
                result.put("image_id", imageId);
                result.put("success", true);
                result.put("text", ocrResult.getText());
                result.put("confidence", ocrResult.getConfidence());
                result.put("line_count", ocrResult.getLineCount());
                result.put("text_positions", ocrResult.getTextPositions());
                
                results.add(result);
                
                log.info("OCR 完成: {}, {} 行, 信心度: {}", 
                    imageId, ocrResult.getLineCount(), ocrResult.getConfidence());
                
            } catch (Exception e) {
                log.error("OCR 失敗: {}", imageId, e);
                
                Map<String, Object> result = new HashMap<>();
                result.put("image_id", imageId);
                result.put("success", false);
                result.put("error", e.getMessage());
                
                results.add(result);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", results.stream().allMatch(r -> Boolean.TRUE.equals(r.get("success"))));
        response.put("results", results);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 匯出檔案
     */
    @PostMapping("/export")
    public ResponseEntity<Map<String, Object>> exportFile(
        @RequestBody Map<String, Object> request
    ) {
        @SuppressWarnings("unchecked")
        List<String> imageIds = (List<String>) request.get("image_ids");
        String format = (String) request.get("format");
        String language = (String) request.getOrDefault("language", "chinese_cht");
        
        log.info("匯出: {} 張圖片, 格式: {}", imageIds.size(), format);
        
        try {
            // 收集圖片和 OCR 結果
            List<BufferedImage> images = new ArrayList<>();
            List<OcrResult> ocrResults = new ArrayList<>();
            
            for (String imageId : imageIds) {
                ImageInfo imageInfo = imageStore.get(imageId);
                if (imageInfo == null) {
                    throw new RuntimeException("圖片不存在: " + imageId);
                }
                
                BufferedImage image = javax.imageio.ImageIO.read(
                    new File(imageInfo.getPath())
                );
                images.add(image);
                
                OcrResult ocrResult = imageInfo.getOcrResult();
                if (ocrResult != null) {
                    log.info("圖片 {} OCR 結果: {} 行, textPositions: {}", 
                        imageId, 
                        ocrResult.getLineCount(),
                        ocrResult.getTextPositions() != null ? ocrResult.getTextPositions().size() : "null");
                } else {
                    log.warn("圖片 {} 沒有 OCR 結果！", imageId);
                }
                
                ocrResults.add(ocrResult);
            }
            
            // 生成輸出檔案名稱（跨平台）
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            );
            String extension = getExtension(format);
            String filename = "output_" + timestamp + extension;
            
            // 確保輸出目錄存在
            Path outputDir = Paths.get(appConfig.getOutputFolder());
            if (!outputDir.toFile().exists()) {
                outputDir.toFile().mkdirs();
            }
            
            Path outputPath = outputDir.resolve(filename);
            
            // 根據格式匯出
            switch (format) {
                case "searchable_pdf":
                    pdfService.generateSearchablePdf(images, ocrResults, outputPath);
                    break;
                case "searchable_ofd":
                    // 步骤 1: 先生成 PDF（可见文字）
                    Path tempPdf = outputDir.resolve("temp_" + timestamp + ".pdf");
                    
                    // 使用可见文字的 PDF（用于 OFD 转换）
                    pdfService.generateSearchablePdfForOfd(images, ocrResults, tempPdf);
                    
                    // 步骤 2: 转换为 OFD（只有图片）
                    Path tempOfd = outputDir.resolve("temp_" + timestamp + ".ofd");
                    if (ofdService instanceof OfdrwServiceImpl) {
                        ((OfdrwServiceImpl) ofdService).convertPdfToOfd(tempPdf, tempOfd);
                        
                        // 步骤 3: 添加文字层
                        ofdPostProcessService.addTextLayerToOfd(tempOfd, ocrResults, outputPath);
                        
                        // 步骤 4: 删除临时文件
                        Files.deleteIfExists(tempPdf);
                        Files.deleteIfExists(tempOfd);
                    } else {
                        throw new RuntimeException("OFD 服务类型不正确");
                    }
                    break;
                case "text":
                    exportText(ocrResults, outputPath);
                    break;
                default:
                    throw new RuntimeException("不支援的格式: " + format);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("output_file", outputPath.toString());
            response.put("format", format);
            response.put("page_count", images.size());
            response.put("message", "已成功匯出 " + images.size() + " 頁");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("匯出失敗", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 取得支援的匯出格式
     */
    @GetMapping("/export/formats")
    public ResponseEntity<Map<String, Object>> getExportFormats() {
        List<Map<String, Object>> formats = Arrays.asList(
            Map.of(
                "id", "searchable_pdf",
                "name", "Searchable PDF",
                "description", "可搜尋的 PDF 檔案，包含透明文字層",
                "extension", ".pdf"
            ),
            Map.of(
                "id", "searchable_ofd",
                "name", "Searchable OFD",
                "description", "可搜尋的 OFD 檔案，符合 GB/T 33190-2016 標準",
                "extension", ".ofd"
            ),
            Map.of(
                "id", "text",
                "name", "純文字",
                "description", "純文字檔案",
                "extension", ".txt"
            )
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("formats", formats);
        response.put("default", "searchable_pdf");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康檢查
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "JPEG2PDF_OFD_Java");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("description", "圖片 OCR 轉換工具");
        return ResponseEntity.ok(response);
    }
    
    // === 工具方法 ===
    
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex >= 0 ? filename.substring(dotIndex) : "";
    }
    
    private String getExtension(String format) {
        return switch (format) {
            case "searchable_pdf" -> ".pdf";
            case "searchable_ofd" -> ".ofd";
            case "text" -> ".txt";
            default -> ".bin";
        };
    }
    
    private void exportText(List<OcrResult> ocrResults, Path outputPath) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("OCR 辨識結果\n");
        sb.append("============================================================\n\n");
        
        for (int i = 0; i < ocrResults.size(); i++) {
            OcrResult result = ocrResults.get(i);
            sb.append("--- 第 ").append(i + 1).append(" 頁 ---\n");
            sb.append("圖片: ").append(result.getImageId()).append("\n\n");
            sb.append(result.getText()).append("\n\n");
        }
        
        java.nio.file.Files.writeString(outputPath, sb.toString());
    }
}
