# The Ick List - DynamoDB Table Creation Script
param(
    [string]$EndpointUrl = $env:DYNAMODB_ENDPOINT
)

$ErrorActionPreference = "Stop"
$region = if ($env:AWS_DEFAULT_REGION) { $env:AWS_DEFAULT_REGION } else { "us-east-1" }

$awsCmd = if (Get-Command aws -ErrorAction SilentlyContinue) { "aws" } else { "C:\Program Files\Amazon\AWSCLIV2\aws.exe" }
$endpointArgs = if ($EndpointUrl) { @("--endpoint-url", $EndpointUrl) } else { @() }
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Creating DynamoDB tables in region: $region $(if ($EndpointUrl) { "at $EndpointUrl" })" -ForegroundColor Cyan

# 1. Users table
Write-Host "Checking 'Users' table..." -ForegroundColor Yellow
$usersExists = $false
try {
    & $awsCmd dynamodb describe-table --table-name Users --region $region @endpointArgs 2>$null | Out-Null
    $usersExists = $true
} catch {}

if ($usersExists) {
    Write-Host "Table 'Users' already exists." -ForegroundColor Green
} else {
    Write-Host "Creating 'Users' table..." -ForegroundColor Yellow
    $gsiFilePath = "file://$scriptDir/users-gsi.json"
    & $awsCmd dynamodb create-table `
        --table-name Users `
        --attribute-definitions `
            AttributeName=userId,AttributeType=S `
            AttributeName=email,AttributeType=S `
        --key-schema `
            AttributeName=userId,KeyType=HASH `
        --global-secondary-indexes $gsiFilePath `
        --billing-mode PAY_PER_REQUEST `
        --region $region @endpointArgs
    Write-Host "Table 'Users' created successfully." -ForegroundColor Green
}

# 2. Transactions table
Write-Host "Checking 'Transactions' table..." -ForegroundColor Yellow
$txExists = $false
try {
    & $awsCmd dynamodb describe-table --table-name Transactions --region $region @endpointArgs 2>$null | Out-Null
    $txExists = $true
} catch {}

if ($txExists) {
    Write-Host "Table 'Transactions' already exists." -ForegroundColor Green
} else {
    Write-Host "Creating 'Transactions' table..." -ForegroundColor Yellow
    & $awsCmd dynamodb create-table `
        --table-name Transactions `
        --attribute-definitions `
            AttributeName=userId,AttributeType=S `
            AttributeName=transactionId,AttributeType=S `
        --key-schema `
            AttributeName=userId,KeyType=HASH `
            AttributeName=transactionId,KeyType=RANGE `
        --billing-mode PAY_PER_REQUEST `
        --region $region @endpointArgs
    Write-Host "Table 'Transactions' created successfully." -ForegroundColor Green
}

# 3. Roasts table
Write-Host "Checking 'Roasts' table..." -ForegroundColor Yellow
$roastsExists = $false
try {
    & $awsCmd dynamodb describe-table --table-name Roasts --region $region @endpointArgs 2>$null | Out-Null
    $roastsExists = $true
} catch {}

if ($roastsExists) {
    Write-Host "Table 'Roasts' already exists." -ForegroundColor Green
} else {
    Write-Host "Creating 'Roasts' table..." -ForegroundColor Yellow
    & $awsCmd dynamodb create-table `
        --table-name Roasts `
        --attribute-definitions `
            AttributeName=userId,AttributeType=S `
            AttributeName=roastId,AttributeType=S `
        --key-schema `
            AttributeName=userId,KeyType=HASH `
            AttributeName=roastId,KeyType=RANGE `
        --billing-mode PAY_PER_REQUEST `
        --region $region @endpointArgs
    Write-Host "Table 'Roasts' created successfully." -ForegroundColor Green
}

Write-Host "All DynamoDB tables verified/created!" -ForegroundColor Green
