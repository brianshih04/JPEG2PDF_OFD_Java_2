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
                
                // 添加不可见文字层（终极方案：LetterSpacing 模拟 DeltaX）
                int textCount = 0;
                for (OcrResult.TextPosition tp : ocrResult.getTextPositions()) {
                    try {
                        // 转换坐标：像素 -> mm
                        double ocrWidthMm = tp.getWidth() * 25.4 / 72.0;
                        double ocrHeightMm = tp.getHeight() * 25.4 / 72.0;
                        double x_mm = tp.getX() * 25.4 / 72.0;
                        double y_mm = tp.getY() * 25.4 / 72.0;
                        String text = tp.getText();
                        
                        // ⭐️ 核心修正 1：字号系数 0.65
                        // 让字体稍微饱满一点，更贴合原字
                        double fontSizeMm = ocrHeightMm * 0.65; // 从 0.6 改为 0.65
                        
                        // 1. 计算 AWT 标准宽度
                        java.awt.Font awtFont = new java.awt.Font("SimSun", java.awt.Font.PLAIN, 12)
                            .deriveFont((float)(fontSizeMm * 72 / 25.4));
                        java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, true, true);
                        double awtWidthMm = awtFont.getStringBounds(text, frc).getWidth() * 25.4 / 72;
                        
                        // 2. ⭐️ 终极修正 1：扣除 OCR 边距，防止字体被过度拉宽
                        // PaddleOCR 检测的宽度包含左右背景边距（Padding）
                        // 将 OCR 宽度打 95 折，模拟真实字迹的宽度
                        double actualTargetWidth = ocrWidthMm * 0.95; // 关键修正！
                        
                        double letterSpacing = 0;
                        if (text.length() > 1) {
                            letterSpacing = (actualTargetWidth - awtWidthMm) / Math.max(1, text.length() - 1);
                        }
                        
                        // 3. 创建文字片段（先用红色测试对齐）
                        Span span = new Span(text);
                        span.setFontSize(fontSizeMm);
                        span.setLetterSpacing(letterSpacing); // 解决 X 轴不对齐的关键
                        span.setColor(255, 0, 0); // 红色（测试对齐）
                        
                        // 4. 创建段落
                        Paragraph p = new Paragraph();
                        p.add(span);
                        p.setPosition(Position.Absolute);
                        
                        // 5. 归零所有 Layout 引擎的干扰
                        p.setMargin(0d);
                        p.setPadding(0d);
                        p.setLineSpace(0d);
                        
                        // 6. 给超大宽度，绝对不换行
                        p.setWidth(ocrWidthMm + 100.0);
                        
                        // 7. ⭐️ 终极修正 2：再往下推一点
                        // 从 0.18 改为 0.25，把微偏高的红字压回黑字的正上方
                        // 原因：中文字型和英文字型的内部 Ascent 计算差异
                        double yOffset = ocrHeightMm * 0.25; // 从 0.18 改为 0.25
                        p.setX(x_mm);
                        p.setY(y_mm + yOffset);
                        
                        // 8. 先用完全不透明测试对齐
                        p.setOpacity(1.0); // 完全可见（调试）
                        // 确认对齐后改为: p.setOpacity(0.01);
                        // 确认对齐后改为: span.setColor(255, 255, 255);
                        
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
