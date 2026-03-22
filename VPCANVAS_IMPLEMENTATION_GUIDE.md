# OFD VPCanvas 完整实现方案

## 用户提供的专业级实现代码

### 核心代码

```java
import org.ofdrw.core.basicType.ST_Box;
import org.ofdrw.doc.OFDDoc;
import org.ofdrw.font.Font;
import org.ofdrw.layout.VirtualPage;
import org.ofdrw.layout.edit.VPCanvas;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OfdSearchableGenerator {

    private String fontPath = "src/main/resources/fonts/SourceHanSerif-Regular.ttf";
    private String fontName = "Source Han Serif";

    public void createSearchableOfd(String imagePath, List<OCRResult> ocrResults, String outputPath) throws Exception {
        Path fontFile = Paths.get(fontPath);
        
        try (OFDDoc ofdDoc = new OFDDoc(Paths.get(outputPath))) {
            // 1. 注册字体
            Font ofdFont = new Font(fontName, fontName, fontFile);
            ofdDoc.addFont(ofdFont);

            VirtualPage page = new VirtualPage(ofdDoc.getPageLayout());
            VPCanvas canvas = new VPCanvas(page.getWidth(), page.getHeight());

            // 2. 画底图
            canvas.drawImage(Paths.get(imagePath), 0, 0, page.getWidth(), page.getHeight());

            // 3. 设置文字属性（WPS 索引关键：Alpha=1 的几乎透明色）
            canvas.setFillColor(255, 255, 255, 1);

            for (OCRResult ocr : ocrResults) {
                double fontSizeMm = ocr.getHeightMm();
                double targetWidthMm = ocr.getWidthMm();
                
                // 4. AWT 精确测量 Ascent
                java.awt.Font awtFont = java.awt.Font.createFont(
                    java.awt.Font.TRUETYPE_FONT, 
                    fontFile.toFile()
                ).deriveFont((float) (fontSizeMm * 72 / 25.4));
                
                FontRenderContext frc = new FontRenderContext(null, true, true);
                LineMetrics lm = awtFont.getLineMetrics(ocr.getText(), frc);
                
                // 计算完美的 Baseline Y
                double ascentMm = lm.getAscent() * 25.4 / 72;
                double finalBaselineY = ocr.getY() + ascentMm;

                // 5. 计算动态 DeltaX（避免 X 轴偏移）
                List<Double> deltaX = calculateDeltaX(
                    ocr.getText(), 
                    targetWidthMm, 
                    awtFont, 
                    frc
                );

                // 6. 绘制隐形文字
                canvas.setFont(fontName, fontSizeMm);
                canvas.fillText(ocr.getText(), ocr.getX(), finalBaselineY, deltaX);
            }

            page.add(canvas);
            ofdDoc.addPage(page);
        }
    }
    
    /**
     * 动态计算 DeltaX
     */
    private List<Double> calculateDeltaX(
        String text, 
        double targetWidthMm, 
        java.awt.Font awtFont, 
        FontRenderContext frc
    ) {
        List<Double> deltaX = new ArrayList<>();
        
        double totalWidth = 0;
        double[] charWidths = new double[text.length()];
        
        // 测量每个字符宽度
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));
            double w = awtFont.getStringBounds(ch, frc).getWidth() * 25.4 / 72;
            charWidths[i] = w;
            totalWidth += w;
        }
        
        // 计算缩放因子
        double scaleFactor = targetWidthMm / totalWidth;
        
        // 生成 DeltaX 序列
        for (int i = 0; i < text.length() - 1; i++) {
            deltaX.add(charWidths[i] * scaleFactor);
        }
        
        return deltaX;
    }
}
```

---

## 关键细节检查表

### ✅ Alpha 值 (1/255)
- **关键**: 让 WPS 搜索引擎"上当"
- 如果设为 0，WPS 会无视它
- 设为 1，它是"可见对象"，但人眼看不出

### ✅ DeltaX 序列
- `canvas.fillText` 的第四个参数 `List<Double>`
- OFDRW 自动转换为国标规定的 DeltaX 语法
- 让选区每个字符都精准对齐

### ✅ 字体嵌入
- `ofdDoc.addFont(ofdFont)` 将字体打包进 OFD
- 增加文件大小约 2-5MB
- 保证跨平台搜索位置不跑偏

---

## 调试小撇步

### 步骤 1: 红色测试
```java
// 先用纯红色
canvas.setFillColor(255, 0, 0, 255);
```

### 步骤 2: 检查对齐
- 生成 OFD 后，用系统放大镜看文字边缘
- 如果红色的 "possibly" 完美套在图片的 "possibly" 上
- 说明 Ascent 和 DeltaX 逻辑完美

### 步骤 3: 改为透明
```java
// 测试通过后，改为极低透明度
canvas.setFillColor(255, 255, 255, 1); // Alpha=1/255
```

---

## 字体选择

### 推荐字体
1. **思源宋体** (Source Han Serif) - 最推荐
2. **思源黑体** (Source Han Sans)
3. **微软正黑体** (msjh.ttc → 转为 ttf)
4. **教育部标准楷书**

### 为什么选择思源宋体？
- 开源免费
- 字符集极全（繁简中、日、韩）
- 字体度量非常标准
- 不容易产生偏移

---

## 完整实现计划

```
1. 下载思源宋体 (5分钟)
2. 创建 VPCanvas 实现 (30分钟)
3. 实现 AWT Ascent 计算 (15分钟)
4. 实现动态 DeltaX (20分钟)
5. 红色调试测试 (10分钟)
6. 改为 Alpha=1 (5分钟)
7. 最终测试 (15分钟)

总计: 约 1.5 小时
```

---

## 优点

- ✅ 透明但可搜索（WPS 兼容）
- ✅ 精确的字符间距（DeltaX）
- ✅ 精确的 Baseline（AWT Ascent）
- ✅ 完全符合国标 GB/T 33190-2016
- ✅ 跨平台一致性（字体嵌入）

---

## 参考资料

- 思源宋体下载: Google Fonts
- OFD 标准: GB/T 33190-2016
- OFDRW 文档: https://github.com/ofdrw/ofdrw

---

**专业级 OFD 透明搜索层实现方案！** 🚀
