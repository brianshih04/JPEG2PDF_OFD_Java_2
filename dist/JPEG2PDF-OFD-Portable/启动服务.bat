@echo off
REM ==================================================
REM JPEG2PDF-OFD 便携版启动脚本
REM ==================================================

echo.
echo ==================================
echo JPEG2PDF-OFD 便携版
echo ==================================
echo.
echo 正在启动服务...
echo.

REM 检查 Java 是否安装
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: 未检测到 Java
    echo.
    echo 请先安装 JDK 17 或更高版本
    echo 下载地址: https://adoptium.net/
    echo.
    echo 或运行: choco install openjdk17
    echo.
    pause
    exit /b 1
)

REM 启动服务
start "" java -Xmx2G -jar jpeg2pdf-ofd.jar

REM 等待服务启动
timeout /t 5 /nobreak >nul 2>&1

echo.
echo ✅ 服务已启动
echo.
echo ==================================
echo 访问地址
echo ==================================
echo.
echo   Web 界面: http://localhost:8000
echo   API 文档: http://localhost:8000/swagger-ui.html
echo.
echo ==================================
echo 输出目录
echo ==================================
echo.
echo   P:\OCR\Output\
echo.
echo ==================================
echo 使用说明
echo ==================================
echo.
echo   1. 打开浏览器访问 http://localhost:8000
echo   2. 上传 JPEG/PNG 图片
echo   3. 点击 OCR 识别
echo   4. 导出为 OFD/PDF/Text
echo.
echo   详细说明请查看: 使用说明.md
echo.
echo ==================================
echo 提示
echo ==================================
echo.
echo   - 首次启动可能需要等待 20-30 秒
echo   - 要停止服务，运行: 停止服务.bat
echo   - 按 Ctrl+C 可查看服务日志
echo.
echo ==================================
echo.
