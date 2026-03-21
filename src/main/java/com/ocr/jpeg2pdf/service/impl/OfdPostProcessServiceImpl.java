package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.OfdPostProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * OFD 后处理服务实现 - 在 OFD 中添加文字层
 */
@Slf4j
@Service
public class OfdPostProcessServiceImpl implements OfdPostProcessService {
    
    @Override
    public Path addTextLayerToOfd(Path ofdPath, List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.info("开始处理 OFD 文件，添加文字层: {}", ofdPath);
        
        // 创建临时目录
        Path tempDir = Files.createTempDirectory("ofd_process");
        log.debug("临时目录: {}", tempDir);
        
        try {
            // 1. 解压 OFD
            unzipOfd(ofdPath, tempDir);
            
            // 2. 处理每一页的文字
            for (int i = 0; i < ocrResults.size(); i++) {
                OcrResult ocrResult = ocrResults.get(i);
                if (ocrResult != null && ocrResult.getTextPositions() != null) {
                    processPage(tempDir, i, ocrResult);
                }
            }
            
            // 3. 重新打包 OFD
            zipOfd(tempDir, outputPath);
            
            log.info("OFD 处理完成: {}", outputPath);
            return outputPath;
            
        } finally {
            // 清理临时目录
            deleteDirectory(tempDir);
        }
    }
    
    /**
     * 解压 OFD 文件
     */
    private void unzipOfd(Path ofdPath, Path targetDir) throws IOException {
        log.debug("解压 OFD: {}", ofdPath);
        
        try (ZipFile zipFile = new ZipFile(ofdPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = targetDir.resolve(entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        Files.copy(is, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        
        log.debug("解压完成");
    }
    
    /**
     * 处理单个页面
     */
    private void processPage(Path tempDir, int pageIndex, OcrResult ocrResult) throws IOException {
        log.info("处理第 {} 页，添加 {} 个文字", pageIndex + 1, ocrResult.getTextPositions().size());
        
        // 读取 Content.xml
        Path contentPath = tempDir.resolve("Doc_0/Pages/Page_" + pageIndex + "/Content.xml");
        if (!Files.exists(contentPath)) {
            log.warn("页面文件不存在: {}", contentPath);
            return;
        }
        
        String content = Files.readString(contentPath, StandardCharsets.UTF_8);
        
        // 修改 Content.xml，添加文字层
        String modifiedContent = addTextLayer(content, ocrResult);
        
        // 写回文件
        Files.writeString(contentPath, modifiedContent, StandardCharsets.UTF_8);
        
        log.debug("页面 {} 处理完成", pageIndex + 1);
    }
    
    /**
     * 在 Content.xml 中添加文字层
     */
    private String addTextLayer(String content, OcrResult ocrResult) {
        StringBuilder textLayer = new StringBuilder();
        
        textLayer.append("<ofd:Layer Type=\"Foreground\" ID=\"100\">\n");
        
        int objectId = 1000;
        for (OcrResult.TextPosition tp : ocrResult.getTextPositions()) {
            // 转换坐标（像素 -> mm）
            // OFD 使用毫米，假设 DPI = 72
            float x = (float) tp.getX() * 25.4f / 72.0f;  // pixel to mm
            float y = 297.0f - ((float) tp.getY() * 25.4f / 72.0f);  // 翻转 Y 轴（A4 高度 297mm）
            float width = (float) tp.getWidth() * 25.4f / 72.0f;
            float height = (float) tp.getHeight() * 25.4f / 72.0f;
            float fontSize = (float) tp.getFontSize() * 25.4f / 72.0f;
            
            // 生成 TextObject (不可见文字层 - 使用 Visible="false")
            String text = escapeXml(tp.getText());
            // 使用 Visible="false" 实现不可见但可搜索的文字层（OFD 标准推荐方式）
            textLayer.append(String.format(
                "  <ofd:TextObject ID=\"%d\" Font=\"1\" Size=\"%.1f\" Boundary=\"%.1f %.1f %.1f %.1f\" Visible=\"false\">\n",
                objectId++, fontSize, x, y, width, height
            ));
            textLayer.append(String.format(
                "    <ofd:TextCode X=\"%.1f\" Y=\"%.1f\">%s</ofd:TextCode>\n",
                x, y, text
            ));
            textLayer.append("  </ofd:TextObject>\n");
        }
        
        textLayer.append("</ofd:Layer>\n");
        
        // 在 </ofd:Content> 之前插入文字层
        int insertPos = content.indexOf("</ofd:Content>");
        if (insertPos > 0) {
            content = content.substring(0, insertPos) + textLayer.toString() + content.substring(insertPos);
        }
        
        return content;
    }
    
    /**
     * 转义 XML 特殊字符
     */
    private String escapeXml(String text) {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
    
    /**
     * 重新打包 OFD 文件
     */
    private void zipOfd(Path sourceDir, Path outputPath) throws IOException {
        log.debug("打包 OFD: {}", outputPath);
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath.toFile()))) {
            Files.walk(sourceDir)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString().replace("\\", "/"));
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        log.error("打包文件失败: {}", path, e);
                    }
                });
        }
        
        log.debug("打包完成");
    }
    
    /**
     * 删除目录
     */
    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        log.warn("删除文件失败: {}", p, e);
                    }
                });
        }
    }
}
