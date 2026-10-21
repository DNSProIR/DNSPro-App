# Publishes this repository to GitHub under DNSProIR/DNSPro-App
# Prerequisites: gh auth login (org access to DNSProIR)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

gh auth status
$repo = "DNSProIR/DNSPro-App"

$exists = $false
try {
  gh repo view $repo | Out-Null
  $exists = $true
} catch {
  $exists = $false
}

$description = "Open-source DNSPro clients for Windows, Linux, and Android — one-tap DNS for sanction access and Safe Family protection."
$topics = @(
  "dns","dnspro","dns-client","windows","linux","android",
  "safe-family","open-source","golang","kotlin","csharp","wpf","fyne"
)

if (-not $exists) {
  Write-Host "Creating $repo ..."
  gh repo create $repo --public --source=. --remote=origin --description $description --homepage "https://dnspro.ir"
} else {
  Write-Host "Repo exists. Ensuring origin remote..."
  $remotes = git remote
  if ($remotes -notcontains "origin") {
    git remote add origin "https://github.com/$repo.git"
  }
}

git push -u origin main

Write-Host "Setting description, homepage, and topics..."
gh repo edit $repo --description $description --homepage "https://dnspro.ir"
foreach ($t in $topics) {
  gh repo edit $repo --add-topic $t
}

Write-Host "Done. Create a release with: git tag v0.1.0; git push origin v0.1.0"
