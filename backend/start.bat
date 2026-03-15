@echo off
chcp 65001
echo ================================
echo   校园书环 - 后端服务启动脚本
echo ================================
echo.

cd /d "%~dp0"

echo [1/2] 正在编译项目...
call mvnw.cmd clean compile -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [错误] 编译失败！请检查代码。
    pause
    exit /b 1
)

echo.
echo [2/2] 正在启动服务...
call mvnw.cmd spring-boot:run

pause
