@echo off
setlocal enabledelayedexpansion

:: 设置基础路径
set "basePath=D:\Eliminate"
set "cmclJar=%basePath%\cmcl.jar"

:: 检查 cmcl.jar 是否存在
if not exist "%cmclJar%" (
    echo 错误: 找不到 CMCL 启动器: %cmclJar%
    echo 当前目录: %CD%
    pause
    exit /b 1
)

:: 切换到正确的目录
cd /d "%basePath%"

echo 当前工作目录: %CD%
echo CMCL 启动器路径: %cmclJar%
echo.
echo 正在使用 CMCL 启动 1.21.1neoforge 版本...
echo.

:: 使用 CMCL 启动游戏
java -jar "%cmclJar%" 1.21.1neoforge

pause
endlocal
