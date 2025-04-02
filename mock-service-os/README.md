# Mock Service Open Source

This solution provides a complete local instance of Mock mTLS gateway and OpenID Provider, including all components needed to adhere to FAPI standards.

## Prerequisites

To run Mock OS, ensure you have the following tools installed:

- **Docker**
- **Node.js**

### 1. Generate PKI Certificates
To generate the required certificates, run the command below. This step is required only once and will generate a new CA along with the necessary server certificates for the MTLS agent.

```bash
make certs
```

### 2. Create local host entries
You need to add api.local and auth.local as aliases to localhost at the hosts file.

```bash
127.0.0.1 api.local
127.0.0.1 auth.local
```

### 3. Start eveything up
To have a fully local instance running you can only spin up all the components using singles containers with the command:
```bash
make run
```

### 4. Start eveything up with lambda context using localstack
To have a fully local instance running with the aws lambda context you need to spin up all the components using singles containers and the api with localstack with the command:

`ps: This will require a powerfull machine to run properly`
```bash
make run-local-localstack
```

# Get a token using the certs

```bash
curl -v \
  --cert ./certs/client_one.crt \
  --key ./certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -u client:1234 \
  -d "grant_type=client_credentials&scope=openid" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://auth.local/token -k

  curl -v \
  --cert ./certs/client_one.crt \
  --key ./certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -u client:1234 \
  -d "token=UXc7zN2knByBYD2conPeRgysCRlfL-FbsKmyUoJylnU" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://auth.local/token/introspection -k


  curl -v \
  --cert ./certs/client_one.crt \
  --key ./certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -H "Authorization: Bearer UXc7zN2knByBYD2conPeRgysCRlfL-FbsKmyUoJylnU" \
  https://api.local/ -k
  ```
