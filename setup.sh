#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="wallet-auth-gateway"
RAW_BASE="https://raw.githubusercontent.com/Carlo451/wallet-auth-gateway/main"

echo "Erstelle Verzeichnisstruktur..."
mkdir -p "$BASE_DIR"
mkdir -p "$BASE_DIR/ssl"
mkdir -p "$BASE_DIR/certs"
cd "$BASE_DIR"


echo "Lade docker-compose.yml herunter..."
curl -fsSL "$RAW_BASE/docker-compose.yml" -o docker-compose.yml


