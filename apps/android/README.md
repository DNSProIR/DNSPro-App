# DNSPro for Android

Kotlin + Jetpack Compose client. Uses a **local VPN** solely to route DNS queries to DNSPro.

## Build

```bash
./gradlew :app:assembleRelease
```

APK output: `app/build/outputs/apk/release/`

## Behavior

- Enable asks for VPN permission, then starts `DnsVpnService`
- DNS queries are forwarded to `80.244.11.244` (from `assets/dns-config.json`)
- Disable stops the VPN service

## Notes

Google Play listing is out of scope for v1; distribute the APK via GitHub Releases.
