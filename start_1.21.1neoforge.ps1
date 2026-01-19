# PowerShell script to start 1.21.1neoforge

# Set paths
$basePath = "D:\Eliminate"
$minecraftPath = "$basePath\.minecraft"
$versionPath = "$minecraftPath\versions\1.21.1neoforge"
$nativesPath = "$versionPath\natives-windows-x86_64"
$modsPath = "$versionPath\mods"
$neoforgeJar = "$versionPath\1.21.1neoforge.jar"

# Check required files
if (-not (Test-Path "$neoforgeJar")) {
    Write-Host "Error: Neoforge jar file not found: $neoforgeJar"
    Read-Host "Press Enter to continue..."
    exit 1
}

if (-not (Test-Path "$nativesPath")) {
    Write-Host "Error: Natives directory not found: $nativesPath"
    Read-Host "Press Enter to continue..."
    exit 1
}

# Build classpath
$classpath = $neoforgeJar

# Add mods to classpath
if (Test-Path "$modsPath") {
    $modFiles = Get-ChildItem "$modsPath" -Filter "*.jar"
    foreach ($mod in $modFiles) {
        $classpath += ";$($mod.FullName)"
    }
}

# Start Minecraft
Write-Host "Starting 1.21.1neoforge version..."
Write-Host "Using classpath: $classpath"
Write-Host "Using natives directory: $nativesPath"

# Launch command
$javaArgs = @(
    "-Xmx2G",
    "-Xms1G",
    "-Djava.library.path=$nativesPath",
    "-Djna.tmpdir=$nativesPath",
    "-Dorg.lwjgl.system.SharedLibraryExtractPath=$nativesPath",
    "-Dio.netty.native.workdir=$nativesPath",
    "-Dminecraft.launcher.brand=CMCL",
    "-Dminecraft.launcher.version=3.5.2",
    "-cp",
    "$classpath",
    "net.neoforged.neoforge.loading.neoforge.Launcher",
    "--username", "TestUser",
    "--version", "1.21.1neoforge",
    "--gameDir", "$minecraftPath",
    "--assetsDir", "$minecraftPath\assets",
    "--assetIndex", "27",
    "--uuid", "00000000-0000-0000-0000-000000000000",
    "--accessToken", "0",
    "--clientId", "0",
    "--xuid", "0",
    "--versionType", "release"
)

Write-Host "Launching with command: java $($javaArgs -join ' ' )"

# Execute Java
try {
    & java $javaArgs
} catch {
    Write-Host "Error launching Minecraft: $($_.Exception.Message)"
}

Read-Host "Press Enter to continue..."
