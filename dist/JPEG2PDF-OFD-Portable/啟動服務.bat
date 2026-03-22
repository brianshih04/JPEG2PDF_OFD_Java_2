@echo off
chcp 65001 >nul
title JPEG2PDF-OFD

echo ========================================
echo   JPEG2PDF-OFD - OCR 轉換工具
echo ========================================
echo.

REM 檢查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 錯誤：未檢測到 Java
    echo.
    echo 請安裝 JDK 17 或更高版本
    echo 下載地址：https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo ✅ Java 環境檢測通過
echo.
echo 🚀 正在啟動服務...
echo.
echo ⏳ 請稍候，服務啟動需要 10-20 秒
echo.

REM 啟動 Java 應用
start "JPEG2PDF-OFD Service" java -Xmx2G -jar jpeg2pdf-ofd-1.0.0.jar

REM 等待服務啟動
timeout /t 20 /nobreak >nul

REM 檢查服務
netstat -ano | findstr ":8000.*LISTENING" >nul
if errorlevel 1 (
    echo.
    echo ❌ 服務啟動失敗
    pause
    exit /b 1
)

echo.
echo ✅ 服務啟動成功！
echo.
echo 🌐 訪問：http://localhost:8000
echo.
echo 按 Ctrl+C 可停止服務
echo.

REM 打開瀏覽器
start http://localhost:8000

pause
