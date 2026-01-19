@echo off

:: 手动启动脚本
echo 正在启动 Minecraft 1.21.1neoforge...

:: 设置路径
set minecraftPath=%CD%\.minecraft
set versionPath=%minecraftPath%\versions\1.21.1neoforge
set modsPath=%versionPath%\mods
set inheritVersionPath=%minecraftPath%\versions\1.21.10
set clientJar=%inheritVersionPath%\1.21.10.jar

:: 构建类路径
set classpath=%clientJar%

:: 添加模组到类路径
if exist "%modsPath%" (
    for %%f in ("%modsPath%\*.jar") do (
        set classpath=!classpath!;"%%f"
    )
)

:: 启动命令
echo 使用类路径: %classpath%
echo 正在启动...

java -Xmx2G -Xms1G -cp "%classpath%" net.minecraft.client.main.Main --username TestUser --version 1.21.1neoforge --gameDir "%minecraftPath%" --assetsDir "%minecraftPath%\assets" --assetIndex 27 --uuid 00000000-0000-0000-0000-000000000000 --accessToken 0 --clientId 0 --xuid 0 --versionType release

pause