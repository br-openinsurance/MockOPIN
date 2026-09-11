#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status

BRAND_STRIPPED="${BRAND%_LOCAL}"
BRAND_LOWER=$(echo "$BRAND_STRIPPED" | tr '[:upper:]' '[:lower:]')
echo "Environment has been set to $BRAND_LOWER"

# Dynamically set file paths based on BRAND_LOWER
INIT_DIR="/mock_as/mongo-seed/$BRAND_LOWER"
if [ ! -d "$INIT_DIR" ]; then
    echo "Error: Directory $INIT_DIR does not exist!"
    exit 1
fi

# Copy the appropriate files to their expected locations
cp "$INIT_DIR/init_clients.json" /init_clients.json
cp "$INIT_DIR/init_accounts.json" /init_accounts.json
cp "$INIT_DIR/init_credentials.json" /init_credentials.json

echo "Initialization files have been set for $BRAND_LOWER"
echo "Starting import"

sleep 15

mongosh --host "$1" --eval "db = db.getSiblingDB('openid-server'); db.client.drop()"
mongoimport --host $1 --db openid-server --collection client --type json --file $2/init_clients.json --jsonArray
mongoimport --host $1 --db accounts --collection accounts --type json --file $2/init_accounts.json --jsonArray
mongoimport --host $1 --db accounts --collection credentials --type json --file $2/init_credentials.json --jsonArray

echo "Stopping Import"
