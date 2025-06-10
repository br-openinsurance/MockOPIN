# Mock Insurance Open Source

This repository provides a fully local instance of **Mock Insurance**, including all components required to comply with [Open Insurance (OPIN)] standards and API specifications.

Certificates for the Certificate Authority and mTLS clients are located in [`mock-service-os/certs`](./mock-service-os/certs).

---

## Prerequisites

To run Mock Insurance OS, ensure the following tools are installed:

- **Java 17**
- **Gradle**
- **Docker**

---

Below are the steps to run and interact with Mock OPIN on your local machine.
If you're using Windows, make sure to run these commands in a Linux-compatible terminal (e.g., WSL or Git Bash).

### 1. Setup

Publish the local dependency that contains the API models used by Mock Insurance.

> This step is required only once.

```bash
make setup
```

### 2. Configure Local Hosts

Add the following entries to your hosts file to create aliases for localhost:

```bash
127.0.0.1 auth.local
127.0.0.1 api.local
127.0.0.1 directory
```

### 3. Run the Environment

To run a fully local instance, you can start all the components using Docker with the following command:

```bash
make run
```

### 4. Run with Conformance Suite

To run a fully local instance with the conformance suite, you need to start all components, along with the conformance suite itself.

Run the command below to set up the conformance suite:

```bash
make setup-cs
```

Then, you can start the environment with:

```bash
make run-with-cs
```

Configuration for the tests is available in the `cs_config.json` file.

## Obtaining a Token Using Certificates

You can request a token using the client certificate:

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
```

To introspect the token:

```bash
curl -v \
  --cert ./mock-service-os/certs/client_one.crt \
  --key ./mock-service-os/certs/client_one.key \
  --cert-type PEM \
  --key-type PEM \
  -u client:1234 \
  -d "token=your_access_token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://auth.local/token/introspection -k
```
