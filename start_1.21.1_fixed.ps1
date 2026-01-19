# Fixed PowerShell script to start Minecraft 1.21.1

# Set paths
$basePath = "D:\Eliminate"
$minecraftPath = "$basePath\.minecraft"
$versionPath = "$minecraftPath\versions\1.21.1"
$librariesPath = "$minecraftPath\libraries"

# Check required files
$clientJar = "$versionPath\1.21.1.jar"
if (-not (Test-Path "$clientJar")) {
    Write-Host "Error: Minecraft client jar not found: $clientJar"
    Read-Host "Press Enter to continue..."
    exit 1
}

# Build complete classpath
Write-Host "Building complete classpath..."
$classpath = "$clientJar"

# Add all libraries
if (Test-Path "$librariesPath") {
    $libraryJars = Get-ChildItem "$librariesPath" -Recurse -Filter "*.jar"
    foreach ($jar in $libraryJars) {
        $classpath += ";$($jar.FullName)"
    }
    Write-Host "Added $($libraryJars.Count) libraries"
}

# Start Minecraft with all required parameters
Write-Host "Starting Minecraft 1.21.1..."

# Full launch parameters
$launchArgs = @(
    "-Xmx2G",
    "-Xms1G",
    "-cp",
    "$classpath",
    "net.minecraft.client.main.Main",
    "--username", "TestUser",
    "--version", "1.21.1",
    "--gameDir", "$minecraftPath",
    "--assetsDir", "$minecraftPath\assets",
    "--assetIndex", "27",
    "--uuid", "00000000-0000-0000-0000-000000000000",
    "--accessToken", "0",
    "--clientId", "0",
    "--xuid", "0",
    "--versionType", "release"
)

# Execute
Write-Host "Launching Minecraft..."
try {
    & java $launchArgs
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Read-Host "Press Enter to continue..."
}
