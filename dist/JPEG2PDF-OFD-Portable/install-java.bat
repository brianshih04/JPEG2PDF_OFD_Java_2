@echo off
REM ==================================================
REM Java 安装辅助脚本
REM ==================================================

echo.
echo ==================================
echo Java 安装辅助工具
echo ==================================
echo.

REM 检查是否已安装
java -version >nul 2>&1
if errorlevel 1 (
    echo 未检测到 Java，正在安装...
    echo.
    
    REM 检查 Chocolatey
    choco -v >nul 2>&1
    if errorlevel 1 (
        echo 正在安装 Chocolatey...
        powershell -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"
        echo.
    )
    
    echo 正在安装 OpenJDK 17...
    choco install openjdk17 -y
    echo.
    
    echo 刷新环境变量...
    refreshenv >nul 2>&1
    
    echo.
    echo ✅ 安装完成！
    echo.
    echo 请关闭此窗口，然后重新运行 "启动服务.bat"
    echo.
) else (
    echo ✅ 已安装 Java:
    java -version
    echo.
    echo 无需安装，可以直接运行 "启动服务.bat"
    echo.
)

pause
