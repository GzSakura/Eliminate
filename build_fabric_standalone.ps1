# Fabric Build Script (PowerShell)
# This script builds the fabric version of the mod

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Fabric Mod" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set paths
$basePath = "D:\Eliminate"
$fabricPath = "$basePath\fabric"
$buildOutputPath = "$basePath\build\mods"

# Check if fabric directory exists
if (-not (Test-Path $fabricPath)) {
    Write-Host "Error: Fabric directory not found: $fabricPath" -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

Write-Host "Fabric directory: $fabricPath"
Write-Host ""

# Create output directory
New-Item -ItemType Directory -Path $buildOutputPath -Force | Out-Null

# Navigate to fabric directory
Set-Location $fabricPath

Write-Host "Running Gradle build..." -ForegroundColor Yellow
Write-Host ""

# Run Gradle build
$buildResult = & .\gradlew.bat clean build

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Build failed with error code: $LASTEXITCODE" -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Build completed successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Copy built jar to output directory
$jarPath = "$fabricPath\build\libs"
if (Test-Path $jarPath) {
    Write-Host "Copying built jars to: $buildOutputPath"
    Copy-Item -Path "$jarPath\*.jar" -Destination $buildOutputPath -Force
    
    $builtJars = Get-ChildItem -Path $buildOutputPath -Filter "*.jar"
    if ($builtJars.Count -gt 0) {
        Write-Host ""
        Write-Host "Built jars:" -ForegroundColor Cyan
        foreach ($jar in $builtJars) {
            Write-Host "- $($jar.Name)"
        }
    } else {
        Write-Host "Warning: No jars found after copy" -ForegroundColor Yellow
    }
} else {
    Write-Host "Warning: No jars found in build/libs directory" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Fabric build process completed" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Read-Host "Press Enter to exit..."
