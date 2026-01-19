@echo off
setlocal enabledelayedexpansion

:: 正确的Minecraft启动脚本
:: 设置路径
set "MC_DIR=D:\Eliminate\.minecraft"
set "CLIENT_JAR=%MC_DIR%\versions\1.21.1\1.21.1.jar"
set "LIBRARIES_DIR=%MC_DIR%\libraries"

:: 检查文件
if not exist "%CLIENT_JAR%" (
    echo Error: Minecraft client jar not found
    pause
    exit /b 1
)

:: 构建类路径
set "CLASSPATH=%CLIENT_JAR%"

:: 添加所有库文件
for /r "%LIBRARIES_DIR%" %%f in (*.jar) do (
    set "CLASSPATH=!CLASSPATH!;%%f"
)

:: 启动Minecraft
echo Starting Minecraft 1.21.1...
echo Classpath built with all libraries

title Minecraft 1.21.1
java -Xmx2G -Xms1G -cp "!CLASSPATH!" net.minecraft.client.main.Main ^
    --username TestUser ^
    --version 1.21.1 ^
    --gameDir "%MC_DIR%" ^
    --assetsDir "%MC_DIR%\assets" ^
    --assetIndex 27 ^
    --uuid 00000000-0000-0000-0000-000000000000 ^
    --accessToken 0 ^
    --clientId 0 ^
    --xuid 0 ^
    --versionType release

pause
