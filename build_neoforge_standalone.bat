@echo off
setlocal enabledelayedexpansion

:: Neoforge Build Script
:: This script builds the neoforge version of the mod

echo ========================================
echo Building Neoforge Mod
echo ========================================
echo.

:: Set paths
set "basePath=D:\Eliminate"
set "neoforgePath=%basePath%\neoforge"
set "buildOutputPath=%basePath%\build\mods"

:: Check if neoforge directory exists
if not exist "%neoforgePath%" (
    echo Error: Neoforge directory not found: %neoforgePath%
    pause
    exit /b 1
)

echo Neoforge directory: %neoforgePath%
echo.

:: Create output directory
if not exist "%buildOutputPath%" (
    mkdir "%buildOutputPath%"
)

:: Navigate to neoforge directory
cd /d "%neoforgePath%"

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
set "jarPath=%neoforgePath%\build\libs"
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
echo Neoforge build process completed
echo ========================================
pause
endlocal
