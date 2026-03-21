# JPEG2PDF_OFD_Java 技术规格

## 项目概述

**JPEG2PDF_OFD_Java** 是一个将 JPEG 图片通过 OCR 转换为可搜索 PDF 和 OFD 文件的 Java 应用。

---

## 技术栈

| 组件 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **框架** | Spring Boot | 3.2.x | 基础框架 |
| **语言** | Java | 17+ | 开发语言 |
| **OCR** | RapidOCR | 0.0.7 | [GitHub](https://github.com/RapidAI/RapidOCR) |
| **PDF** | Apache PDFBox | 2.0.29 | PDF 生成（必须用 2.x） |
| **OFD** | ofdrw-converter | 2.0.3 | [GitHub](https://github.com/ofdrw/ofdrw/tree/master/ofdrw-converter) |
| **模板引擎** | Thymeleaf | - | 服务端渲染 |
| **CSS 框架** | Tailwind CSS | - | 响应式 UI |
| **构建工具** | Maven | 3.9.x | 依赖管理 |

---

## 架构设计

### 工作流程

```
JPEG 图片
    ↓
[上传] Upload API (/api/upload)
    ↓
[OCR] RapidOCR 识别 (/api/ocr)
    ↓
[生成] PDFBox 2.x → Searchable PDF
    ↓
[转换] ofdrw-converter → Searchable OFD
```

---

## API 设计

### 1. 上传图片

```http
POST /api/upload
Content-Type: multipart/form-data

files: [图片文件]
```

**响应**:
```json
{
  "images": [
    {
      "id": "uuid",
      "originalFilename": "test.jpg",
      "width": 800,
      "height": 600
    }
  ],
  "uploaded_count": 1,
  "success": true
}
```

---

### 2. OCR 识别

```http
POST /api/ocr
Content-Type: application/json

{
  "image_ids": ["uuid1", "uuid2"],
  "language": "chinese_cht"
}
```

**支持的语言**:
- `chinese_cht` - 繁体中文
- `chinese_sim` - 简体中文
- `english` - 英文
- `japan` - 日文

**响应**:
```json
{
  "success": true,
  "results": [
    {
      "image_id": "uuid",
      "line_count": 27,
      "confidence": 0.95,
      "text": "识别的文字内容",
      "text_positions": [
        {
          "text": "Sample",
          "x": 100,
          "y": 50,
          "width": 200,
          "height": 30,
          "fontSize": 24
        }
      ]
    }
  ]
}
```

---

### 3. 导出文件

```http
POST /api/export
Content-Type: application/json

{
  "image_ids": ["uuid1", "uuid2"],
  "format": "searchable_ofd"
}
```

**支持的格式**:
- `searchable_pdf` - 可搜索 PDF
- `searchable_ofd` - 可搜索 OFD
- `text` - 纯文本

**响应**:
```json
{
  "success": true,
  "output_file": "P:/OCR/Output/output_20260321_120000.ofd",
  "format": "searchable_ofd",
  "page_count": 2,
  "message": "已成功匯出 2 頁"
}
```

---

## 核心功能

### 1. OCR 识别

**技术**: RapidOCR-Java
- 基于 PaddleOCR
- 支持 PP-OCRv4 模型
- 离线运行，无需网络

**特性**:
- ✅ 真实坐标提取
- ✅ 信心度评分
- ✅ 多语言支持
- ✅ 批量处理

---

### 2. Searchable PDF

**技术**: Apache PDFBox 2.0.29

**特性**:
- ✅ 透明文字层（RenderingMode.NEITHER）
- ✅ 可搜索、可复制
- ✅ 保持原始图片
- ✅ 字体自动选择

**关键代码**:
```java
// 设置透明文字
contentStream.setRenderingMode(RenderingMode.NEITHER);
contentStream.beginText();
contentStream.setFont(font, fontSize);
contentStream.newLineAtOffset(x, y);
contentStream.showText(text);
contentStream.endText();
```

---

### 3. Searchable OFD

**技术**: ofdrw-converter 2.0.3

**特性**:
- ✅ 符合 GB/T 33190-2016 标准
- ✅ 透明文字层
- ✅ WPS Office 兼容
- ✅ 文件体积小（不嵌入字体）

**转换流程**:
```java
// 1. 使用 PDFBox 生成 PDF
pdfService.generateSearchablePdf(images, ocrResults, tempPdf);

// 2. 使用 ofdrw-converter 转换
try (PDFConverter converter = new PDFConverter(ofdPath)) {
    converter.convert(tempPdf);
}
```

---

## 项目结构

```
JPEG2PDF_OFD_Java/
├── src/main/
│   ├── java/com/ocr/jpeg2pdf/
│   │   ├── config/           # 配置类
│   │   ├── controller/       # 控制器
│   │   ├── model/            # 数据模型
│   │   ├── service/          # 服务接口
│   │   │   └── impl/         # 服务实现
│   │   └── Application.java  # 主程序
│   └── resources/
│       ├── templates/        # Thymeleaf 模板
│       ├── static/           # 静态资源
│       └── application.yml   # 配置文件
├── pom.xml
└── README.md
```

---

## 配置

### application.yml

```yaml
server:
  port: 8000

spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB

app:
  output_folder: P:/OCR/Output
  upload_folder: P:/OCR/temp
  font_path: C:/Windows/Fonts/simsun.ttc
  ocr_engine: paddleocr
  default_language: chinese_cht
```

---

## 跨平台支持

### 支持的操作系统

| 平台 | 状态 | 测试 |
|------|------|------|
| **Windows** | ✅ | ✅ |
| **macOS** | ✅ | ⚠️ |
| **Linux** | ✅ | ⚠️ |
| **麒麟 Kylin** | ✅ | ⚠️ |
| **统信 UOS** | ✅ | ⚠️ |
| **深度 Deepin** | ✅ | ⚠️ |

### 跨平台路径

```java
// 自动检测操作系统
String os = System.getProperty("os.name").toLowerCase();

// 字体路径
if (os.contains("win")) {
    fontPath = "C:/Windows/Fonts/simsun.ttc";
} else if (os.contains("mac")) {
    fontPath = "/System/Library/Fonts/STHeiti Light.ttc";
} else {
    fontPath = "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc";
}

// 输出目录
String home = System.getProperty("user.home");
if (os.contains("win")) {
    outputDir = "P:/OCR/Output";
} else if (os.contains("mac")) {
    outputDir = home + "/Documents/OCR/Output";
} else {
    outputDir = home + "/OCR/Output";
}
```

---

## 部署

### 本地运行

```bash
# 编译
mvn clean package -DskipTests

# 运行
java -jar target/jpeg2pdf-ofd-1.0.0.jar
```

### 后台运行

```bash
# Windows
start /B java -jar jpeg2pdf-ofd-1.0.0.jar

# Linux/macOS
nohup java -jar jpeg2pdf-ofd-1.0.0.jar > app.log 2>&1 &
```

### 系统服务（Linux）

```bash
# 创建服务
sudo nano /etc/systemd/system/jpeg2pdf.service

# 启动
sudo systemctl start jpeg2pdf
sudo systemctl enable jpeg2pdf
```

---

## 安全性

- ✅ **完全离线** - 不需要网络连接
- ✅ **数据不出本机** - 所有处理在本地
- ✅ **无外部依赖** - 纯 Java 实现
- ✅ **开源代码** - 可审计

---

## 性能

| 指标 | 数值 |
|------|------|
| **单张图片 OCR** | ~2 秒 |
| **PDF 生成** | ~1 秒 |
| **OFD 转换** | ~3 秒 |
| **内存占用** | ~200 MB |
| **JAR 大小** | ~58 MB |

---

## 限制

1. **PDFBox 版本**: 必须使用 2.x（ofdrw-converter 依赖）
2. **字体**: 需要 SimSun 或其他中文字体
3. **图片格式**: 支持 JPEG, PNG
4. **文件大小**: 单文件最大 50 MB

---

## 版本历史

### v1.0.0 (2026-03-21)
- ✅ 初始版本
- ✅ OCR 识别
- ✅ Searchable PDF
- ✅ Searchable OFD
- ✅ Web UI
- ✅ 跨平台支持

---

## 参考文档

- [RapidOCR](https://github.com/RapidAI/RapidOCR)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [ofdrw](https://github.com/ofdrw/ofdrw)
- [GB/T 33190-2016](http://www.ofdspec.org/)
