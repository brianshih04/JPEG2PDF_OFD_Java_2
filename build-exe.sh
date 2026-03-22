#!/bin/bash

echo "=================================="
echo "JPEG2PDF-OFD Linux/macOS 打包工具"
echo "=================================="
echo ""

# 检测操作系统
OS=$(uname -s)
echo "检测到操作系统: $OS"

# 1. 编译项目
echo ""
echo "[1/3] 编译项目..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败！"
    exit 1
fi

echo "✅ 编译成功！"

# 2. 创建输出目录
echo ""
echo "[2/3] 创建输出目录..."
mkdir -p dist

# 3. 使用 jpackage 打包
echo ""
echo "[3/3] 生成可执行文件..."

if [ "$OS" = "Darwin" ]; then
    # macOS
    jpackage \
      --name "JPEG2PDF-OFD" \
      --input target \
      --main-jar jpeg2pdf-ofd-1.0.0.jar \
      --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
      --type app-image \
      --dest dist \
      --java-options "-Xmx2G" \
      --mac-package-identifier com.ocr.jpeg2pdf \
      --mac-package-name "JPEG2PDF-OFD" \
      --app-version "1.0.0"
    
    echo ""
    echo "✅ 打包完成！"
    echo ""
    echo "可执行文件位置:"
    echo "  dist/JPEG2PDF-OFD.app"
    echo ""
    echo "使用方法:"
    echo "  1. 双击运行 JPEG2PDF-OFD.app"
    echo "  2. 访问 http://localhost:8000"
    
elif [ "$OS" = "Linux" ]; then
    # Linux
    jpackage \
      --name "jpeg2pdf-ofd" \
      --input target \
      --main-jar jpeg2pdf-ofd-1.0.0.jar \
      --main-class com.ocr.jpeg2pdf.Jpeg2PdfOfdApplication \
      --type app-image \
      --dest dist \
      --java-options "-Xmx2G" \
      --linux-shortcut \
      --app-version "1.0.0"
    
    echo ""
    echo "✅ 打包完成！"
    echo ""
    echo "可执行文件位置:"
    echo "  dist/jpeg2pdf-ofd/bin/jpeg2pdf-ofd"
    echo ""
    echo "使用方法:"
    echo "  1. 运行: dist/jpeg2pdf-ofd/bin/jpeg2pdf-ofd"
    echo "  2. 访问 http://localhost:8000"
else
    echo "❌ 不支持的操作系统: $OS"
    exit 1
fi
