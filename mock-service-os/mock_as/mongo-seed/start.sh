#!/bin/bash
echo "Starting import"
sleep 15

echo "Update the JWKS of client one and two"
jq --argjson jwks1 "$(jq . < $2/certs/client_one_pub.jwks)" \
   --argjson jwks2 "$(jq . < $2/certs/client_two_pub.jwks)" \
   '.[0].payload.jwks = $jwks1 | .[1].payload.jwks = $jwks2' \
   $2/init_clients.json > $2/init_clients.json.tmp && mv $2/init_clients.json.tmp $2/init_clients.json

mongosh --host "$1" --eval "db = db.getSiblingDB('openid-server'); db.client.drop()"
mongoimport --host $1 --db openid-server --collection client --type json --file $2/init_clients.json --jsonArray
mongoimport --host $1 --db accounts --collection accounts --type json --file $2/init_accounts.json --jsonArray
mongoimport --host $1 --db accounts --collection credentials --type json --file $2/init_credentials.json --jsonArray

echo "Stopping Import"
