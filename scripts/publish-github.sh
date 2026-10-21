#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

gh auth status
REPO="DNSProIR/DNSPro-App"

DESCRIPTION='Open-source DNSPro clients for Windows, Linux, and Android — one-tap DNS for sanction access and Safe Family protection.'
TOPICS=(dns dnspro dns-client windows linux android safe-family open-source golang kotlin csharp wpf fyne)

if gh repo view "$REPO" >/dev/null 2>&1; then
  echo "Repo exists. Ensuring origin remote..."
  git remote get-url origin >/dev/null 2>&1 || git remote add origin "https://github.com/${REPO}.git"
else
  echo "Creating ${REPO} ..."
  gh repo create "$REPO" --public --source=. --remote=origin \
    --description "$DESCRIPTION" --homepage "https://dnspro.ir"
fi

git push -u origin main

echo "Setting description, homepage, and topics..."
gh repo edit "$REPO" --description "$DESCRIPTION" --homepage "https://dnspro.ir"
for t in "${TOPICS[@]}"; do
  gh repo edit "$REPO" --add-topic "$t"
done

echo "Done. Create a release with: git tag v0.1.0 && git push origin v0.1.0"
