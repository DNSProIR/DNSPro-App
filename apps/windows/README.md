# DNSPro for Windows

WPF (.NET 8) client that enables or disables DNSPro DNS on active network adapters.

## Requirements

- Windows 10 or later
- .NET 8 (self-contained publish does not require a separate runtime install)
- Administrator approval when changing DNS

## Build

```bash
dotnet restore -r win-x64
dotnet build -c Release -r win-x64
dotnet publish -c Release -r win-x64 --self-contained true -p:PublishSingleFile=true -o publish
```

## Behavior

- **Enable:** Sets IPv4 DNS on up hardware adapters to values from `shared/dns-config.json` (default `80.244.11.244`)
- **Disable:** Resets adapters to automatic (DHCP) DNS
- **Check connection:** Resolves the configured check host

Changing DNS requires a UAC elevation prompt.
