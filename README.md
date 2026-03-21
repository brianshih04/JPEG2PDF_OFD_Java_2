# JPEG2PDF_OFD_Java

**JPEG 图片 OCR 转 Searchable PDF/OFD**

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux%20%7C%20麒麟%20%7C%20UOS-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 功能特性

- ✅ **OCR 识别** - RapidOCR-Java (PP-OCRv4)
- ✅ **Searchable PDF** - 透明文字层（可搜索、可复制）
- ✅ **Searchable OFD** - 符合 GB/T 33190-2016 标准
- ✅ **两种 OFD 生成方案** - PDF 转换 / 直接生成
- ✅ **多页支持** - 批量处理多页文档
- ✅ **Web UI** - 现代化响应式界面
- ✅ **跨平台** - Windows、macOS、Linux、国产操作系统

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
- 使用 `Alpha="0"` 实现不可见文字
- **文件大小: ~728 KB (-37%)**
- **无 PathObject 干扰**
- **代码更简单**

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
- `ofd` - 方案 B（推荐）：ofdrw-layout 直接生成
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

**方案 B 实现**:
```java
Span span = new Span(text);
Paragraph p = new Paragraph();
p.add(span);
p.setOpacity(0.0);  // 完全透明
```

生成的 OFD 结构:
```xml
<ofd:TextObject Alpha="0" ...>
  <ofd:TextCode>Sample PDF</ofd:TextCode>
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

A: 已修复基线偏移问题：
- PDF: 使用 `fontSize * 0.8` 修正基线位置
- OFD: 坐标转换已优化

---

## 性能对比

| 方案 | 文件大小 | 生成速度 | PathObject | 推荐度 |
|------|---------|---------|-----------|--------|
| **方案 B** | 728 KB | 快 | 无 | ⭐⭐⭐⭐⭐ |
| 方案 A | 1155 KB | 较慢 | 有 | ⭐⭐⭐ |

---

## 技术规格

详见 [spec.md](spec.md)

---

## 更新日志

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
