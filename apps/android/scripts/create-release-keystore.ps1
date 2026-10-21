# Creates a Cafe Bazaar / Play-compatible release keystore for DNSPro.
# Run from anywhere; writes files into apps/android/

$ErrorActionPreference = "Stop"
$AndroidRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $AndroidRoot

$keystore = Join-Path $AndroidRoot "dnspro-release.keystore"
$props = Join-Path $AndroidRoot "keystore.properties"

if (Test-Path $keystore) {
    Write-Host "Keystore already exists: $keystore"
    Write-Host "Delete it first if you want to create a new one."
    exit 1
}

$storePassword = Read-Host "Store password (min 6 chars)"
$keyPassword = Read-Host "Key password (min 6 chars, can match store password)"
$alias = "dnspro"

Write-Host "Generating keystore (validity 10000 days)..."
keytool -genkeypair `
  -v `
  -keystore $keystore `
  -alias $alias `
  -keyalg RSA `
  -keysize 2048 `
  -validity 10000 `
  -storepass $storePassword `
  -keypass $keyPassword `
  -dname "CN=DNSPro, OU=Mobile, O=DNSPro, L=Tehran, ST=Tehran, C=IR"

@"
STORE_FILE=dnspro-release.keystore
STORE_PASSWORD=$storePassword
KEY_ALIAS=$alias
KEY_PASSWORD=$keyPassword
"@ | Set-Content -Path $props -Encoding UTF8

Write-Host ""
Write-Host "Created:"
Write-Host "  $keystore"
Write-Host "  $props"
Write-Host ""
Write-Host "Build signed release APK:"
Write-Host "  cd apps\android"
Write-Host "  gradle assembleRelease"
Write-Host ""
Write-Host "APK path:"
Write-Host "  app\build\outputs\apk\release\app-release.apk"
Write-Host ""
Write-Host "IMPORTANT: Back up the keystore. If you lose it, you cannot update the Cafe Bazaar app."
