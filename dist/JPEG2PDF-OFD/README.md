# JPEG2PDF-OFD Windows 可执行程序

## ✅ 打包完成！

**生成文件位置：** `D:\Projects\JPEG2PDF_OFD_Java_2\dist\JPEG2PDF-OFD\`

**主程序：** `JPEG2PDF-OFD.exe`

**文件大小：** 212 MB（包含 JRE，无需安装 Java）

---

## 🚀 使用方法

### 方式 1：直接运行
```
1. 双击 JPEG2PDF-OFD.exe
2. 等待服务启动（约 10-20 秒）
3. 浏览器自动打开：http://localhost:8000
4. 开始使用 OCR 功能
```

### 方式 2：命令行运行
```powershell
cd D:\Projects\JPEG2PDF_OFD_Java_2\dist\JPEG2PDF-OFD
.\JPEG2PDF-OFD.exe
```

---

## 📁 文件夹结构

```
JPEG2PDF-OFD\
├── JPEG2PDF-OFD.exe         ← 主程序（双击运行）
├── JPEG2PDF-OFD.ico         ← 图标
├── app\
│   ├── jpeg2pdf-ofd-1.0.0.jar   ← 应用程序 JAR
│   └── JPEG2PDF-OFD.cfg          ← 配置文件
└── runtime\                      ← JRE（Java 运行环境）
    ├── bin\
    ├── conf\
    ├── legal\
    └── lib\
```

---

## ⚙️ 系统要求

```
✅ Windows 10/11 (64-bit)
✅ 无需安装 Java（已包含 JRE）
✅ 至少 2 GB RAM
✅ 至少 500 MB 硬盘空间
```

---

## 🎯 功能特性

### 1. OCR 文字识别
- 支持 80+ 种语言
- 中文（繁体/简体）
- 英文、日文、韩文
- 阿拉伯文、俄文等

### 2. Watch Folder 自动监控
- 自动监控文件夹
- 检测到新图片自动处理
- 支持批量处理

### 3. 多格式输出
- Searchable PDF
- Searchable OFD
- 纯文字 TXT

### 4. Web UI
- 现代化界面
- 实时进度显示
- 拖放上傳

---

## 📂 默认文件夹

```
监控文件夹：C:\OCR\Watch\
输出文件夹：C:\OCR\Output\
保存文件夹：C:\OCR\Save\
临时文件夹：C:\OCR\temp\
```

---

## 🔧 配置文件

**位置：** `app\JPEG2PDF-OFD.cfg`

**默认配置：**
```
-Dspring.application.name=JPEG2PDF_OFD_Java
-Dserver.port=8000
-Dapp.output-folder=C:/OCR/Output
-Dapp.watch-folder=C:/OCR/Watch
-Dapp.save-folder=C:/OCR/Save
-Dapp.default-language=chinese_cht
```

**修改端口：**
```
在配置文件中添加：
-Dserver.port=9000
```

---

## 🌐 访问地址

**Web 界面：** http://localhost:8000

**API 文档：** http://localhost:8000/swagger-ui.html

---

## 📦 分发给其他人

### 方式 1：压缩整个文件夹
```
1. 右键点击 JPEG2PDF-OFD 文件夹
2. 发送到 → 压缩(zipped)文件夹
3. 生成 JPEG2PDF-OFD.zip (~212 MB)
4. 发送给其他人
```

### 方式 2：上传到云盘
```
上传 JPEG2PDF-OFD.zip 到：
- Google Drive
- OneDrive
- 百度网盘
- 其他云存储服务
```

---

## 🛠️ 创建安装包（可选）

### 创建 MSI 安装包
```
需要安装 WiX Toolset:

1. 下载 WiX: https://wixtoolset.org/releases/
2. 安装后添加到 PATH
3. 运行:
   jpackage --type msi --name "JPEG2PDF-OFD" ...
```

### 创建 EXE 安装包
```
jpackage `
  --name "JPEG2PDF-OFD" `
  --input target `
  --main-jar jpeg2pdf-ofd-1.0.0.jar `
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication `
  --type exe `
  --dest dist `
  --java-options "-Xmx2G" `
  --win-console `
  --win-dir-chooser `
  --win-menu `
  --win-shortcut `
  --app-version "1.0.0"
```

---

## 🐛 故障排除

### 问题 1：端口被占用
```
错误：Port 8000 was already in use

解决：
1. 修改配置文件中的端口
2. 或者停止占用 8000 端口的程序
```

### 问题 2：无法启动
```
检查：
1. 是否有足够的内存（至少 2 GB）
2. 是否有硬盘空间（至少 500 MB）
3. Windows Defender 是否阻止
```

### 问题 3：浏览器没有自动打开
```
手动打开浏览器，访问：
http://localhost:8000
```

---

## 📊 性能参考

| 图片数量 | 图片大小 | 处理时间 | 输出文件大小 |
|---------|---------|---------|------------|
| 1 张 | 1 MB | ~3 秒 | ~700 KB |
| 5 张 | 5 MB | ~15 秒 | ~3.5 MB |
| 10 张 | 10 MB | ~30 秒 | ~7 MB |

---

## 🎯 快速测试

### 测试 1：手动上传
```
1. 访问 http://localhost:8000
2. 拖放图片到上传区域
3. 选择输出格式（默认 PDF）
4. 点击「上传并开始 OCR」
5. 观察进度条
6. 下载文件
```

### 测试 2：Watch Folder
```
1. 点击 Watch Folder 开关（OFF → ON）
2. 复制图片到 C:\OCR\Watch
3. 等待 1-2 秒
4. 检查 C:\OCR\Output（3 种格式）
5. 检查 C:\OCR\Save（原图）
```

---

## 📞 技术支持

**GitHub:** https://github.com/brianshih04/JPEG2PDF_OFD_Java_2

**文档:** 参见项目根目录的 README.md

**问题反馈:** GitHub Issues

---

## 📄 许可证

MIT License

---

## 🎉 完成！

**现在你可以：**
```
✅ 直接使用便携版
✅ 分享给其他人
✅ 无需安装 Java
✅ 双击即可运行
```

**文件位置：** `D:\Projects\JPEG2PDF_OFD_Java_2\dist\JPEG2PDF-OFD\`

**立即开始：** 双击 `JPEG2PDF-OFD.exe`
