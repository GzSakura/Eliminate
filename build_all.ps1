# Unified Build Script
# This script builds both neoforge and fabric versions of the mod

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building All Mods" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set paths
$basePath = "D:\Eliminate"
$neoforgePath = "$basePath\neoforge"
$fabricPath = "$basePath\fabric"
$buildOutputPath = "$basePath\build\mods"

# Create output directory
New-Item -ItemType Directory -Path $buildOutputPath -Force | Out-Null

# Build neoforge
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Neoforge Mod" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (Test-Path $neoforgePath) {
    Write-Host "Neoforge directory: $neoforgePath"
    Set-Location $neoforgePath
    
    Write-Host "Running Gradle build..." -ForegroundColor Yellow
    $neoforgeResult = & .\gradlew.bat clean build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Neoforge build completed successfully!" -ForegroundColor Green
        
        # Copy built jar to output directory
        $jarPath = "$neoforgePath\build\libs"
        if (Test-Path $jarPath) {
            Write-Host "Copying built jars to: $buildOutputPath"
            Copy-Item -Path "$jarPath\*.jar" -Destination $buildOutputPath -Force
        }
    } else {
        Write-Host "Neoforge build failed with error code: $LASTEXITCODE" -ForegroundColor Red
    }
} else {
    Write-Host "Warning: Neoforge directory not found: $neoforgePath" -ForegroundColor Yellow
}

Write-Host ""
Write-Host ""

# Build fabric
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Building Fabric Mod" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

if (Test-Path $fabricPath) {
    Write-Host "Fabric directory: $fabricPath"
    Set-Location $fabricPath
    
    Write-Host "Running Gradle build..." -ForegroundColor Yellow
    $fabricResult = & .\gradlew.bat clean build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Fabric build completed successfully!" -ForegroundColor Green
        
        # Copy built jar to output directory
        $jarPath = "$fabricPath\build\libs"
        if (Test-Path $jarPath) {
            Write-Host "Copying built jars to: $buildOutputPath"
            Copy-Item -Path "$jarPath\*.jar" -Destination $buildOutputPath -Force
        }
    } else {
        Write-Host "Fabric build failed with error code: $LASTEXITCODE" -ForegroundColor Red
    }
} else {
    Write-Host "Warning: Fabric directory not found: $fabricPath" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Build Summary" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Show all built jars
$builtJars = Get-ChildItem -Path $buildOutputPath -Filter "*.jar"
if ($builtJars.Count -gt 0) {
    Write-Host "All built jars:" -ForegroundColor Cyan
    foreach ($jar in $builtJars) {
        Write-Host "- $($jar.Name)"
    }
} else {
    Write-Host "No jars found in build output directory" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "All builds completed" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Read-Host "Press Enter to exit..."
