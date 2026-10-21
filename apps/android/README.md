# DNSPro for Android

Kotlin + Jetpack Compose client. Uses a **local VPN** solely to route DNS queries to DNSPro.

## Build (debug)

```bash
gradle assembleDebug
```

## Build (release — required for Cafe Bazaar)

Cafe Bazaar rejects debug-signed APKs. Create a release keystore once, then:

```powershell
.\scripts\create-release-keystore.ps1
gradle assembleRelease
```

Signed APK: `app/build/outputs/apk/release/app-release.apk`

See [docs/CAFEBAZAAR.md](docs/CAFEBAZAAR.md).

## Behavior

- Enable asks for VPN permission, then starts `DnsVpnService`
- DNS queries are forwarded to `80.244.11.244` (from `assets/dns-config.json`)
- Disable stops the VPN service
