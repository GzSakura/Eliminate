# PowerShell script to start 1.21.1neoforge with all dependencies

# Set paths
$basePath = "D:\Eliminate"
$minecraftPath = "$basePath\.minecraft"
$versionPath = "$minecraftPath\versions\1.21.1neoforge"
$nativesPath = "$versionPath\natives-windows-x86_64"
$modsPath = "$versionPath\mods"
$neoforgeJar = "$versionPath\1.21.1neoforge.jar"
$librariesPath = "$minecraftPath\libraries"

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

# Build classpath with all dependencies
Write-Host "Building classpath with all dependencies..."

# Start with the neoforge jar
$classpath = $neoforgeJar

# Add all libraries from the libraries directory
if (Test-Path "$librariesPath") {
    $libraryJars = Get-ChildItem "$librariesPath" -Recurse -Filter "*.jar"
    foreach ($jar in $libraryJars) {
        $classpath += ";$($jar.FullName)"
    }
    Write-Host "Added $($libraryJars.Count) library jars to classpath"
}

# Add mods to classpath
if (Test-Path "$modsPath") {
    $modFiles = Get-ChildItem "$modsPath" -Filter "*.jar"
    foreach ($mod in $modFiles) {
        $classpath += ";$($mod.FullName)"
    }
    Write-Host "Added $($modFiles.Count) mods to classpath"
}

# Start Minecraft
Write-Host "Starting 1.21.1neoforge version..."
Write-Host "Using classpath with $($classpath.Split(';').Count) entries"
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
    "net.minecraft.client.main.Main",
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

Write-Host "Launching Minecraft with complete classpath..."

# Execute Java
try {
    & java $javaArgs
} catch {
    Write-Host "Error launching Minecraft: $($_.Exception.Message)"
    Read-Host "Press Enter to continue..."
}

Read-Host "Press Enter to continue..."
