# DNSPro for Linux

Go + Fyne client that works across major Linux distributions.

## Backends (auto-detected)

1. NetworkManager (`nmcli`)
2. systemd-resolved (`resolvectl`)
3. `/etc/resolv.conf` backup/restore

Privilege escalation uses `pkexec`.

## Build

```bash
go mod tidy
go build -o dnspro .
```

## Usage

```bash
./dnspro              # GUI
./dnspro enable
./dnspro disable
./dnspro status
./dnspro check
```

## Packaging

```bash
./packaging/build.sh
```

Produces `.deb` and optionally `.rpm`. CI builds an AppImage for universal desktop use.

## Requirements

- Linux desktop (X11 or Wayland)
- `pkexec` / polkit for non-root DNS changes
- One of: NetworkManager, systemd-resolved, or writable `/etc/resolv.conf`
