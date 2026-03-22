# 独立执行文件打包方案

## 📊 方案对比

| 方案 | 平台 | 文件大小 | 启动速度 | 难度 | 推荐度 |
|------|------|---------|---------|------|--------|
| **Spring Boot Fat JAR** | 跨平台 | ~80 MB | 中等 | ⭐ | ⭐⭐⭐⭐ |
| **jpackage (JDK 17+)** | 原生 | ~150 MB | 快 | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **GraalVM Native Image** | 原生 | ~50 MB | 极快 | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Launch4j** | Windows | ~80 MB | 中等 | ⭐⭐ | ⭐⭐⭐ |
| **Docker** | 跨平台 | ~200 MB | 慢 | ⭐⭐ | ⭐⭐⭐⭐ |

---

## 🎯 推荐方案：jpackage（JDK 17+ 内置）

**优势：**
- ✅ 创建原生安装包和可执行文件
- ✅ 包含 JRE，无需用户安装 Java
- ✅ 支持 Windows/macOS/Linux
- ✅ 启动速度快
- ✅ 双击即可运行

---

## 📦 方案 1：Spring Boot Fat JAR（最简单）

### 当前已支持！

**编译：**
```bash
mvn clean package -DskipTests
```

**生成文件：**
```
target/jpeg2pdf-ofd-1.0.0.jar (约 80 MB)
```

**运行：**
```bash
java -jar jpeg2pdf-ofd-1.0.0.jar
```

**优点：**
- ✅ 无需额外配置
- ✅ 跨平台
- ✅ 已经可用

**缺点：**
- ⚠️ 需要用户安装 JDK/JRE
- ⚠️ 不是真正的"可执行文件"

---

## 📦 方案 2：jpackage（推荐）⭐

### Windows 可执行文件（.exe）

**1. 安装 JDK 17+**
```bash
# Windows
choco install openjdk17

# macOS
brew install openjdk@17

# Linux
sudo apt install openjdk-17-jdk
```

**2. 编译项目**
```bash
mvn clean package -DskipTests
```

**3. 使用 jpackage 创建 EXE**
```bash
# Windows
jpackage \
  --name "JPEG2PDF-OFD" \
  --input target \
  --main-jar jpeg2pdf-ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
  --type app-image \
  --dest dist \
  --java-options "-Xmx2G" \
  --win-console \
  --win-dir-chooser \
  --win-menu \
  --win-shortcut

# 生成：dist/JPEG2PDF-OFD/JPEG2PDF-OFD.exe (约 150 MB)
```

**4. 创建安装包**
```bash
jpackage \
  --name "JPEG2PDF-OFD" \
  --input target \
  --main-jar jpeg2pdf-ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
  --type exe \
  --dest dist \
  --java-options "-Xmx2G" \
  --win-console \
  --win-dir-chooser \
  --win-menu \
  --win-shortcut \
  --app-version "1.0.0" \
  --description "JPEG OCR to Searchable PDF/OFD Converter" \
  --vendor "Brian Shih"

# 生成：dist/JPEG2PDF-OFD-1.0.0.exe (安装包)
```

---

### macOS App Bundle（.app）

```bash
jpackage \
  --name "JPEG2PDF-OFD" \
  --input target \
  --main-jar jpeg2pdf-ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
  --type app-image \
  --dest dist \
  --java-options "-Xmx2G" \
  --mac-package-identifier com.ocr.jpeg2pdf \
  --mac-package-name "JPEG2PDF-OFD"

# 生成：dist/JPEG2PDF-OFD.app
```

---

### Linux DEB/RPM

```bash
# DEB (Ubuntu/Debian)
jpackage \
  --name "jpeg2pdf-ofd" \
  --input target \
  --main-jar jpeg2pdf-ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
  --type deb \
  --dest dist \
  --java-options "-Xmx2G" \
  --linux-shortcut

# 生成：dist/jpeg2pdf-ofd_1.0.0_amd64.deb

# RPM (CentOS/RHEL)
jpackage \
  --name "jpeg2pdf-ofd" \
  --input target \
  --main-jar jpeg2pdf-ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
  --type rpm \
  --dest dist \
  --java-options "-Xmx2G" \
  --linux-shortcut

# 生成：dist/jpeg2pdf-ofd-1.0.0-1.x86_64.rpm
```

---

## 📦 方案 3：GraalVM Native Image（最快）

**优势：**
- ✅ 编译成真正的原生机器码
- ✅ 启动时间 <100ms
- ✅ 内存占用低
- ✅ 文件小（~50 MB）

**缺点：**
- ⚠️ 需要额外配置
- ⚠️ 可能与某些库不兼容（需要测试）
- ⚠️ 编译时间长

**步骤：**

**1. 安装 GraalVM**
```bash
# Windows
choco install graalvm-jdk17

# macOS
brew install --cask graalvm/tap/graalvm-jdk17

# Linux
sdk install java 17.0.9-graal
```

**2. 添加 Native Image 配置**

创建 `src/main/resources/META-INF/native-image/reflect-config.json`:
```json
[
  {
    "name": "com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
  }
]
```

**3. 添加 Maven 插件**
```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <version>0.9.28</version>
    <configuration>
        <mainClass>com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication</mainClass>
        <buildArgs>
            <buildArg>--no-fallback</buildArg>
        </buildArgs>
    </configuration>
</plugin>
```

**4. 编译 Native Image**
```bash
mvn -Pnative package
```

**5. 生成文件**
```
target/jpeg2pdf-ofd (Linux/macOS)
target/jpeg2pdf-ofd.exe (Windows)
```

---

## 📦 方案 4：Launch4j（仅 Windows）

**1. 安装 Launch4j**
```bash
choco install launch4j
```

**2. 创建配置文件 `launch4j.xml`**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>console</headerType>
  <jar>target/jpeg2pdf-ofd-1.0.0.jar</jar>
  <outfile>dist/JPEG2PDF-OFD.exe</outfile>
  <errTitle>JPEG2PDF-OFD</errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>http://java.com/download</downloadUrl>
  <supportUrl></supportUrl>
  <stayAlive>false</stayAlive>
  <restartOnCrash>false</restartOnCrash>
  <manifest></manifest>
  <icon></icon>
  <jre>
    <minVersion>17.0</minVersion>
    <bundledJre64Bit>false</bundledJre64Bit>
    <bundledJreAsFallback>false</bundledJreAsFallback>
    <jdkPreference>jreOnly</jdkPreference>
    <runtimeBits>64/32</runtimeBits>
  </jre>
  <versionInfo>
    <fileVersion>1.0.0.0</fileVersion>
    <txtFileVersion>1.0.0</txtFileVersion>
    <fileDescription>JPEG OCR to Searchable PDF/OFD Converter</fileDescription>
    <copyright>Brian Shih</copyright>
    <productVersion>1.0.0.0</productVersion>
    <txtProductVersion>1.0.0</txtProductVersion>
    <productName>JPEG2PDF-OFD</productName>
    <internalName>JPEG2PDF-OFD</internalName>
    <originalFilename>JPEG2PDF-OFD.exe</originalFilename>
    <trademarks></trademarks>
    <language>ENGLISH_US</language>
  </versionInfo>
</launch4jConfig>
```

**3. 生成 EXE**
```bash
launch4j launch4j.xml
```

---

## 📦 方案 5：Docker（跨平台）

**1. 创建 Dockerfile**
```dockerfile
FROM openjdk:17-jdk-slim

# 安装中文字体
RUN apt-get update && \
    apt-get install -y fonts-wqy-microhei && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/jpeg2pdf-ofd-1.0.0.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-Xmx2G", "-jar", "app.jar"]
```

**2. 构建 Docker 镜像**
```bash
docker build -t jpeg2pdf-ofd:1.0.0 .
```

**3. 运行容器**
```bash
docker run -d \
  -p 8000:8000 \
  -v ~/OCR/Output:/app/output \
  -v ~/OCR/temp:/app/temp \
  jpeg2pdf-ofd:1.0.0
```

---

## 🎯 推荐方案总结

### 方案 1：jpackage（最佳平衡）⭐⭐⭐⭐⭐
```
✅ 原生可执行文件
✅ 包含 JRE
✅ 支持所有平台
✅ 无需额外安装
✅ 双击运行

文件大小: ~150 MB
启动速度: 快
```

### 方案 2：Spring Boot Fat JAR（最简单）⭐⭐⭐⭐
```
✅ 已经可用
✅ 跨平台
✅ 无需额外配置

⚠️ 需要安装 Java
文件大小: ~80 MB
启动速度: 中等
```

### 方案 3：Docker（服务器部署）⭐⭐⭐⭐
```
✅ 环境隔离
✅ 跨平台
✅ 易于部署

⚠️ 需要安装 Docker
文件大小: ~200 MB
启动速度: 慢
```

---

## 🚀 快速开始（推荐 jpackage）

**Windows 一键打包脚本：**
```powershell
# build-exe.ps1
mvn clean package -DskipTests

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

Write-Host "✅ 安装包已生成: dist/JPEG2PDF-OFD-1.0.0.exe"
```

**运行：**
```bash
powershell -File build-exe.ps1
```

---

## 📝 总结

**立即可用：** Spring Boot Fat JAR（当前已支持）

**推荐打包：** jpackage（原生可执行文件）

**服务器部署：** Docker

**追求极致性能：** GraalVM Native Image（需测试兼容性）
