@echo off
setlocal enabledelayedexpansion

:: 设置路径
set "MC_DIR=D:\Eliminate\.minecraft"
set "VERSION=1.21.1"

:: 检查文件
if not exist "%MC_DIR%\versions\%VERSION%\%VERSION%.jar" (
    echo Error: Minecraft %VERSION% jar not found
    pause
    exit /b 1
)

:: 构建类路径
set "CP=%MC_DIR%\versions\%VERSION%\%VERSION%.jar"

:: 添加库文件
for /r "%MC_DIR%\libraries" %%f in (*.jar) do (
    set "CP=!CP!;%%f"
)

:: 启动Minecraft
echo Starting Minecraft %VERSION%...
echo Classpath prepared with libraries

title Minecraft %VERSION%
java -Xmx2G -Xms1G -cp "!CP!" net.minecraft.client.main.Main ^
    --username TestUser ^
    --version %VERSION% ^
    --gameDir "%MC_DIR%" ^
    --assetsDir "%MC_DIR%\assets" ^
    --assetIndex 27 ^
    --uuid 00000000-0000-0000-0000-000000000000 ^
    --accessToken 0 ^
    --clientId 0 ^
    --xuid 0 ^
    --versionType release

pause
