@echo off
REM ==================================================
REM JPEG2PDF-OFD 便携版停止脚本
REM ==================================================

echo.
echo ==================================
echo JPEG2PDF-OFD 便携版
echo ==================================
echo.
echo 正在停止服务...
echo.

REM 查找 Java 进程
for /f "tokens=2" %%i in ('jps -l ^| findstr jpeg2pdf-ofd.jar') do (
    echo 找到进程: %%i
    taskkill /F /PID %%i >nul 2>&1
    if errorlevel 1 (
        echo ⚠️  停止进程失败，可能需要管理员权限
    ) else (
        echo ✅ 服务已停止
    )
    goto :end
)

echo ⚠️  未找到运行中的服务
echo.
echo 提示: 如果服务未正常停止，请：
echo   1. 打开任务管理器
echo   2. 找到 java.exe 进程
echo   3. 手动结束进程
echo.

:end
echo.
echo ==================================
echo.
pause
