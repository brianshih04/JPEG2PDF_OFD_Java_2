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
                        // 1. 取得 OCR 边界框 (确保全部转换为 mm)
                        double ocrX = tp.getX() * 25.4 / 72.0;
                        double ocrY = tp.getY() * 25.4 / 72.0;
                        double ocrW = tp.getWidth() * 25.4 / 72.0;
                        double ocrH = tp.getHeight() * 25.4 / 72.0;
                        String text = tp.getText();
                        
                        // 2. 设定字号 (0.75 这个高度在截图里看起来非常完美，保持不变)
                        double fontSizeMm = ocrH * 0.75;
                        float fontSizePt = (float) (fontSizeMm * 72.0 / 25.4);
                        
                        // =========================================================
                        // 3. ⭐️ 修正 1：换掉 SimSun！改用标准衬线体 (SERIF)
                        // 这能确保英文字母的宽度测量准确，不会误判导致字距缩水
                        // SimSun 的英文字母宽度非常宽，导致 AWT 测量误以为"已经够宽"
                        // 改用 SERIF 后，awtWidthMm 会变小，letterSpacing 会变大，红字会撑开！
                        java.awt.Font awtFont = new java.awt.Font(java.awt.Font.SERIF, java.awt.Font.PLAIN, 1)
                            .deriveFont(fontSizePt);
                        java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, true, true);
                        
                        double awtWidthPt = awtFont.getStringBounds(text, frc).getWidth();
                        double awtWidthMm = awtWidthPt * 25.4 / 72.0;
                        
                        double ascentPt = awtFont.getLineMetrics(text, frc).getAscent();
                        double ascentMm = ascentPt * 25.4 / 72.0;
                        // =========================================================
                        
                        // 4. X 轴：精确字距计算 (不打折，还原真实 OCR 宽度)
                        double letterSpacing = 0;
                        if (text.length() > 1) {
                            // 现在 awtWidthMm 变准了，算出来的 letterSpacing 会变大，把红字精准撑开！
                            letterSpacing = (ocrW - awtWidthMm) / (text.length() - 1);
                            
                            // ⭐️ 防呆机制：如果 OCR 框真的很窄，至少不要让字挤成一团
                            if (letterSpacing < -0.5) {
                                letterSpacing = -0.5;
                            }
                        }
                        
                        // 5. Y 轴：抛弃魔法数字，使用科学对齐
                        // ⭐️ 修正 2：Y 轴微微上提（0.80 → 0.76）
                        // 将 0.80 改为 0.76，把稍微下沉的红字拉回正确的基准线上
                        // 对于英文字体，基准线通常在框高的 0.75 ~ 0.76 左右
                        double ocrBaselineY = ocrY + (ocrH * 0.76);
                        // Paragraph 需要的 Y = 基准线 - Ascent
                        double paragraphY = ocrBaselineY - ascentMm;
                        
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
                        p.setWidth(ocrW + 100.0);
                        
                        // 7. 设置 X、Y 座标
                        p.setX(ocrX);
                        p.setY(paragraphY);
                        
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
