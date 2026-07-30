#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="wallet-auth-gateway"
RAW_BASE="https://raw.githubusercontent.com/Carlo451/wallet-auth-gateway/main"

echo "Erstelle Verzeichnisstruktur..."
mkdir -p "$BASE_DIR"
mkdir -p "$BASE_DIR/ssl"
mkdir -p "$BASE_DIR/certs"
cd "$BASE_DIR"
if [ ! -f certs/private.pem ]; then
  openssl genpkey -algorithm RSA -out certs/private.pem -pkeyopt rsa_keygen_bits:2048
fi

if [ ! -f certs/public.pem ]; then
  openssl rsa -in certs/private.pem -pubout -out certs/public.pem
fi



echo "Lade docker-compose.yml herunter..."
curl -fsSL "$RAW_BASE/docker-compose.yml" -o docker-compose.yml


