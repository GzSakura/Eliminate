@echo off
setlocal enabledelayedexpansion

:: Fabric Build Script
:: This script builds the fabric version of the mod

echo ========================================
echo Building Fabric Mod
echo ========================================
echo.

:: Set paths
set "basePath=D:\Eliminate"
set "fabricPath=%basePath%\fabric"
set "buildOutputPath=%basePath%\build\mods"

:: Check if fabric directory exists
if not exist "%fabricPath%" (
    echo Error: Fabric directory not found: %fabricPath%
    pause
    exit /b 1
)

echo Fabric directory: %fabricPath%
echo.

:: Create output directory
if not exist "%buildOutputPath%" (
    mkdir "%buildOutputPath%"
)

:: Navigate to fabric directory
cd /d "%fabricPath%"

echo Running Gradle build...
echo.

:: Run Gradle build
call gradlew.bat clean build

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed with error code: %ERRORLEVEL%
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.

:: Copy built jar to output directory
set "jarPath=%fabricPath%\build\libs"
if exist "%jarPath%" (
    echo Copying built jars to: %buildOutputPath%
    copy /Y "%jarPath%\*.jar" "%buildOutputPath%\" >nul
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Built jars:
        dir /B "%buildOutputPath%\*.jar"
    ) else (
        echo Warning: Failed to copy jars
    )
) else (
    echo Warning: No jars found in build/libs directory
)

echo.
echo ========================================
echo Fabric build process completed
echo ========================================
pause
endlocal
