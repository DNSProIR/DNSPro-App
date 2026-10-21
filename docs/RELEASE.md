# Releasing DNSPro Apps

## Create a release

1. Ensure `main` is green on CI
2. Tag and push:

```bash
git tag v0.1.0
git push origin v0.1.0
```

3. The **Release** workflow builds platform artifacts and attaches them to a GitHub Release.

## Expected assets

| Asset | Platform |
|-------|----------|
| Windows publish folder / EXE | Windows |
| `DNSPro-Linux-x86_64` binary, `.deb`, `.rpm` | Linux |
| `DNSPro-Android.apk` | Android |

## First-time GitHub setup

```bash
gh auth login
gh repo create DNSProIR/DNSPro-App --public --source=. --remote=origin --push
```

## Optional secrets

| Secret | Purpose |
|--------|---------|
| Android keystore secrets | Signed release APK |
| Windows code signing cert | Signed EXE/MSIX |
