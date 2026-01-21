#!/bin/bash
echo "Starting import"
sleep 15

mongosh --host "$1" --eval "db = db.getSiblingDB('openid-server'); db.client.drop()"
mongoimport --host $1 --db openid-server --collection client --type json --file $2/init_clients.json --jsonArray
mongoimport --host $1 --db accounts --collection accounts --type json --file $2/init_accounts.json --jsonArray
mongoimport --host $1 --db accounts --collection credentials --type json --file $2/init_credentials.json --jsonArray

echo "Stopping Import"
