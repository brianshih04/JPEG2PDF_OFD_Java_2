# JPEG2PDF_OFD_Java

**JPEG 图片 OCR 转 Searchable PDF/OFD**

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux%20%7C%20麒麟%20%7C%20UOS-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![License](https://img.shields.io/badge/License-MIT-green)

## 功能特性

- ✅ **OCR 识别** - RapidOCR-Java (PP-OCRv4)
- ✅ **Searchable PDF** - 透明文字层（可搜索、可复制）
- ✅ **Searchable OFD** - 符合 GB/T 33190-2016 标准
- ✅ **多页支持** - 批量处理多页文档
- ✅ **Web UI** - 现代化响应式界面
- ✅ **跨平台** - Windows、macOS、Linux、国产操作系统

---

## 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| **OCR** | RapidOCR | 0.0.7 |
| **PDF** | Apache PDFBox | 2.0.29 |
| **OFD** | ofdrw-converter | 2.0.3 |
| **框架** | Spring Boot | 3.2.x |
| **UI** | Thymeleaf + Tailwind CSS | - |

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
  "format": "searchable_ofd"  // 或 "searchable_pdf", "text"
}
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

# 3. 导出 OFD
ofd_response = requests.post(f"{BASE_URL}/export", json={
    "image_ids": [image_id],
    "format": "searchable_ofd"
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

---

## 技术规格

详见 [spec.md](spec.md)

---

## GitHub

https://github.com/brianshih04/JPEG2PDF_OFD_Java_2

---

## License

MIT
