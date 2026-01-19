@echo off
setlocal enabledelayedexpansion

:: Unified Build Script (Batch)
:: This script builds both neoforge and fabric versions of the mod

echo ========================================
echo Building All Mods
echo ========================================
echo.

:: Set paths
set "basePath=D:\Eliminate"
set "neoforgePath=%basePath%\neoforge"
set "fabricPath=%basePath%\fabric"
set "buildOutputPath=%basePath%\build\mods"

:: Create output directory
if not exist "%buildOutputPath%" (
    mkdir "%buildOutputPath%"
)

:: Build neoforge
echo ========================================
echo Building Neoforge Mod
echo ========================================
echo.

if exist "%neoforgePath%" (
    echo Neoforge directory: %neoforgePath%
    cd /d "%neoforgePath%"
    
    echo Running Gradle build...
    call gradlew.bat clean build
    
    if %ERRORLEVEL% EQU 0 (
        echo Neoforge build completed successfully!
        
        :: Copy built jar to output directory
        set "jarPath=%neoforgePath%\build\libs"
        if exist "%jarPath%" (
            echo Copying built jars to: %buildOutputPath%
            copy /Y "%jarPath%\*.jar" "%buildOutputPath%\" >nul
        )
    ) else (
        echo Neoforge build failed with error code: %ERRORLEVEL%
    )
) else (
    echo Warning: Neoforge directory not found: %neoforgePath%
)

echo.
echo.

:: Build fabric
echo ========================================
echo Building Fabric Mod
echo ========================================
echo.

if exist "%fabricPath%" (
    echo Fabric directory: %fabricPath%
    cd /d "%fabricPath%"
    
    echo Running Gradle build...
    call gradlew.bat clean build
    
    if %ERRORLEVEL% EQU 0 (
        echo Fabric build completed successfully!
        
        :: Copy built jar to output directory
        set "jarPath=%fabricPath%\build\libs"
        if exist "%jarPath%" (
            echo Copying built jars to: %buildOutputPath%
            copy /Y "%jarPath%\*.jar" "%buildOutputPath%\" >nul
        )
    ) else (
        echo Fabric build failed with error code: %ERRORLEVEL%
    )
) else (
    echo Warning: Fabric directory not found: %fabricPath%
)

echo.
echo ========================================
echo Build Summary
echo ========================================
echo.

:: Show all built jars
dir /B "%buildOutputPath%\*.jar" 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo No jars found in build output directory
)

echo.
echo ========================================
echo All builds completed
echo ========================================
pause
endlocal
