$ErrorActionPreference = "Stop"
$testEmail = "parth-$(Get-Random)@theicklist.app"
$signupBody = @{
    name = "Parth Rathi"
    email = $testEmail
    password = "password123"
} | ConvertTo-Json

$res = Invoke-RestMethod -Uri "http://localhost:5173/api/auth/signup" -Method POST -ContentType "application/json" -Body $signupBody
Write-Host "Vite Proxy Signup Success!" -ForegroundColor Green
Write-Host "  Name:           $($res.name)"
Write-Host "  Initials:       $($res.avatarInitials)"
Write-Host "  Color:          $($res.avatarColor)"
Write-Host "  UserId:         $($res.userId)"

$loginBody = @{
    email = $testEmail
    password = "password123"
} | ConvertTo-Json

$loginRes = Invoke-RestMethod -Uri "http://localhost:5173/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
Write-Host "Vite Proxy Login Success!" -ForegroundColor Green
Write-Host "  Name:           $($loginRes.name)"
Write-Host "  Initials:       $($loginRes.avatarInitials)"
Write-Host "  Color:          $($loginRes.avatarColor)"
Write-Host "  Token Length:   $($loginRes.token.Length)"
