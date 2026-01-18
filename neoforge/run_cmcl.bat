@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0.."

echo Current directory: %CD%
echo CMCL launcher path: %CD%\cmcl.jar
echo.

if not exist "cmcl.jar" (
    echo Error: CMCL launcher not found: %CD%\cmcl.jar
    pause
    exit /b 1
)

java -jar cmcl.jar %*

pause
endlocal
