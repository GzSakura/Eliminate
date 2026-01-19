# Start Minecraft 1.21.1neoforge with automatic Y input
Write-Host "Starting Minecraft 1.21.1neoforge..."

# Create input file with 'Y'
$inputFile = "$env:TEMP\cmcl_input.txt"
"Y" | Out-File -FilePath $inputFile -Force

# Start CMCL with input redirection
$process = Start-Process -FilePath "java" -ArgumentList "-jar", "cmcl.jar", "1.21.1neoforge" -RedirectStandardInput $inputFile -NoNewWindow -PassThru

# Wait for process to exit
$process.WaitForExit()

Write-Host "Game exited with code: $($process.ExitCode)"
