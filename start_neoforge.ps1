$process = Start-Process -FilePath "java" -ArgumentList "-jar", "cmcl.jar", "1.21.1neoforge" -NoNewWindow -PassThru -RedirectStandardInput "input.txt"
Start-Sleep -Seconds 2
Add-Content -Path "input.txt" -Value "Y"
$process.WaitForExit()
Remove-Item -Path "input.txt" -Force -ErrorAction SilentlyContinue