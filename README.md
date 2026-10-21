# DNSPro Client Apps

Official open-source clients for [DNSPro](https://dnspro.ir) — a fast DNS service for sanction-restricted website access and Safe Family protection.

**Language:** English only (UI, docs, and contributions).

> DNSPro helps you reach sanction-restricted websites and provides Safe Family filtering. It is **not** intended for bypassing government filtering.

## Platforms

| Platform | Path | Notes |
|----------|------|--------|
| Windows | [`apps/windows`](apps/windows) | WPF (.NET 8) — sets system DNS (admin) |
| Linux | [`apps/linux`](apps/linux) | Go + Fyne — AppImage, `.deb`, `.rpm` |
| Android | [`apps/android`](apps/android) | Kotlin — DNS-only local VPN |

Default DNS (IPv4): **`80.244.11.244`** (configured in [`shared/dns-config.json`](shared/dns-config.json)).

## Features

- One-tap Enable / Disable DNSPro DNS
- Connection check
- Status display
- Links to website, Telegram, and email

## Downloads

See [GitHub Releases](https://github.com/DNSProIR/DNSPro-App/releases) for:

- Windows: `DNSPro-Windows-x64.zip`
- Linux AppImage, `.deb`, `.rpm` / binary
- Android: `DNSPro-Android.apk`

### Linux quick start

```bash
# AppImage (recommended — works on most desktop distributions)
chmod +x DNSPro-Linux-x86_64.AppImage
./DNSPro-Linux-x86_64.AppImage

# Debian / Ubuntu
sudo dpkg -i dnspro_amd64.deb

# Fedora / RHEL / openSUSE
sudo rpm -i dnspro.x86_64.rpm

# CLI
dnspro enable
dnspro disable
dnspro status
```

## Contact

- Website: [dnspro.ir](https://dnspro.ir)
- Telegram: [@dnspro_ir](https://t.me/dnspro_ir)
- Email: [we@dnspro.ir](mailto:we@dnspro.ir)

## Related

- Domain submissions: [DNSProIR/DNSPro](https://github.com/DNSProIR/DNSPro)

## Publishing to GitHub

Local git history is ready on `main`. To create **https://github.com/DNSProIR/DNSPro-App** and push:

```bash
gh auth login
# Windows:
.\scripts\publish-github.ps1
# Linux/macOS:
./scripts/publish-github.sh
```

Then create the first release:

```bash
git tag v0.1.0
git push origin v0.1.0
```

Tagged releases trigger the Release workflow. See [docs/RELEASE.md](docs/RELEASE.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Pull requests and issues are welcome.

## License

[MIT](LICENSE)
