@echo off
chcp 65001 >nul

echo 正在停止服務...

REM 停止 Java 進程
taskkill /F /FI "WINDOWTITLE eq JPEG2PDF-OFD Service*" >nul 2>&1

echo ✅ 服務已停止
echo.
pause
