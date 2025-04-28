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

 awslocal s3 mb s3://keystore
 awslocal s3 website s3://keystore --index-document jwks.json
 awslocal s3 cp /init/ssa/jwks.json s3://keystore/jwks.json --content-type application/json
 awslocal s3api put-object-acl --bucket keystore --key jwks.json --acl public-read

 awslocal s3 cp /init/ssa/private_jwk.json s3://keystore/private_jwk.json --content-type application/json
 awslocal s3api put-object-acl --bucket keystore --key private_jwk.json --acl public-read

echo "ready" > $CONFIG_FILE_PATH
