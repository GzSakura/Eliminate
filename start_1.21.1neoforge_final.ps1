# Final PowerShell script to start 1.21.1neoforge

# Set paths
$basePath = "D:\Eliminate"
$minecraftPath = "$basePath\.minecraft"
$versionPath = "$minecraftPath\versions\1.21.1neoforge"
$nativesPath = "$versionPath\natives-windows-x86_64"
$modsPath = "$versionPath\mods"
$librariesPath = "$minecraftPath\libraries"

# Check required directories
if (-not (Test-Path "$nativesPath")) {
    Write-Host "Error: Natives directory not found: $nativesPath"
    Read-Host "Press Enter to continue..."
    exit 1
}

# Build complete classpath
Write-Host "Building complete classpath..."

# Start with an empty classpath
$classpath = ""

# Add all libraries from the libraries directory
if (Test-Path "$librariesPath") {
    $libraryJars = Get-ChildItem "$librariesPath" -Recurse -Filter "*.jar"
    foreach ($jar in $libraryJars) {
        if ([string]::IsNullOrEmpty($classpath)) {
            $classpath = $jar.FullName
        } else {
            $classpath += ";$($jar.FullName)"
        }
    }
    Write-Host "Added $($libraryJars.Count) library jars"
}

# Add mods
if (Test-Path "$modsPath") {
    $modFiles = Get-ChildItem "$modsPath" -Filter "*.jar"
    foreach ($mod in $modFiles) {
        $classpath += ";$($mod.FullName)"
    }
    Write-Host "Added $($modFiles.Count) mods"
}

# Add the neoforge jar itself
$neoforgeJar = "$versionPath\1.21.1neoforge.jar"
if (Test-Path "$neoforgeJar") {
    $classpath += ";$neoforgeJar"
    Write-Host "Added neoforge jar"
} else {
    Write-Host "Warning: Neoforge jar not found, using only libraries"
}

Write-Host "Total classpath entries: $($classpath.Split(';').Count)"

# Start Minecraft with the correct Neoforge launcher
Write-Host "Starting 1.21.1neoforge with Neoforge Launcher..."
Write-Host "Using natives directory: $nativesPath"

# Launch command with the correct main class
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

Write-Host "Launching with Neoforge Launcher..."

# Execute Java
try {
    & java $javaArgs
} catch {
    Write-Host "Error launching Minecraft: $($_.Exception.Message)"
    Read-Host "Press Enter to continue..."
}

Read-Host "Press Enter to continue..."
