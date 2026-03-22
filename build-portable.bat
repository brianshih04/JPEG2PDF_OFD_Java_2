@echo off
REM JPEG2PDF-OFD 便携版打包脚本（无需安装）

echo ==================================
echo JPEG2PDF-OFD 便携版打包工具
echo ==================================
echo.

REM 检查 JDK 版本
java -version 2>&1 | findstr /i "version" >nul
if errorlevel 1 (
    echo ❌ 未检测到 Java，请安装 JDK 17+
    pause
    exit /b 1
)

REM 1. 编译项目
echo.
echo [1/3] 编译项目...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo ❌ 编译失败！
    pause
    exit /b 1
)

echo ✅ 编译成功！

REM 2. 创建输出目录
echo.
echo [2/3] 创建输出目录...
if not exist "dist\JPEG2PDF-OFD-Portable" mkdir "dist\JPEG2PDF-OFD-Portable"

REM 3. 复制文件
echo.
echo [3/3] 生成便携版...

REM 复制 JAR 文件
copy /Y "target\jpeg2pdf-ofd-1.0.0.jar" "dist\JPEG2PDF-OFD-Portable\jpeg2pdf-ofd.jar" >nul

REM 创建启动脚本
echo @echo off > dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo ================================== >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo JPEG2PDF-OFD 服务启动 >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo ================================== >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo. >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo 正在启动服务... >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo start "" java -Xmx2G -jar jpeg2pdf-ofd.jar >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo. >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo ✅ 服务已启动！ >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo 📂 输出目录: P:\OCR\Output >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo 🌐 Web 界面: http://localhost:8000 >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo. >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo echo 按 Ctrl+C 停止服务 >> dist\JPEG2PDF-OFD-Portable\启动服务.bat
echo pause >> dist\JPEG2PDF-OFD-Portable\启动服务.bat

REM 创建停止脚本
echo @echo off > dist\JPEG2PDF-OFD-Portable\停止服务.bat
echo echo 正在停止服务... >> dist\JPEG2PDF-OFD-Portable\停止服务.bat
echo taskkill /F /IM java.exe /FI "WINDOWTITLE eq JPEG2PDF-OFD*" >nul 2>&1 >> dist\JPEG2PDF-OFD-Portable\停止服务.bat
echo echo ✅ 服务已停止 >> dist\JPEG2PDF-OFD-Portable\停止服务.bat
echo pause >> dist\JPEG2PDF-OFD-Portable\停止服务.bat

REM 创建 README
echo # JPEG2PDF-OFD 便携版 > dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo ## 使用方法 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo 1. 双击 "启动服务.bat" 启动服务 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo 2. 浏览器访问 http://localhost:8000 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo 3. 上传图片并进行 OCR 识别 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo 4. 导出为 PDF 或 OFD 格式 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo 5. 使用 "停止服务.bat" 停止服务 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo ## 系统要求 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo - Java 17 或更高版本 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo - Windows 10/11 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo ## 输出目录 >> dist\JPEG2PDF-OFD-Portable\README.txt
echo. >> dist\JPEG2PDF-OFD-Portable\README.txt
echo P:\OCR\Output >> dist\JPEG2PDF-OFD-Portable\README.txt

echo.
echo ✅ 打包完成！
echo.
echo 便携版位置:
echo   dist\JPEG2PDF-OFD-Portable\
echo.
echo 文件列表:
echo   - jpeg2pdf-ofd.jar (主程序)
echo   - 启动服务.bat (启动脚本)
echo   - 停止服务.bat (停止脚本)
echo   - README.txt (使用说明)
echo.
echo 使用方法:
echo   1. 复制整个 JPEG2PDF-OFD-Portable 文件夹到任意位置
echo   2. 双击 "启动服务.bat" 启动服务
echo   3. 浏览器访问 http://localhost:8000
echo.

pause
