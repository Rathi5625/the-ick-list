$ErrorActionPreference = "Stop"

Write-Host "=== 1. Test Empty History for Fresh User ===" -ForegroundColor Cyan
$freshEmail = "fresh-$(Get-Random)@theicklist.app"
$signupBody = @{
    name = "Fresh User"
    email = $freshEmail
    password = "password123"
} | ConvertTo-Json

$null = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signup" -Method POST -ContentType "application/json" -Body $signupBody

$loginBody = @{
    email = $freshEmail
    password = "password123"
} | ConvertTo-Json

$loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $loginRes.token

$initialHistory = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/history" `
    -Method GET `
    -Headers @{ "Authorization" = "Bearer $token" }

if ($initialHistory.roasts.Count -eq 0) {
    Write-Host "PASS: Empty state confirmed! User has 0 roasts." -ForegroundColor Green
} else {
    Write-Error "FAIL: Expected 0 roasts, got $($initialHistory.roasts.Count)"
}

Write-Host "`n=== 2. Upload & Generate Batch #1 ===" -ForegroundColor Cyan
$sampleCsvPath = if (Test-Path "frontend\public\sample-transactions.csv") { "frontend\public\sample-transactions.csv" } else { "sample-transactions.csv" }
$curlOut1 = & curl.exe -s -X POST "http://localhost:8080/api/transactions/upload" `
    -H "Authorization: Bearer $token" `
    -F "file=@$sampleCsvPath"
$uploadRes1 = $curlOut1 | ConvertFrom-Json
$batch1Id = $uploadRes1.uploadBatchId
Write-Host "Uploaded Batch #1: $batch1Id" -ForegroundColor Gray

$genBody1 = @{ uploadBatchId = $batch1Id } | ConvertTo-Json
$genRes1 = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/generate" `
    -Method POST `
    -Headers @{ "Authorization" = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $genBody1

Write-Host "PASS: Batch #1 generated $($genRes1.roasts.Count) roasts." -ForegroundColor Green

Start-Sleep -Seconds 1

Write-Host "`n=== 3. Upload & Generate Batch #2 ===" -ForegroundColor Cyan
$curlOut2 = & curl.exe -s -X POST "http://localhost:8080/api/transactions/upload" `
    -H "Authorization: Bearer $token" `
    -F "file=@$sampleCsvPath"
$uploadRes2 = $curlOut2 | ConvertFrom-Json
$batch2Id = $uploadRes2.uploadBatchId
Write-Host "Uploaded Batch #2: $batch2Id" -ForegroundColor Gray

$genBody2 = @{ uploadBatchId = $batch2Id } | ConvertTo-Json
$genRes2 = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/generate" `
    -Method POST `
    -Headers @{ "Authorization" = "Bearer $token" } `
    -ContentType "application/json" `
    -Body $genBody2

Write-Host "PASS: Batch #2 generated $($genRes2.roasts.Count) roasts." -ForegroundColor Green

Write-Host "`n=== 4. Verify History Returns Both Batches (No Overwrite) ===" -ForegroundColor Cyan
$fullHistory = Invoke-RestMethod -Uri "http://localhost:8080/api/roasts/history" `
    -Method GET `
    -Headers @{ "Authorization" = "Bearer $token" }

$totalCount = $fullHistory.roasts.Count
Write-Host "Total history items: $totalCount" -ForegroundColor Yellow

if ($totalCount -ne 8) {
    Write-Error "FAIL: Expected 8 total roasts across two batches, found $totalCount"
}

$batch1Items = $fullHistory.roasts | Where-Object { $_.uploadBatchId -eq $batch1Id }
$batch2Items = $fullHistory.roasts | Where-Object { $_.uploadBatchId -eq $batch2Id }

Write-Host "Batch #1 count: $($batch1Items.Count)" -ForegroundColor White
Write-Host "Batch #2 count: $($batch2Items.Count)" -ForegroundColor White

if ($batch1Items.Count -eq 4 -and $batch2Items.Count -eq 4) {
    Write-Host "PASS: Batch #2 did NOT overwrite Batch #1! Both batches are fully preserved." -ForegroundColor Green
} else {
    Write-Error "FAIL: Expected 4 items in each batch!"
}

Write-Host "`n=== 5. Verify Ordering: Newest Batch Appears First ===" -ForegroundColor Cyan
$firstItemBatch = $fullHistory.roasts[0].uploadBatchId
if ($firstItemBatch -eq $batch2Id) {
    Write-Host "PASS: First item belongs to Batch #2 (most recent)! Ordered newest-first." -ForegroundColor Green
} else {
    Write-Error "FAIL: First item does not belong to the most recent batch."
}

Write-Host "`n=== ALL TESTS PASSED SUCCESSFULLY! ===" -ForegroundColor Green
