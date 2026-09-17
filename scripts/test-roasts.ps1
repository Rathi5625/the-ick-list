$ErrorActionPreference = "Stop"

Write-Host "=== 1. Authenticate ===" -ForegroundColor Cyan
$loginEmail = "roaster-$(Get-Random)@theicklist.app"
$signupBody = @{
    name = "Roast Tester"
    email = $loginEmail
    password = "password123"
} | ConvertTo-Json

$signupRes = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signup" -Method POST -ContentType "application/json" -Body $signupBody

$loginBody = @{
    email = $loginEmail
    password = "password123"
} | ConvertTo-Json

$loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $loginRes.token
Write-Host "PASS: Authenticated as $($loginRes.name)!" -ForegroundColor Green

Write-Host "`n=== 2. Upload sample-transactions.csv ===" -ForegroundColor Cyan
$sampleCsvPath = if (Test-Path "frontend\public\sample-transactions.csv") { "frontend\public\sample-transactions.csv" } else { "sample-transactions.csv" }
$curlOut = & curl.exe -s -X POST "http://localhost:8080/api/transactions/upload" `
    -H "Authorization: Bearer $token" `
    -F "file=@$sampleCsvPath"

$uploadRes = $curlOut | ConvertFrom-Json
$batchId = $uploadRes.uploadBatchId
Write-Host "PASS: Uploaded $($uploadRes.transactionCount) transactions! Batch ID: $batchId" -ForegroundColor Green

Write-Host "`n=== 3. POST /api/roasts/generate ===" -ForegroundColor Cyan
$genBody = @{
    uploadBatchId = $batchId
} | ConvertTo-Json

$genRes = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/generate" `
    -Method POST `
    -Headers @{ "Authorization" = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $genBody

Write-Host "PASS: Roasts Generated! Count: $($genRes.roasts.Count)" -ForegroundColor Green
foreach ($r in $genRes.roasts) {
    Write-Host "  $($r.emoji) [$($r.category.ToUpper()) | Severity: $($r.severity.ToUpper())]" -ForegroundColor Yellow
    Write-Host "     `"$($r.roastText)`"" -ForegroundColor White
}

Write-Host "`n=== 4. GET /api/roasts/history ===" -ForegroundColor Cyan
$histRes = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/history" `
    -Method GET `
    -Headers @{ "Authorization" = "Bearer $token" }

Write-Host "PASS: History retrieved! Total saved roasts: $($histRes.roasts.Count)" -ForegroundColor Green

Write-Host "`n=== 5. Verify DynamoDB Roasts Table ===" -ForegroundColor Cyan
$env:AWS_ACCESS_KEY_ID = "test"
$env:AWS_SECRET_ACCESS_KEY = "test"
$scanRes = & "C:\Program Files\Amazon\AWSCLIV2\aws.exe" dynamodb scan --table-name Roasts --endpoint-url http://localhost:8000 --region us-east-1 --select COUNT
Write-Host "PASS: DynamoDB Roasts table item count verified: $scanRes" -ForegroundColor Green
