#!/usr/bin/env bash

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
awslocal ssm put-parameter \
  --name "/mock/ready" \
  --type "SecureString" \
  --value "true" \
  --overwrite
