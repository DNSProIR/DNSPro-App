#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
REPO_ROOT="$(cd "$ROOT/../.." && pwd)"
OUT="${OUT:-$ROOT/dist}"
VERSION="${VERSION:-0.1.0}"
ARCH="${ARCH:-amd64}"

mkdir -p "$OUT"
cd "$ROOT"

echo "==> Building dnspro binary"
CGO_ENABLED=1 go build -ldflags "-s -w -X main.Version=$VERSION" -o "$OUT/dnspro" .

STAGE="$OUT/stage"
rm -rf "$STAGE"
mkdir -p "$STAGE/usr/bin" "$STAGE/usr/share/applications" "$STAGE/usr/share/dnspro" "$STAGE/usr/share/polkit-1/actions"

cp "$OUT/dnspro" "$STAGE/usr/bin/dnspro"
cp "$ROOT/packaging/dnspro.desktop" "$STAGE/usr/share/applications/"
cp "$REPO_ROOT/shared/dns-config.json" "$STAGE/usr/share/dnspro/"
cp "$ROOT/packaging/ir.dnspro.policy" "$STAGE/usr/share/polkit-1/actions/" 2>/dev/null || true

echo "==> Building .deb"
DEB_ROOT="$OUT/deb"
rm -rf "$DEB_ROOT"
mkdir -p "$DEB_ROOT/DEBIAN"
cp -a "$STAGE"/* "$DEB_ROOT/"
cat > "$DEB_ROOT/DEBIAN/control" <<EOF
Package: dnspro
Version: $VERSION
Section: net
Priority: optional
Architecture: $ARCH
Maintainer: DNSPro <we@dnspro.ir>
Description: DNSPro DNS client for Linux
 Enable or disable DNSPro DNS servers with a simple GUI or CLI.
Homepage: https://dnspro.ir
EOF
dpkg-deb --build "$DEB_ROOT" "$OUT/dnspro_${ARCH}.deb"

echo "==> Building .rpm (requires rpmbuild or fpm if available)"
if command -v fpm >/dev/null 2>&1; then
  fpm -s dir -t rpm -n dnspro -v "$VERSION" -a x86_64 \
    --description "DNSPro DNS client for Linux" \
    --url "https://dnspro.ir" \
    --maintainer "DNSPro <we@dnspro.ir>" \
    -C "$STAGE" \
    usr
  mv dnspro-*.rpm "$OUT/" || true
elif command -v rpmbuild >/dev/null 2>&1; then
  echo "rpmbuild detected; prefer fpm for simple packaging in CI"
else
  echo "Skipping rpm (install fpm to produce rpm packages)"
fi

echo "==> AppImage placeholder note"
echo "Use CI job with appimagetool to wrap $OUT/dnspro into DNSPro-Linux-x86_64.AppImage"
echo "Artifacts in $OUT"
