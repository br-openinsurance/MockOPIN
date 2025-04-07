# Mock Insurance Open Source

This solution provides a complete local instance of Mock Insurance, including all components needed to adhere to OPIN standards and API specification implementations.

## Prerequisites

To run Mock Insurance OS, ensure you have the following tools installed:

- **Java 17**
- **Gradle**
- **Docker**

### 1. Set Up
Run the command below to generate a Certificate Authority along with the necessary server certificates for the MTLS agent. This will also publish a local dependency containing the API models used by Mock Insurance.

This step is required only once.

```bash
make setup
```

### 2. Create local host entries
You need to add `api.local` and `auth.local` as aliases for localhost in the hosts file.

```bash
127.0.0.1 auth.local
127.0.0.1 api.local
```

### 3. Start eveything up
To run a fully local instance, you can start all the components using Docker with the following command:
```bash
make run
```

### 4. Start eveything up with the Conformance Suite
To run a fully local instance with the conformance suite, you need to start all components, along with the conformance suite itself.

Run the command below to set up the conformance suite:
```bash
make setup-cs
```

A configuration file for the conformance suite will be generated at `insurance-server-lambdas/cs_config.json`.

Then, you can start the environment with:

Note: This requires a powerful machine to run properly.
```bash
make run-with-cs
```

# Get a token using the certificates

```bash
curl -v \
  --cert ./mock-service-os/certs/client_one.crt \
  --key ./mock-service-os/certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -u client:1234 \
  -d "grant_type=client_credentials&scope=openid" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://auth.local/token -k

curl -v \
  --cert ./mock-service-os/certs/client_one.crt \
  --key ./mock-service-os/certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -u client:1234 \
  -d "token=UXc7zN2knByBYD2conPeRgysCRlfL-FbsKmyUoJylnU" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://auth.local/token/introspection -k


curl -v \
  --cert ./mock-service-os/certs/client_one.crt \
  --key ./mock-service-os/certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -H "Authorization: Bearer UXc7zN2knByBYD2conPeRgysCRlfL-FbsKmyUoJylnU" \
  https://api.local/ -k
  ```
