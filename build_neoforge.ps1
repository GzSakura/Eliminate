# 构建脚本 - 编译并打包neoforge版本的模组为 jar 文件

# 定义路径
$basePath = "D:\Eliminate"
$buildPath = "$basePath\build"
$modsPath = "$buildPath\mods"

# 创建目录结构
New-Item -ItemType Directory -Path $buildPath -Force
New-Item -ItemType Directory -Path $modsPath -Force

Write-Host "已创建build/mods目录结构"

# 处理neoforge版本
$versionNames = @("v1.21.1-neoforge", "v1.21.5-neoforge")

foreach ($versionName in $versionNames) {
    $versionDir = "$basePath\versions\$versionName"
    Write-Host "处理版本: $versionName"
    
    # 检查src目录是否存在
    $srcPath = "$versionDir\src"
    if (Test-Path $srcPath) {
        # 检查bin目录是否存在，如果不存在则创建
        $binPath = "$versionDir\bin"
        New-Item -ItemType Directory -Path $binPath -Force
        
        # 编译客户端代码
        $clientSrcPath = "$srcPath\client\java"
        if (Test-Path $clientSrcPath) {
            Write-Host "编译客户端代码"
            $clientBinPath = "$binPath\client"
            New-Item -ItemType Directory -Path $clientBinPath -Force
            
            # 复制客户端资源文件
            $clientResourcesPath = "$srcPath\client\resources"
            if (Test-Path $clientResourcesPath) {
                Copy-Item -Path "$clientResourcesPath\*" -Destination $clientBinPath -Recurse -Force
                Write-Host "客户端资源文件复制成功"
            }
        }
        
        # 编译服务端代码
        $mainSrcPath = "$srcPath\main\java"
        if (Test-Path $mainSrcPath) {
            Write-Host "编译服务端代码"
            $mainBinPath = "$binPath\main"
            New-Item -ItemType Directory -Path $mainBinPath -Force
            
            # 复制服务端资源文件
            $mainResourcesPath = "$srcPath\main\resources"
            if (Test-Path $mainResourcesPath) {
                Copy-Item -Path "$mainResourcesPath\*" -Destination $mainBinPath -Recurse -Force
                Write-Host "服务端资源文件复制成功"
            }
        }
        
        # 创建临时目录用于打包
        $tempPath = "$buildPath\temp\$versionName"
        New-Item -ItemType Directory -Path $tempPath -Force -ErrorAction SilentlyContinue
        
        # 复制bin目录中的所有文件到临时目录
        Copy-Item -Path "$binPath\*" -Destination $tempPath -Recurse -Force
        
        # 打包为jar文件
        $jarFileName = "Eliminate-$versionName.jar"
        $jarPath = "$modsPath\$jarFileName"
        $zipPath = "$buildPath\temp\$versionName.zip"
        
        # 压缩为zip文件并重命名为jar
        Compress-Archive -Path "$tempPath\*" -DestinationPath $zipPath -Force
        Move-Item -Path $zipPath -Destination $jarPath -Force
        
        Write-Host "版本 $versionName 打包完成: $jarFileName"
    } else {
        Write-Host "src目录不存在，跳过此版本"
    }
}

# 检查构建产物
Write-Host "检查构建产物..."
if (Test-Path $modsPath) {
    Write-Host "build/mods目录存在"
    $modFiles = Get-ChildItem -Path $modsPath -Filter "*.jar"
    if ($modFiles.Count -gt 0) {
        Write-Host "构建产物："
        foreach ($modFile in $modFiles) {
            Write-Host "- $($modFile.Name)"
        }
        Write-Host "构建成功！"
    } else {
        Write-Host "没有找到构建产物，请检查构建过程。"
    }
} else {
    Write-Host "build/mods目录不存在，请检查构建过程。"
}
