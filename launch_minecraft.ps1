# Simplified Minecraft launcher script

# Set paths
$mcDir = "D:\Eliminate\.minecraft"
$version = "1.21.1"

# Check required files
$clientJar = "$mcDir\versions\$version\$version.jar"
if (-not (Test-Path $clientJar)) {
    Write-Host "Error: Minecraft $version jar not found at $clientJar"
    Read-Host "Press Enter to exit..."
    exit 1
}

# Build classpath
Write-Host "Building classpath..."
$classpath = $clientJar

# Add libraries
$librariesDir = "$mcDir\libraries"
if (Test-Path $librariesDir) {
    $libraryJars = Get-ChildItem $librariesDir -Recurse -Filter "*.jar"
    foreach ($jar in $libraryJars) {
        $classpath += ";$($jar.FullName)"
    }
    Write-Host "Added $($libraryJars.Count) libraries"
} else {
    Write-Host "Warning: Libraries directory not found"
}

# Launch parameters
$launchArgs = @(
    "-Xmx2G",
    "-Xms1G",
    "-cp",
    $classpath,
    "net.minecraft.client.main.Main",
    "--username", "TestUser",
    "--version", $version,
    "--gameDir", $mcDir,
    "--assetsDir", "$mcDir\assets",
    "--assetIndex", "27",
    "--uuid", "00000000-0000-0000-0000-000000000000",
    "--accessToken", "0",
    "--clientId", "0",
    "--xuid", "0",
    "--versionType", "release"
)

# Start Minecraft
Write-Host "Launching Minecraft $version..."
Write-Host "Using classpath: $clientJar;... (libraries)"

try {
    & java $launchArgs
} catch {
    Write-Host "Error launching Minecraft: $($_.Exception.Message)"
    Read-Host "Press Enter to exit..."
}
