# Contributing to DNSPro Apps

Thank you for helping improve DNSPro client apps. All project communication and code comments must be in **English**.

## Ways to contribute

- Report bugs and request features via GitHub Issues
- Submit pull requests for bug fixes or improvements
- Improve documentation and packaging
- Suggest domains for DNS resolution via [DNSProIR/DNSPro](https://github.com/DNSProIR/DNSPro)

## Development setup

This repository is a monorepo:

| Path            | Stack                      |
|-----------------|----------------------------|
| `apps/windows`  | C# / WPF (.NET 8)          |
| `apps/linux`    | Go + Fyne                  |
| `apps/android`  | Kotlin + Jetpack Compose   |
| `shared/`       | DNS config and English UI strings |

Keep DNS server addresses in `shared/dns-config.json`. Do not hard-code IPs in UI code.

## Pull requests

1. Fork the repository and create a feature branch
2. Keep changes focused and documented
3. Match existing code style for that platform
4. Fill out the PR template
5. Ensure CI passes when workflows apply to your change

## Commit messages

Prefer short, imperative messages (for example: `Add Linux resolvectl backend`).

## Code of conduct

By participating, you agree to follow our [Code of Conduct](CODE_OF_CONDUCT.md).
