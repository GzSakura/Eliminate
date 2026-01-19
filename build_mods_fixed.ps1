# 构建脚本 - 编译并打包所有版本的模组为 jar 文件

# 定义路径
$basePath = "D:\Eliminate"
$buildPath = "$basePath\build"
$modsPath = "$buildPath\mods"

# 创建目录结构
New-Item -ItemType Directory -Path $buildPath -Force
New-Item -ItemType Directory -Path $modsPath -Force

Write-Host "Created build/mods directory structure"

# Process one version as test
$versionName = "v1.21.10-fabric"
$versionDir = "$basePath\versions\$versionName"
Write-Host "Processing version: $versionName"

# Check if src directory exists
$srcPath = "$versionDir\src"
if (Test-Path $srcPath) {
    # Check if bin directory exists, create if not
    $binPath = "$versionDir\bin"
    New-Item -ItemType Directory -Path $binPath -Force
    
    # Compile client code
    $clientSrcPath = "$srcPath\client\java"
    if (Test-Path $clientSrcPath) {
        Write-Host "Compiling client code"
        $clientBinPath = "$binPath\client"
        New-Item -ItemType Directory -Path $clientBinPath -Force
        
        # Copy client resources
        $clientResourcesPath = "$srcPath\client\resources"
        if (Test-Path $clientResourcesPath) {
            Copy-Item -Path "$clientResourcesPath\*" -Destination $clientBinPath -Recurse -Force
            Write-Host "Client resources copied successfully"
        }
    }
    
    # Compile server code
    $mainSrcPath = "$srcPath\main\java"
    if (Test-Path $mainSrcPath) {
        Write-Host "Compiling server code"
        $mainBinPath = "$binPath\main"
        New-Item -ItemType Directory -Path $mainBinPath -Force
        
        # Copy server resources
        $mainResourcesPath = "$srcPath\main\resources"
        if (Test-Path $mainResourcesPath) {
            Copy-Item -Path "$mainResourcesPath\*" -Destination $mainBinPath -Recurse -Force
            Write-Host "Server resources copied successfully"
        }
    }
    
    # Create temp directory for packaging
    $tempPath = "$buildPath\temp\$versionName"
    New-Item -ItemType Directory -Path $tempPath -Force -ErrorAction SilentlyContinue
    
    # Copy all files from bin directory to temp directory
    Copy-Item -Path "$binPath\*" -Destination $tempPath -Recurse -Force
    
    # Package as jar file
    $jarFileName = "Eliminate-$versionName.jar"
    $jarPath = "$modsPath\$jarFileName"
    $zipPath = "$buildPath\temp\$versionName.zip"
    
    # Compress to zip file and rename to jar
    Compress-Archive -Path "$tempPath\*" -DestinationPath $zipPath -Force
    Move-Item -Path $zipPath -Destination $jarPath -Force
    
    Write-Host "Version $versionName packaged successfully: $jarFileName"
} else {
    Write-Host "src directory does not exist, skipping this version"
}

# Check build results
Write-Host "Checking build results..."
if (Test-Path $modsPath) {
    Write-Host "build/mods directory exists"
    $modFiles = Get-ChildItem -Path $modsPath -Filter "*.jar"
    if ($modFiles.Count -gt 0) {
        Write-Host "Build products:"
        foreach ($modFile in $modFiles) {
            Write-Host "- $($modFile.Name)"
        }
        Write-Host "Build successful!"
    } else {
        Write-Host "No build products found, please check build process."
    }
} else {
    Write-Host "build/mods directory does not exist, please check build process."
}