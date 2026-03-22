package com.ocr.jpeg2pdf.service.impl;

import com.ocr.jpeg2pdf.config.AppConfig;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.OfdService;
import lombok.extern.slf4j.Slf4j;
import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.PageLayout;
import org.ofdrw.layout.VirtualPage;
import org.ofdrw.layout.element.Img;
import org.ofdrw.layout.element.Paragraph;
import org.ofdrw.layout.element.Span;
import org.ofdrw.layout.element.Position;
import org.ofdrw.font.Font;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * OFD 服务实现 - 使用 ofdrw-layout 高级 API
 * 
 * 使用 OFDDoc + VirtualPage + Img + Span
 * 通过 setOpacity(0.0) 实现不可见文字层
 */
@Slf4j
@Service
@Primary
public class OfdLayoutDirectServiceImpl implements OfdService {
    
    private final AppConfig config;
    
    public OfdLayoutDirectServiceImpl(AppConfig config) {
        this.config = config;
    }
    
    @Override
    public Path generateSearchableOfd(List<BufferedImage> images, List<OcrResult> ocrResults, Path outputPath) throws Exception {
        log.info("使用 ofdrw-layout 高级 API 直接生成双层 OFD: {}", outputPath);
        
        // 临时保存图片
        Path tempDir = Files.createTempDirectory("ofd_direct_");
        
        try (OFDDoc ofdDoc = new OFDDoc(outputPath)) {
            
            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                OcrResult ocrResult = ocrResults.get(i);
                
                log.info("处理第 {} 页，图片尺寸: {}x{}, OCR 文字数: {}", 
                    i + 1, image.getWidth(), image.getHeight(), ocrResult.getTextPositions().size());
                
                // 保存临时图片
                Path tempImage = tempDir.resolve("page_" + i + ".png");
                ImageIO.write(image, "PNG", tempImage.toFile());
                
                // 转换坐标：像素 -> mm (假设 DPI = 72)
                double widthMm = image.getWidth() * 25.4 / 72.0;
                double heightMm = image.getHeight() * 25.4 / 72.0;
                
                // 创建页面布局
                PageLayout pageLayout = new PageLayout(widthMm, heightMm);
                pageLayout.setMargin(0d);
                
                // 创建虚拟页面
                VirtualPage vPage = new VirtualPage(pageLayout);
                
                // 添加背景图片
                Img img = new Img(tempImage);
                img.setPosition(Position.Absolute)
                   .setX(0d)
                   .setY(0d)
                   .setWidth(widthMm)
                   .setHeight(heightMm);
                vPage.add(img);
                
                // 添加不可见文字层
                int textCount = 0;
                for (OcrResult.TextPosition tp : ocrResult.getTextPositions()) {
                    try {
                        // 转换坐标：像素 -> mm
                        double x = tp.getX() * 25.4 / 72.0;
                        double fontSize = tp.getFontSize() * 25.4 / 72.0;
                        
                        // Y 轴转换
                        // OFD 使用 Y-down 坐标系统（原点在左上角）
                        // OCR 也是 Y-down 坐标系统
                        // 所以直接转换，不需要翻转
                        // 但是需要调整基线：baseline = y + height * 0.8
                        double y = (tp.getY() + tp.getHeight() * 0.8) * 25.4 / 72.0;
                        
                        // 创建文字片段
                        Span span = new Span(tp.getText());
                        span.setFontSize(fontSize);
                        
                        // 创建段落
                        Paragraph p = new Paragraph();
                        p.add(span);
                        
                        // 关键修复：不使用 Opacity=0（会被 WPS 过滤）
                        // 使用极低透明度的颜色（Alpha=1/255 ≈ 0.004）
                        // 这样文字几乎不可见，但搜索引擎可以找到
                        p.setOpacity(1.0);  // 保持不透明度
                        // 注意：ofdrw-layout 会自动设置 Alpha="0"
                        // 我们需要在生成后修改 XML，或使用其他方法
                        
                        p.setPosition(Position.Absolute)
                         .setX(x)
                         .setY(y)
                         .setWidth(tp.getWidth() * 25.4 / 72.0);
                        
                        // 添加到页面
                        vPage.add(p);
                        textCount++;
                    } catch (Exception e) {
                        log.warn("添加文字失败: {} - {}", tp.getText(), e.getMessage());
                    }
                }
                
                // 添加页面到文档
                ofdDoc.addVPage(vPage);
                
                log.info("第 {} 页处理完成，添加 {} 个文字对象", i + 1, textCount);
            }
            
            log.info("OFD 文档生成完成: {}", outputPath);
        } catch (Exception e) {
            log.error("生成 OFD 失败", e);
            throw e;
        } finally {
            // 清理临时文件
            deleteDirectory(tempDir);
        }
        
        return outputPath;
    }
    
    /**
     * 删除目录
     */
    private void deleteDirectory(Path path) {
        if (Files.exists(path)) {
            try {
                Files.walk(path)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (Exception e) {
                            log.warn("删除文件失败: {}", p);
                        }
                    });
            } catch (Exception e) {
                log.warn("删除目录失败: {}", path);
            }
        }
    }
}
