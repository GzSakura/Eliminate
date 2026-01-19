# Simple PowerShell script to start vanilla 1.21.1

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

# Build classpath
Write-Host "Building classpath..."
$classpath = "$clientJar"

# Add libraries
if (Test-Path "$librariesPath") {
    $libraryJars = Get-ChildItem "$librariesPath" -Recurse -Filter "*.jar"
    foreach ($jar in $libraryJars) {
        $classpath += ";$($jar.FullName)"
    }
    Write-Host "Added $($libraryJars.Count) libraries"
}

# Start Minecraft
Write-Host "Starting Minecraft 1.21.1..."

# Launch parameters
$args = @(
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
Write-Host "Launching with: java $($args -join ' ' | Select-Object -First 200)"
try {
    & java $args
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    Read-Host "Press Enter to continue..."
}
