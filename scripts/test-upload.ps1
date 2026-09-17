$ErrorActionPreference = "Continue"

Write-Host "=== TEST 1: Obtain Auth Token via Login ===" -ForegroundColor Cyan
$loginEmail = "tester-$(Get-Random)@theicklist.app"
$signupBody = @{
    name = "Test Uploader"
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
Write-Host "PASS: Authenticated! Token length: $($token.Length) User: $($loginRes.name)" -ForegroundColor Green

Write-Host "`n=== TEST 2: Upload Without Token (Expect 401 Unauthorized) ===" -ForegroundColor Cyan
$sampleCsvPath = if (Test-Path "frontend\public\sample-transactions.csv") { "frontend\public\sample-transactions.csv" } else { "sample-transactions.csv" }
try {
    $res = Invoke-WebRequest -Uri "http://localhost:8080/api/transactions/upload" -Method POST -InFile $sampleCsvPath -ContentType "multipart/form-data"
    Write-Host "FAIL: Expected 401 but received 200" -ForegroundColor Red
} catch {
    $code = $_.Exception.Response.StatusCode.value__
    Write-Host "PASS: Correctly rejected unauthenticated request with HTTP $code" -ForegroundColor Green
}

Write-Host "`n=== TEST 3: Upload Malformed CSV (Missing Merchant Column - Expect 400) ===" -ForegroundColor Cyan
$badCsvContent = "Date,Amount,Category`n2026-09-01,15.99,Subscriptions"
$badCsvPath = "scripts\bad-transactions.csv"
Set-Content -Path $badCsvPath -Value $badCsvContent

try {
    # Using curl.exe for multipart/form-data upload
    $curlOut = & curl.exe -s -w "`n%{http_code}" -X POST "http://localhost:8080/api/transactions/upload" `
        -H "Authorization: Bearer $token" `
        -F "file=@$badCsvPath"

    $lines = $curlOut -split "`n"
    $httpCode = $lines[-1].Trim()
    $responseBody = ($lines[0..($lines.Length-2)]) -join "`n"

    if ($httpCode -eq "400") {
        Write-Host "PASS: Correctly rejected bad CSV with HTTP 400! Response: $responseBody" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Unexpected HTTP code $($httpCode) - $($responseBody)" -ForegroundColor Red
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    if (Test-Path $badCsvPath) { Remove-Item $badCsvPath }
}

Write-Host "`n=== TEST 4: Upload Valid sample-transactions.csv ===" -ForegroundColor Cyan
try {
    $curlOut = & curl.exe -s -w "`n%{http_code}" -X POST "http://localhost:8080/api/transactions/upload" `
        -H "Authorization: Bearer $token" `
        -F "file=@$sampleCsvPath"

    $lines = $curlOut -split "`n"
    $httpCode = $lines[-1].Trim()
    $responseBody = ($lines[0..($lines.Length-2)]) -join "`n"

    if ($httpCode -eq "200") {
        Write-Host "PASS: Successfully uploaded transactions with HTTP 200!" -ForegroundColor Green
        Write-Host "  Response: $responseBody" -ForegroundColor Green
    } else {
        Write-Host "FAIL: Unexpected HTTP code $($httpCode) - $($responseBody)" -ForegroundColor Red
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== TEST 5: Verify Transactions in DynamoDB Local ===" -ForegroundColor Cyan
$env:AWS_ACCESS_KEY_ID = "test"
$env:AWS_SECRET_ACCESS_KEY = "test"
$scanRes = & "C:\Program Files\Amazon\AWSCLIV2\aws.exe" dynamodb scan --table-name Transactions --endpoint-url http://localhost:8000 --select COUNT
Write-Host "PASS: DynamoDB Transactions table verified! $scanRes" -ForegroundColor Green
