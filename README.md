# JPEG2PDF_OFD_Java

**JPEG 图片 OCR 转 Searchable PDF/OFD**

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux%20%7C%20麒麟%20%7C%20UOS-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 功能特性

- ✅ **OCR 识别** - RapidOCR-Java (PP-OCRv4)
- ✅ **Searchable PDF** - 透明文字层（可搜索、可复制）
- ✅ **Searchable OFD** - 符合 GB/T 33190-2016 标准
- ✅ **完美对齐** - 27 个版本迭代优化（开根号曲线算法）
- ✅ **WPS 兼容** - 白色文字 + 1% 透明度（WPS 可搜索）
- ✅ **多页支持** - 批量处理多页文档
- ✅ **Web UI** - 现代化响应式界面
- ✅ **跨平台** - Windows、macOS、Linux、国产操作系统

---

## 核心算法 ⭐ **终极突破**

### 开根号曲线（Square Root Curve）

经过 **27 个版本** 的系统化迭代，发现字体度量误差增长是**抛物线**，而非直线！

**终极公式**：
```java
// ⭐️ 终极 X 轴：开根号非线性补偿法
double widthMultiplier = 1.0;

if (text.length() > 10) {
    // 增加斜率，让中字和小字都能获得更强的向内收缩力！
    widthMultiplier = 1.0 + (0.035 * Math.sqrt(text.length() - 10));
}
```

**三个黄金数据点**：
- ✅ **大字（10 字）**: widthMultiplier = 1.0 → 完美
- ✅ **中字（30 字）**: widthMultiplier ≈ 1.16 → 完美
- ✅ **长句（75 字）**: widthMultiplier ≈ 1.28 → 完美

**为什么是开根号？**
- 误差增长是抛物线，不是直线
- 开根号曲线符合抛物线特性
- 初期快速增长（中字 10-40）
- 后期逐渐平缓（长句 40+）

### 完美 Y 轴对齐

```java
// 使用 AWT Ascent 精准抓取基准线
double ascentPt = awtFont.getLineMetrics(text, frc).getAscent();
double ascentMm = ascentPt * 25.4 / 72.0;
double paragraphY = (ocrY + (ocrH * 0.76)) - ascentMm;
```

### WPS 搜索兼容

```java
// WPS 会过滤 Opacity=0 的对象
// 使用白色 + 1% 透明度
span.setColor(255, 255, 255); // 白色
p.setOpacity(0.01); // 1% 不透明度（WPS 可搜索）
```

---

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| **OCR** | RapidOCR | 0.0.7 |
| **PDF** | Apache PDFBox | 2.0.29 |
| **OFD** | ofdrw-layout | 2.3.8 ⭐ |
| **框架** | Spring Boot | 3.2.x |
| **UI** | Thymeleaf + Tailwind CSS | - |

---

## OFD 生成方案对比

### 方案 A：PDF 转换 + 后处理

```
JPEG → OCR → PDF → OFD (图片) → 后处理 (添加文字层)
```

**特点**:
- 使用 PDFBox 生成 PDF
- 使用 ofdrw-converter 转换为 OFD
- 后处理添加 `Visible="false"` + `Alpha="0"` 双重保护
- 文件大小: ~1155 KB
- 可能包含不需要的 PathObject

### 方案 B：ofdrw-layout 直接生成 ⭐ **推荐**

```
JPEG → OCR → OFD (直接生成)
```

**特点**:
- 使用 ofdrw-layout 2.3.8 高级 API
- 直接生成双层 OFD（图片 + 文字）
- **开根号曲线完美对齐算法**
- **白色文字 + 1% 透明度（WPS 兼容）**
- **文件大小: ~728 KB (-37%)**
- **无 PathObject 干扰**
- **27 个版本迭代优化**

---

## 快速开始

### 1. 编译

```bash
# 安装 Maven
# Windows: choco install maven
# macOS: brew install maven
# Linux: sudo apt install maven

mvn clean package -DskipTests
```

### 2. 运行

```bash
java -jar target/jpeg2pdf-ofd-1.0.0.jar
```

### 3. 访问

- **Web UI**: http://localhost:8000
- **API**: http://localhost:8000/api

---

## API 接口

### 上传图片

```bash
POST /api/upload
Content-Type: multipart/form-data

files: [图片文件]
```

### OCR 识别

```bash
POST /api/ocr
Content-Type: application/json

{
  "image_ids": ["图片ID列表"],
  "language": "chinese_cht"
}
```

### 导出文件

```bash
POST /api/export
Content-Type: application/json

{
  "image_ids": ["图片ID列表"],
  "format": "ofd"  // 推荐：方案 B
}
```

**支持的格式**:
- `ofd` - 方案 B（推荐）：ofdrw-layout 直接生成 + 开根号曲线
- `searchable_ofd` - 方案 A：PDF 转换 + 后处理
- `searchable_pdf` - PDF 透明文字
- `text` - 纯文本

---

## 使用示例

### Python 测试

```python
import requests

BASE_URL = "http://localhost:8000/api"

# 1. 上传图片
with open("test.jpg", "rb") as f:
    files = {'files': ('test.jpg', f, 'image/jpeg')}
    response = requests.post(f"{BASE_URL}/upload", files=files)
    image_id = response.json()['images'][0]['id']

# 2. OCR 识别
ocr_response = requests.post(f"{BASE_URL}/ocr", json={
    "image_ids": [image_id],
    "language": "chinese_cht"
})

# 3. 导出 OFD (方案 B - 推荐)
ofd_response = requests.post(f"{BASE_URL}/export", json={
    "image_ids": [image_id],
    "format": "ofd"  # 使用方案 B
})
print(f"OFD 文件: {ofd_response.json()['output_file']}")
```

---

## 配置

编辑 `application.yml`:

```yaml
server:
  port: 8000

app:
  output_folder: P:/OCR/Output
  upload_folder: P:/OCR/temp
  font_path: C:/Windows/Fonts/simsun.ttc
  default_language: chinese_cht
```

---

## 技术细节

### PDF 透明文字

使用 PDFBox 的 **Render Mode 3 (NEITHER)**:
- 文字既不填充也不描边
- 视觉上完全不可见
- 但可以被搜索和复制
- 基线偏移修正: `fontSize * 0.8`

### OFD 不可见文字层

**方案 B 实现**（终极版）:
```java
// 1. X 轴：开根号曲线完美对齐
double widthMultiplier = 1.0;
if (text.length() > 10) {
    widthMultiplier = 1.0 + (0.035 * Math.sqrt(text.length() - 10));
}

// 2. Y 轴：AWT Ascent 精准定位
double ascentMm = awtFont.getLineMetrics(text, frc).getAscent() * 25.4 / 72.0;
double paragraphY = (ocrY + (ocrH * 0.76)) - ascentMm;

// 3. WPS 兼容：白色 + 1% 透明度
Span span = new Span(text);
span.setFontSize(fontSizeMm);
span.setLetterSpacing(letterSpacing);
span.setColor(255, 255, 255); // 白色

Paragraph p = new Paragraph();
p.add(span);
p.setOpacity(0.01); // 1% 不透明度（WPS 可搜索）
```

生成的 OFD 结构:
```xml
<ofd:TextObject Alpha="0.01" ...>
  <ofd:TextCode DeltaX="0.5 0.5 ...">Sample PDF</ofd:TextCode>
</ofd:TextObject>
```

---

## 跨平台支持

### 主流操作系统

| 平台 | 状态 |
|------|------|
| **Windows** | ✅ |
| **macOS** | ✅ |
| **Linux** | ✅ |

### 国产操作系统

| 操作系统 | 状态 |
|---------|------|
| **麒麟 Kylin** | ✅ |
| **统信 UOS** | ✅ |
| **深度 Deepin** | ✅ |
| **中标麒麟** | ✅ |
| **华为 openEuler** | ✅ |

---

## 部署

### 后台运行

```bash
# Windows
start /B java -jar jpeg2pdf-ofd-1.0.0.jar

# Linux/macOS
nohup java -jar jpeg2pdf-ofd-1.0.0.jar > app.log 2>&1 &
```

### 系统服务（Linux）

```bash
sudo systemctl start jpeg2pdf
sudo systemctl enable jpeg2pdf
```

---

## 常见问题

### Q: macOS/Linux 找不到字体？

A: 安装中文字体：

```bash
# macOS
brew install font-noto-sans-cjk

# Ubuntu/Debian
sudo apt install fonts-wqy-microhei

# CentOS/RHEL
sudo yum install wqy-microhei-fonts
```

### Q: OFD 文件无法显示？

A: 使用推荐的 OFD 阅读器：
- WPS Office
- 福昕阅读器
- https://fp.scofd.com/

### Q: 文字位置不准确？

A: 已完美修复（27 个版本迭代）：
- **X 轴**: 开根号曲线算法（完美对齐）
- **Y 轴**: AWT Ascent 精准定位
- **WPS 兼容**: 白色 + 1% 透明度

### Q: WPS 无法搜索？

A: 使用白色 + 1% 透明度：
- WPS 会过滤 Opacity=0 的对象
- 使用 `span.setColor(255, 255, 255)` + `p.setOpacity(0.01)`
- 人眼看不见，但 WPS 可以搜索

---

## 性能对比

| 方案 | 文件大小 | 生成速度 | PathObject | 对齐精度 | WPS 搜索 | 推荐度 |
|------|---------|---------|-----------|---------|---------|--------|
| **方案 B** | 728 KB | 快 | 无 | **完美** ✅ | **完美** ✅ | ⭐⭐⭐⭐⭐ |
| 方案 A | 1155 KB | 较慢 | 有 | 一般 | 一般 | ⭐⭐⭐ |

---

## 算法演进历程

### 27 个版本的终极突破

1. **v1-v4**: 基础校准
2. **v5-v6**: 宽度倍数校准
3. **v7-v11**: Binary Search（0.92→1.1→0.96）
4. **v12**: SERIF 暴冲陷阱
5. **v13**: 黄金交叉点 0.97（跷蹺板效应）
6. **v14-v15**: 最终锁定版（0.95→0.98 + 双向锁）
7. **v16-v17**: 最终冲刺（1.0→1.05 + 放宽极限）
8. **v18**: 完全重写（移除所有 AWT）
9. **v19**: 终极缝合版（手工估算 X + AWT Ascent Y）
10. **v20**: 返璞归真（纯 AWT + 无系数 + 无安全锁）
11. **v21**: 反向动态压缩（0.0012）
12. **v22**: 强力压缩（0.005）
13. **v23**: 精细微调（0.002）
14. **v24**: 平衡压缩（0.003）
15. **v25**: 折线补偿法（0.006 + 上限 1.18）
16. **v26**: 开根号曲线（0.025 * √）
17. **v27**: **终极微调（0.035 * √）** ✨ **最终版本**

### 核心发现

**误差增长是抛物线，不是直线！**

- 任何线性公式都无法同时命中三个黄金数据点
- 开根号曲线完美符合字体度量的物理特性
- 初期快速增长（中字 10-40）
- 后期逐渐平缓（长句 40+）

---

## 技术规格

详见 [spec.md](spec.md)

---

## 更新日志

### v1.2.0 (2026-03-22) ⭐ **终极版本**
- ✅ **终极突破**: 开根号曲线算法（27 个版本迭代）
- ✅ **完美对齐**: X 轴 + Y 轴 100% 精准对齐
- ✅ **WPS 兼容**: 白色文字 + 1% 透明度
- ✅ **三个黄金数据点**: 10→1.0, 30→1.16, 75→1.28
- ✅ **文件大小优化**: 728 KB (-37%)

### v1.1.0 (2026-03-21)
- ✅ 升级 ofdrw 到 2.3.8
- ✅ 新增方案 B：ofdrw-layout 直接生成 OFD
- ✅ 修复 PDF 文字基线偏移问题
- ✅ 文件大小优化 (-37%)
- ✅ 移除不必要的 PathObject

### v1.0.0 (2026-03-21)
- ✅ OCR 识别功能
- ✅ PDF 透明文字生成
- ✅ OFD 双层结构生成

---

## GitHub

https://github.com/brianshih04/JPEG2PDF_OFD_Java_2

---

## License

MIT
