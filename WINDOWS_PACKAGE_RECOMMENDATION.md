# 🪟 Windows 可执行程序打包方案

## ✅ 当前状态

**已完成：**
- ✅ Spring Boot Fat JAR (83 MB) - 需要安装 Java
- ✅ 便携版脚本 (已创建)
- ✅ jpackage 脚本 (已创建)

---

## 📊 5 种打包方案对比

| 方案 | 文件大小 | 需要 Java | 安装方式 | 难度 | 推荐度 |
|------|---------|----------|---------|------|--------|
| **1. jpackage (MSI/EXE)** | ~150 MB | ❌ 不需要 | 安装包 | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **2. 便携版 (文件夹)** | ~83 MB | ✅ 需要 | 解压即用 | ⭐ | ⭐⭐⭐⭐⭐ |
| **3. Launch4j (EXE)** | ~83 MB | ✅ 需要 | 单个 EXE | ⭐⭐ | ⭐⭐⭐ |
| **4. GraalVM Native** | ~50 MB | ❌ 不需要 | 单个 EXE | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **5. Docker** | ~200 MB | ❌ 不需要 | 容器 | ⭐⭐ | ⭐⭐⭐ |

---

## 🎯 推荐方案

### **方案 1：jpackage（最推荐）⭐⭐⭐⭐⭐**

**优势：**
```
✅ 创建真正的 Windows 安装包（MSI/EXE）
✅ 包含 JRE，用户无需安装 Java
✅ 创建桌面快捷方式
✅ 添加到开始菜单
✅ 专业级部署
```

**生成文件：**
```
选项 A: JPEG2PDF-OFD-1.0.0.exe (安装包, ~150 MB)
选项 B: JPEG2PDF-OFD/ 文件夹 (便携版, ~150 MB)
```

**执行命令：**
```powershell
# 方式 1: 创建安装包（推荐分发）
.\build-exe.ps1

# 方式 2: 创建便携版（无需安装）
jpackage --name "JPEG2PDF-OFD" --input target --main-jar jpeg2pdf-ofd-1.0.0.jar --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication --type app-image --dest dist --java-options "-Xmx2G" --win-console
```

**系统要求：**
- Windows 10/11 (64-bit)
- 无需安装 Java
- 至少 2 GB RAM

---

### **方案 2：便携版（最简单）⭐⭐⭐⭐⭐**

**优势：**
```
✅ 最简单快速
✅ 解压即用
✅ 包含启动脚本
✅ 适合 U 盘携带
```

**生成文件：**
```
dist\JPEG2PDF-OFD-Portable\
  ├── jpeg2pdf-ofd.jar (83 MB)
  ├── 启动服务.bat
  ├── 停止服务.bat
  ├── install-java.bat
  ├── README.txt
  └── 使用说明.md
```

**执行命令：**
```powershell
.\build-portable.bat
```

**系统要求：**
- Windows 10/11 (64-bit)
- **需要安装 JDK 17+**
- 至少 2 GB RAM

---

### **方案 3：Launch4j（单文件 EXE）⭐⭐⭐**

**优势：**
```
✅ 单个 EXE 文件
✅ 自定义图标
✅ 更专业的启动界面
```

**劣势：**
```
⚠️ 仍然需要安装 Java
⚠️ 需要额外工具
```

**执行步骤：**
```powershell
# 1. 下载 Launch4j
choco install launch4j

# 2. 创建配置文件
# 3. 运行打包
launch4j launch4j.xml
```

---

### **方案 4：GraalVM Native Image（最小）⭐⭐⭐**

**优势：**
```
✅ 原生编译，启动极快（<100ms）
✅ 文件最小（~50 MB）
✅ 无需 Java
✅ 性能最好
```

**劣势：**
```
⚠️ 配置复杂
⚠️ 可能与某些库不兼容
⚠️ 需要大量测试
```

**执行步骤：**
```powershell
# 1. 安装 GraalVM
choco install graalvm-jdk17

# 2. 添加 Native Image 插件到 pom.xml

# 3. 编译
mvn -Pnative package
```

---

### **方案 5：Docker（服务器部署）⭐⭐⭐**

**优势：**
```
✅ 环境完全隔离
✅ 跨平台
✅ 易于部署
```

**执行步骤：**
```powershell
# 1. 创建 Dockerfile (已存在)

# 2. 构建镜像
docker build -t jpeg2pdf-ofd:1.0.0 .

# 3. 运行容器
docker run -d -p 8000:8000 jpeg2pdf-ofd:1.0.0
```

---

## 🚀 我的建议

### **场景 1：个人使用（最简单）**
```
推荐：方案 2 - 便携版

原因：
✅ 5 分钟内完成
✅ 不需要额外工具
✅ 已有 Java 环境
✅ 文件小（83 MB）

执行：.\build-portable.bat
```

### **场景 2：分发给别人（最专业）**
```
推荐：方案 1 - jpackage

原因：
✅ 用户无需安装 Java
✅ 专业的安装包
✅ 桌面快捷方式
✅ 开始菜单集成

执行：.\build-exe.ps1
```

### **场景 3：追求极致性能**
```
推荐：方案 4 - GraalVM Native Image

原因：
✅ 启动时间 <100ms
✅ 内存占用低
✅ 文件最小

注意：需要测试兼容性
```

---

## 📝 详细步骤

### **方案 1：jpackage（推荐）**

#### **步骤 1：检查 JDK 版本**
```powershell
java -version
# 需要 JDK 17 或更高版本
```

#### **步骤 2：运行打包脚本**
```powershell
cd D:\Projects\JPEG2PDF_OFD_Java_2
.\build-exe.ps1
```

#### **步骤 3：生成文件**
```
dist\JPEG2PDF-OFD-1.0.0.exe (安装包, ~150 MB)
或
dist\JPEG2PDF-OFD\ (便携版文件夹)
```

#### **步骤 4：安装/运行**
```
双击 JPEG2PDF-OFD-1.0.0.exe 安装
或
进入 JPEG2PDF-OFD 文件夹，运行 JPEG2PDF-OFD.exe
```

---

### **方案 2：便携版（最快）**

#### **步骤 1：运行打包脚本**
```powershell
cd D:\Projects\JPEG2PDF_OFD_Java_2
.\build-portable.bat
```

#### **步骤 2：复制文件夹**
```
将 dist\JPEG2PDF-OFD-Portable 文件夹复制到任意位置
```

#### **步骤 3：运行**
```
双击 启动服务.bat
浏览器访问 http://localhost:8000
```

---

## ⚡ 快速执行（5 分钟）

### **立即可用：便携版**

```powershell
# 1. 进入项目目录
cd D:\Projects\JPEG2PDF_OFD_Java_2

# 2. 运行打包
.\build-portable.bat

# 3. 完成！
# 文件位置：dist\JPEG2PDF-OFD-Portable\
```

---

### **30 分钟：jpackage 安装包**

```powershell
# 1. 检查 JDK
java -version  # 需要 17+

# 2. 如果没有 JDK 17，安装
choco install openjdk17

# 3. 运行打包
.\build-exe.ps1

# 4. 生成安装包
# dist\JPEG2PDF-OFD-1.0.0.exe
```

---

## 🎯 最终推荐

### **现在就做：便携版** ⭐⭐⭐⭐⭐
```
时间：5 分钟
难度：⭐ (最简单)
文件：83 MB
需要：Java 17+

执行：.\build-portable.bat
```

### **稍后优化：jpackage** ⭐⭐⭐⭐⭐
```
时间：30 分钟
难度：⭐⭐ (需要 JDK 17)
文件：150 MB
需要：无（包含 JRE）

执行：.\build-exe.ps1
```

---

## 📦 打包对比

| 特性 | 便携版 | jpackage |
|------|--------|----------|
| **制作时间** | 5 分钟 | 30 分钟 |
| **文件大小** | 83 MB | 150 MB |
| **需要 Java** | ✅ 是 | ❌ 否 |
| **安装方式** | 解压即用 | 安装包 |
| **适合场景** | 个人使用 | 分发给他人 |
| **专业程度** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🚀 立即开始

### **选择 1：5 分钟快速版（推荐新手）**
```powershell
.\build-portable.bat
```

### **选择 2：30 分钟专业版（推荐分发）**
```powershell
.\build-exe.ps1
```

---

## 📞 需要帮助？

**如果遇到问题：**
1. 检查 JDK 版本（需要 17+）
2. 检查 Maven 是否安装
3. 查看错误日志

**推荐执行顺序：**
```
1. 先做便携版（5 分钟，立即可用）
2. 测试功能是否正常
3. 再做 jpackage 安装包（30 分钟，用于分发）
```

---

**你想先做哪一个？** 🎯
