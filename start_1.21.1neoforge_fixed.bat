@echo off
setlocal enabledelayedexpansion

:: 设置路径
set "basePath=D:\Eliminate"
set "minecraftPath=%basePath%\.minecraft"
set "versionPath=%minecraftPath%\versions\1.21.1neoforge"
set "nativesPath=%versionPath%\natives-windows-x86_64"
set "modsPath=%versionPath%\mods"
set "neoforgeJar=%versionPath%\1.21.1neoforge.jar"
set "inheritVersionPath=%minecraftPath%\versions\1.21.1"
set "clientJar=%inheritVersionPath%\1.21.1.jar"

:: 检查必要文件是否存在
if not exist "%neoforgeJar" (
    echo 错误: 找不到Neoforge客户端jar文件: %neoforgeJar%
    pause
    exit /b 1
)

if not exist "%nativesPath" (
    echo 错误: 找不到本地库文件目录: %nativesPath%
    pause
    exit /b 1
)

:: 构建类路径
set "classpath=%neoforgeJar%"

:: 添加模组到类路径
if exist "%modsPath" (
    for %%f in ("%modsPath%\*.jar") do (
        set "classpath=!classpath!;%%f"
    )
)

:: 启动Minecraft
echo 正在启动1.21.1neoforge版本...
echo 使用类路径: %classpath%
echo 使用本地库目录: %nativesPath%

:: 启动命令
java -Xmx2G -Xms1G -Djava.library.path="%nativesPath%" -Djna.tmpdir="%nativesPath%" -Dorg.lwjgl.system.SharedLibraryExtractPath="%nativesPath%" -Dio.netty.native.workdir="%nativesPath%" -Dminecraft.launcher.brand=CMCL -Dminecraft.launcher.version=3.5.2 -cp "%classpath%" net.neoforged.neoforge.loading.neoforge.Launcher --username TestUser --version 1.21.1neoforge --gameDir "%minecraftPath%" --assetsDir "%minecraftPath%\assets" --assetIndex 27 --uuid 00000000-0000-0000-0000-000000000000 --accessToken 0 --clientId 0 --xuid 0 --versionType release

pause
endlocal