@echo off

:: 简化的启动脚本 - 仅启动普通Minecraft 1.21.1

:: 设置基础路径
set "basePath=D:\Eliminate"
set "minecraftPath=%basePath%\.minecraft"

:: 检查必要文件
if not exist "%minecraftPath%\versions\1.21.1\1.21.1.jar" (
    echo 错误：找不到Minecraft 1.21.1客户端文件
    pause
    exit /b 1
)

:: 构建类路径
set "classpath=%minecraftPath%\versions\1.21.1\1.21.1.jar"

:: 添加所有库文件
for /r "%minecraftPath%\libraries" %%f in (*.jar) do (
    set "classpath=!classpath!;%%f"
)

:: 启动Minecraft
echo 正在启动Minecraft 1.21.1...
echo 类路径包含：%classpath:;= & echo 包含：%

title Minecraft 1.21.1
java -Xmx2G -Xms1G -cp "%classpath%" net.minecraft.client.main.Main ^
    --username TestUser ^
    --version 1.21.1 ^
    --gameDir "%minecraftPath%" ^
    --assetsDir "%minecraftPath%\assets" ^
    --assetIndex 27 ^
    --uuid 00000000-0000-0000-0000-000000000000 ^
    --accessToken 0 ^
    --clientId 0 ^
    --xuid 0 ^
    --versionType release

pause
