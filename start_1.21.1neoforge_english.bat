@echo off
setlocal enabledelayedexpansion

:: Set paths
set "basePath=D:\Eliminate"
set "minecraftPath=%basePath%\.minecraft"
set "versionPath=%minecraftPath%\versions\1.21.1neoforge"
set "nativesPath=%versionPath%\natives-windows-x86_64"
set "modsPath=%versionPath%\mods"
set "neoforgeJar=%versionPath%\1.21.1neoforge.jar"

:: Check required files
if not exist "%neoforgeJar" (
    echo Error: Neoforge jar file not found: %neoforgeJar%
    pause
    exit /b 1
)

if not exist "%nativesPath" (
    echo Error: Natives directory not found: %nativesPath%
    pause
    exit /b 1
)

:: Build classpath
set "classpath=%neoforgeJar%"

:: Add mods to classpath
if exist "%modsPath" (
    for %%f in ("%modsPath%\*.jar") do (
        set "classpath=!classpath!;%%f"
    )
)

:: Start Minecraft
echo Starting 1.21.1neoforge version...
echo Using classpath: %classpath%
echo Using natives directory: %nativesPath%

:: Launch command
java -Xmx2G -Xms1G -Djava.library.path="%nativesPath%" -Djna.tmpdir="%nativesPath%" -Dorg.lwjgl.system.SharedLibraryExtractPath="%nativesPath%" -Dio.netty.native.workdir="%nativesPath%" -Dminecraft.launcher.brand=CMCL -Dminecraft.launcher.version=3.5.2 -cp "%classpath%" net.neoforged.neoforge.loading.neoforge.Launcher --username TestUser --version 1.21.1neoforge --gameDir "%minecraftPath%" --assetsDir "%minecraftPath%\assets" --assetIndex 27 --uuid 00000000-0000-0000-0000-000000000000 --accessToken 0 --clientId 0 --xuid 0 --versionType release

pause
endlocal