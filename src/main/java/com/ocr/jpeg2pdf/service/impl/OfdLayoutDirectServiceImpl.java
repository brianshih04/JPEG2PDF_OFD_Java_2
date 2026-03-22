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
                        // 1. ⭐️ 关键修正 1：去除 OCR 文字头尾的隐形空白，避免字距被吃掉！
                        String text = tp.getText().trim();
                        
                        // 2. 取得 OCR 边界框
                        double ocrX = tp.getX() * 25.4 / 72.0;
                        double ocrY = tp.getY() * 25.4 / 72.0;
                        double ocrW = tp.getWidth() * 25.4 / 72.0;
                        double ocrH = tp.getHeight() * 25.4 / 72.0;
                        
                        // 3. 字号保持 0.75 完美比例
                        double fontSizeMm = ocrH * 0.75;
                        float fontSizePt = (float) (fontSizeMm * 72.0 / 25.4);
                        
                        // 3. 使用 SERIF（衬线体），最符合这份底图的英文字型特征
                        java.awt.Font awtFont = new java.awt.Font(java.awt.Font.SERIF, java.awt.Font.PLAIN, 1)
                            .deriveFont(fontSizePt);
                        java.awt.font.FontRenderContext frc = new java.awt.font.FontRenderContext(null, true, true);
                        
                        // 3. Y 轴保留完美公式
                        double ascentPt = awtFont.getLineMetrics(text, frc).getAscent();
                        double ascentMm = ascentPt * 25.4 / 72.0;
                        double paragraphY = (ocrY + (ocrH * 0.72)) - ascentMm;
                        
                        // =========================================================
                        // 4. ⭐ 终极杀手锏：以字为单位的精准坐标计算
                        
                        // 4.1 先分别计算出每一个单一字母的 AWT 物理宽度
                        double[] charWidthsMm = new double[text.length()];
                        double totalAwtWidthMm = 0;
                        
                        for (int charIdx = 0; charIdx < text.length(); charIdx++) {
                            String singleChar = String.valueOf(text.charAt(charIdx));
                            double wPt = awtFont.getStringBounds(singleChar, frc).getWidth();
                            
                            // 防呆：如果遇到空白，AWT 有时会回传 0，我们强制给予宽度
                            if (singleChar.equals(" ") && wPt == 0) {
                                wPt = fontSizePt * 0.3;
                            }
                            
                            double wMm = wPt * 25.4 / 72.0;
                            charWidthsMm[charIdx] = wMm;
                            totalAwtWidthMm += wMm;
                        }
                        
                        // 4.2 计算完美的「目标分配比例」
                        // 也就是 OCR 给的总宽度，除以我们算出来的总宽度
                        double scaleX = 1.0;
                        if (totalAwtWidthMm > 0) {
                            scaleX = ocrW / totalAwtWidthMm;
                        }
                        
                        // 4.3 逐字强制绘制：剥夺 OFD 引擎的排版权，我们自己排！
                        double currentX = ocrX;
                        
                        for (int charIdx = 0; charIdx < text.length(); charIdx++) {
                            String singleChar = String.valueOf(text.charAt(charIdx));
                            
                            Span span = new Span(singleChar);
                            span.setFontSize(fontSizeMm);
                            span.setColor(255, 255, 255); // 白色（最终版本）
                            
                            Paragraph p = new Paragraph();
                            p.add(span);
                            p.setPosition(org.ofdrw.layout.element.Position.Absolute);
                            p.setMargin(0d);
                            p.setPadding(0d);
                            p.setLineSpace(0d);
                            p.setWidth(charWidthsMm[charIdx] * scaleX + 10.0); // 确保单字绝对不换行
                            
                            // ⭐ 强制指定这个字的 X 与 Y
                            p.setX(currentX);
                            p.setY(paragraphY);
                            
                            // ⭐ 1% 透明度（WPS 兼容）
                            p.setOpacity(0.01);
                            
                            vPage.add(p);
                            
                            // ⭐ 坐标推进：把当前字的宽度 (乘上比例后) 加上去，作为下一个字的起点
                            currentX += (charWidthsMm[charIdx] * scaleX);
                        }
                        // =========================================================
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
