@echo off
setlocal enabledelayedexpansion

:: 设置路径
set "MC_DIR=D:liminateminecraft"
set "CLIENT_JAR=!MC_DIR!\versions\1.21.1\1.21.1.jar"
set "LIBRARIES_DIR=!MC_DIR!\libraries"

:: 构建类路径 - 先添加jopt-simple，然后是其他库，最后是客户端jar
set "CLASSPATH="

:: 先添加jopt-simple
for /r "!LIBRARIES_DIR!" %%f in (*jopt*simple*.jar) do (
    set "CLASSPATH=%%f"
    echo Added jopt-simple: %%f
    goto :AddOtherLibs
)

:AddOtherLibs
:: 添加其他库
for /r "!LIBRARIES_DIR!" %%f in (*.jar) do (
    if "%%~nxf" neq "jopt-simple-5.0.4.jar" (
        set "CLASSPATH=!CLASSPATH!;%%f"
    )
)

:: 添加客户端jar
set "CLASSPATH=!CLASSPATH!;!CLIENT_JAR!"

echo Launching Minecraft with jopt-simple first in classpath...

:: 启动Minecraft
java -Xmx2G -Xms1G -cp "!CLASSPATH!" net.minecraft.client.main.Main ^
    --username TestUser ^
    --version 1.21.1 ^
    --gameDir "!MC_DIR!" ^
    --assetsDir "!MC_DIR!\assets" ^
    --assetIndex 27 ^
    --uuid 00000000-0000-0000-0000-000000000000 ^
    --accessToken 0 ^
    --clientId 0 ^
    --xuid 0 ^
    --versionType release

pause
