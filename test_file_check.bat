@echo off
echo 测试文件存在性检查
echo 检查文件: D:\Eliminate\.minecraft\versions\1.21.1neoforge\1.21.1neoforge.jar
if exist "D:\Eliminate\.minecraft\versions\1.21.1neoforge\1.21.1neoforge.jar" (
    echo 文件存在!
) else (
    echo 文件不存在!
)
pause