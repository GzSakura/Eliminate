# Alternative Minecraft launcher that bypasses jopt-simple issues

Write-Host "Starting Minecraft with alternative launcher..."

# Set paths
$mcDir = "D:\Eliminate\.minecraft"
$version = "1.21.1"
$clientJar = "$mcDir\versions\$version\$version.jar"

# Build classpath
$classpath = $clientJar

# Add libraries except jopt-simple
$librariesDir = "$mcDir\libraries"
if (Test-Path $librariesDir) {
    $libraryJars = Get-ChildItem $librariesDir -Recurse -Filter "*.jar" | Where-Object {
        $_.Name -notlike "*jopt*simple*"
    }
    foreach ($jar in $libraryJars) {
        $classpath += ";$($jar.FullName)"
    }
    Write-Host "Added $($libraryJars.Count) libraries (excluding jopt-simple)"
}

# Try to launch with minimal parameters
Write-Host "Launching Minecraft..."

try {
    & java -Xmx2G -Xms1G -cp "$classpath" net.minecraft.client.main.Main
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
