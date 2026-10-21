# Publish DNSPro on Cafe Bazaar

Cafe Bazaar rejects APKs signed with the **debug** key. You must upload a **release-signed** APK.

## 1. Create a release keystore (once)

```powershell
cd apps\android
.\scripts\create-release-keystore.ps1
```

This creates:

- `dnspro-release.keystore` (keep private, back up safely)
- `keystore.properties` (local only, gitignored)

## 2. Build the signed release APK

```powershell
cd apps\android
gradle assembleRelease
```

Output:

```text
app\build\outputs\apk\release\app-release.apk
```

Rename if you like (e.g. `DNSPro-Android.apk`) and upload that file to Cafe Bazaar.

## 3. Optional: CI signing (GitHub Secrets)

Add these repository secrets:

| Secret | Value |
|--------|--------|
| `ANDROID_KEYSTORE_BASE64` | Base64 of `dnspro-release.keystore` |
| `ANDROID_STORE_PASSWORD` | store password |
| `ANDROID_KEY_ALIAS` | `dnspro` |
| `ANDROID_KEY_PASSWORD` | key password |

Encode keystore on Windows PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("apps\android\dnspro-release.keystore")) | Set-Clipboard
```

## Notes

- Never commit the keystore or `keystore.properties`
- Use the **same** keystore for every Cafe Bazaar update
- Package name is `ir.dnspro.app`
