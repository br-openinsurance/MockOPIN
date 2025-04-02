#!/usr/bin/env bash

CONFIG_FILE_PATH="/init/config/config.txt"
# Create or replace the config file.
: > "$CONFIG_FILE_PATH"

printf "Configuring localstack components..."

set -x
API_NAME=bank-server-lambda
REGION=us-east-1
STAGE=v1

#upload the api lambda code to s3
awslocal s3 mb s3://my-bucket
awslocal s3 cp /init/my-lambda.jar s3://my-bucket/
awslocal s3 cp /init/my-op.zip s3://my-bucket/

#create role to be used by the lambda
awslocal iam create-role --role-name lambda-ex --assume-role-policy-document '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "logs:*",
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": "s3:*",
      "Resource": "*"
    }
  ]
}'

# awslocal lambda create-function \
#     --region ${REGION} \
#     --function-name ${API_NAME} \
#     --runtime java17 \
#     --handler com.raidiam.trustframework.bank.StreamLambdaHandler::handleRequest \
#     --memory-size 256 \
#     --code S3Bucket=my-bucket,S3Key=my-lambda.jar \
#     --role arn:aws:iam::000000000000:role/lambda-ex \
#     --snap-start ApplyOn=PublishedVersions \
#     --environment 'Variables={LAMBDA_RUNTIME_ENVIRONMENT_TIMEOUT=120,AWS_XRAY_CONTEXT_MISSING=IGNORE_ERROR,DB_DRIVER=org.postgresql.Driver,DB_PASSWORD=test,DB_URL=jdbc:postgresql://psql:5432/bank,DB_USERNAME=test,MOCKBANK_URL=https://mockbank.com}'


awslocal lambda create-function \
    --region ${REGION} \
    --function-name ${API_NAME} \
    --runtime nodejs18.x \
    --handler lambda.handler \
    --memory-size 256 \
    --code S3Bucket=my-bucket,S3Key=my-op.zip \
    --role arn:aws:iam::000000000000:role/lambda-ex \
    --snap-start ApplyOn=PublishedVersions \
    --environment 'Variables={MONGODB_URI=mongodb://mongo:30001/?replicaSet=my-replica-set, TRUSTFRAMEWORK_SSA_KEYSET="https://keystore.sandbox.directory.opinbrasil.com.br/openinsurance.jwks",AWS_SSM_REGION=us-east-1, LOG_LEVEL="debug", DEBUG="raidiam:server:*,oidc-provider:*", AWS_XRAY_CONTEXT_MISSING="IGNORE_ERROR", BRAND="OPIN"}'

LAMBDA_ARN=$(awslocal lambda list-functions --query "Functions[?FunctionName==\`${API_NAME}\`].FunctionArn" --output text --region ${REGION})

# sed -e "s|\${mockbank_write_arn}|arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/$LAMBDA_ARN:\$LATEST/invocations|g" \
#     -e "s|\${mockbank_read_arn}|arn:aws:apigateway:$REGION:lambda:path/2015-03-31/functions/$LAMBDA_ARN:\$LATEST/invocations|g" \
#     -e "s|\${authorizer_invoke_arn}|http://auth:3000/token/introspection|g" \
#     /init/swagger.yaml > /init/swagger-fixed.yaml

# echo "Placeholders replaced and saved to swagger-fixed.yaml"
# awslocal apigateway import-rest-api --region ${REGION} --body 'file:///init/swagger-fixed.yaml'

# #create role to be used by the lambda
# awslocal iam create-role --role-name apigateway-lambda-invoke-role --assume-role-policy-document '{
#   "Version": "2012-10-17",
#   "Statement": [
#     {
#         "Effect": "Allow",
#         "Action": "lambda:InvokeFunction",
#         "Resource": "*"
#     }
#   ]
# }'

# API_ID=$(awslocal apigateway get-rest-apis --query "items[0].id" --output text --region ${REGION})

awslocal apigateway create-deployment \
    --region ${REGION} \
    --rest-api-id ${API_ID} \
    --stage-name ${STAGE} \

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
 awslocal s3 cp jwks.json s3://keystore/jwks.json --content-type application/json
 awslocal s3api put-object-acl --bucket keystore --key jwks.json --acl public-read

# ENDPOINT=http://${API_ID}.execute-api.localhost.localstack.cloud:4566/${STAGE}/
ENDPOINT=http://localstack:4566/restapis/${API_ID}/${STAGE}/_user_request_/
echo $ENDPOINT > $CONFIG_FILE_PATH