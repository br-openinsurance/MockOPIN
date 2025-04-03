#!/usr/bin/env bash

CONFIG_FILE_PATH="/init/config/config.txt"
# Create or replace the config file.
: > "$CONFIG_FILE_PATH"

printf "Configuring localstack components..."

set -x
REGION=us-east-1

awslocal ssm put-parameter \
    --name "/local/op_fapi_client_config/issuer" \
    --value "auth.local" \
    --type "SecureString" \
    --overwrite \
    --region "${REGION}"
awslocal ssm put-parameter \
    --name "/local/op_fapi_client_config/transport_certificate" \
    --value "$(cat /init/certs/client.crt)" \
    --type "SecureString" \
    --overwrite \
    --region "${REGION}"
awslocal ssm put-parameter \
    --name "/local/op_fapi_client_config/transport_key" \
    --value "$(cat /init/certs/client.key)" \
    --type "SecureString" \
    --overwrite \
    --region "${REGION}"

echo '{
  "keys": [
    {
      "kty": "RSA",
      "e": "AQAB",
      "use": "sig",
      "kid": "kekKrDf-a0AN7jwzvCwpdR3iT0elbaKUr16EJTZEUGk",
      "alg": "PS256",
      "n": "r37dAsoj8UaIJ9kjjRdiKjXe0jCNQbsQddd7OI0wGDq7RGhuFvCaK0U3bOxm6vSbWzba7xmMJzpYy758j0UgsdLD1S18S6cGOWHF1DqNTofHS1aZrz8M7yPA2DVCznmlYa_q0b4w2azhXx72i_9-XhMx__8bkqtgCi1RUfJcbgMK6gByJCpzNuDS7r8CLOLWdcD6Mh48Uag9ll-oKoVTRQ8I41swkCAt-IqU28C0paMjy1skZQgzmAxtoZgEURn_0IQR2ip1ZSfAuHncXvlYkUvKeZKGqcUpE4tFe_QtsWOInt0Ffh_0mtizrghqaAtqtYy9hCEAN_rJ3dyI6LaAXw"
    }
  ]
}' > jwks.json

 awslocal s3 mb s3://keystore
 awslocal s3 website s3://keystore --index-document jwks.json
 awslocal s3 cp jwks.json s3://keystore/jwks.json --content-type application/json
 awslocal s3api put-object-acl --bucket keystore --key jwks.json --acl public-read

echo "ready" > $CONFIG_FILE_PATH

