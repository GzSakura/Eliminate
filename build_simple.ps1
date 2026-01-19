# 简单构建脚本

# 创建构建目录
New-Item -ItemType Directory -Path "build/mods" -Force

# 复制neoforge版本的代码和资源
$neoforgeSrc = "neoforge/src"
$buildTemp = "build/temp"

New-Item -ItemType Directory -Path $buildTemp -Force

# 复制资源文件
Copy-Item -Path "$neoforgeSrc/client/resources/*" -Destination "$buildTemp/client" -Recurse -Force -ErrorAction SilentlyContinue
Copy-Item -Path "$neoforgeSrc/main/resources/*" -Destination "$buildTemp/main" -Recurse -Force -ErrorAction SilentlyContinue

# 打包为jar
$jarPath = "build/mods/Eliminate-neoforge.jar"
$zipPath = "build/temp.zip"

Compress-Archive -Path "$buildTemp/*" -DestinationPath $zipPath -Force
Move-Item -Path $zipPath -Destination $jarPath -Force

# 显示结果
Write-Host "构建完成！"
Get-ChildItem -Path "build/mods" -Filter "*.jar"
