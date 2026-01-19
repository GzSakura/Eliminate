# Start Minecraft 1.21.1neoforge
Write-Host "Starting Minecraft 1.21.1neoforge..."

# Create input file with 'Y'
$inputFile = "$env:TEMP\cmcl_input.txt"
"Y" | Out-File -FilePath $inputFile -Force

# Start CMCL with input redirection
Start-Process -FilePath "java" -ArgumentList "-jar", "cmcl.jar", "1.21.1neoforge" -RedirectStandardInput $inputFile -NoNewWindow -Wait

Write-Host "Game exited"
