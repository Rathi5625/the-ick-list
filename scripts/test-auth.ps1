$ErrorActionPreference = "Continue"

Write-Host "=== TEST: Signup with Name & Avatar ===" -ForegroundColor Cyan
$signupBody = @{
    name = "Parth Rathi"
    email = "parth.rathi@theicklist.app"
    password = "securePassword123"
} | ConvertTo-Json

try {
    $res = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signup" -Method POST -ContentType "application/json" -Body $signupBody
    Write-Host "PASS: User created with Name and Avatar!" -ForegroundColor Green
    Write-Host "  Name:           $($res.name)"
    Write-Host "  AvatarInitials: $($res.avatarInitials)"
    Write-Host "  AvatarColor:    $($res.avatarColor)"
    Write-Host "  UserId:         $($res.userId)"
} catch {
    Write-Host "FAIL: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== TEST: Login returning Name & Avatar ===" -ForegroundColor Cyan
$loginBody = @{
    email = "parth.rathi@theicklist.app"
    password = "securePassword123"
} | ConvertTo-Json

try {
    $res = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "PASS: Login success with Name and Avatar!" -ForegroundColor Green
    Write-Host "  Name:           $($res.name)"
    Write-Host "  AvatarInitials: $($res.avatarInitials)"
    Write-Host "  AvatarColor:    $($res.avatarColor)"
    Write-Host "  Token Length:   $($res.token.Length)"
} catch {
    Write-Host "FAIL: $($_.Exception.Message)" -ForegroundColor Red
}
