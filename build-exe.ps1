# JPEG2PDF-OFD Windows 可执行文件打包脚本

Write-Host "=================================="
Write-Host "JPEG2PDF-OFD Windows EXE 打包工具"
Write-Host "=================================="
Write-Host ""

# 检查 JDK 版本
$javaVersion = java -version 2>&1 | Select-Object -First 1
Write-Host "检测到 Java 版本: $javaVersion"

# 1. 编译项目
Write-Host "`n[1/3] 编译项目..."
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 编译失败！"
    exit 1
}

Write-Host "✅ 编译成功！"

# 2. 创建输出目录
Write-Host "`n[2/3] 创建输出目录..."
New-Item -ItemType Directory -Force -Path "dist" | Out-Null

# 3. 使用 jpackage 创建可执行文件
Write-Host "`n[3/3] 生成 Windows 可执行文件..."

jpackage `
  --name "JPEG2PDF-OFD" `
  --input target `
  --main-jar jpeg2pdf-ofd-1.0.0.jar `
  --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication `
  --type app-image `
  --dest dist `
  --java-options "-Xmx2G" `
  --win-console `
  --app-version "1.0.0" `
  --description "JPEG OCR to Searchable PDF/OFD Converter" `
  --vendor "Brian Shih"

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 打包失败！请确保安装了 JDK 17+"
    Write-Host ""
    Write-Host "安装 JDK 17+:"
    Write-Host "  choco install openjdk17"
    exit 1
}

Write-Host ""
Write-Host "=================================="
Write-Host "✅ 打包完成！"
Write-Host "=================================="
Write-Host ""
Write-Host "可执行文件位置:"
Write-Host "  dist\JPEG2PDF-OFD\JPEG2PDF-OFD.exe"
Write-Host ""
Write-Host "使用方法:"
Write-Host "  1. 双击运行 JPEG2PDF-OFD.exe"
Write-Host "  2. 浏览器访问 http://localhost:8000"
Write-Host ""

# 询问是否创建安装包
$createInstaller = Read-Host "是否创建安装包？(y/n)"

if ($createInstaller -eq "y" -or $createInstaller -eq "Y") {
    Write-Host "`n创建安装包..."
    
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
      --app-version "1.0.0" `
      --description "JPEG OCR to Searchable PDF/OFD Converter" `
      --vendor "Brian Shih"
    
    Write-Host ""
    Write-Host "✅ 安装包已生成: dist\JPEG2PDF-OFD-1.0.0.exe"
}
