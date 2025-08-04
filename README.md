# Mock Insurance Open Source

This repository provides a fully local instance of **Mock Open Insurance**, including all components required to comply with Open Insurance (OPIN) standards and API specifications.

Certificates for the Certificate Authority and mTLS clients are located in [`mock-service-os/certs`](./mock-service-os/certs).

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Instructions for macOS / Linux](#instructions-for-macos--linux)
- [Instructions for Windows](#instructions-for-windows)
- [Obtaining a Token Using Certificates](#obtaining-a-token-using-certificates)

---

## Prerequisites

To run Mock Insurance OS, ensure the following tools are installed:

- **Java 17**
- **Gradle**
- **Docker**

---

Below are the steps to run and interact with Mock OPIN on your local machine.

If you're using macOS or Linux, refer to the [Instructions for macOS / Linux](#instructions-for-macos--linux).

If you're using Windows, refer to the [Instructions for Windows](#instructions-for-windows).


## Instructions for macOS / Linux

The following instructions cover how to run and interact with Mock OPIN on macOS or Linux, as well as how to configure and execute it together with the Conformance Suite.

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

To run a fully local instance, start all components using Docker:

```bash
make run
```

### 4. Run with Conformance Suite

To run a fully local instance with the Conformance Suite, you need to start all components, along with the Conformace Suite itself.

1. Run the command below to set up the Conformance Suite:

   ```bash
   make setup-cs
   ```

2. Then start the environment with:

   ```bash
   make run-with-cs
   ```

3. Once running, you can access the environment via:

   ```
   https://localhost:8443
   ```

Configuration for the tests is available in the `cs_config.json` file.

---

## Instructions for Windows

> ⚠️ These instructions assume you're using a Linux-compatible terminal such as **WSL** (Windows Subsystem for Linux) or **Git Bash**.

The following instructions cover how to run and interact with Mock OPIN on Windows, as well as how to configure and execute it together with the Conformance Suite.

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

To run a fully local instance, start all components using Docker:

```bash
make run
```

Wait for the containers to be fully initialized, then stop them before continuing to the next step.

### 4. Modify the Makefile in Order to Run with Conformance Suite

1. In the root `Makefile`, locate the `setup-cs` section, related to cloning the Conformance Suite repository and initial setup, and do the following:

   - **Comment out** line 15 (which builds the Conformance Suite)
   - **Keep only** lines 11, 12, and 13 in `setup-cs`

2. Save the file, then run:

   ```bash
   make setup-cs
   ```

3. Edit the same `Makefile` again:
   - **Uncomment** line 15
   - **Comment out** lines 11, 12, and 13 (to avoid re-downloading)

4. Save and run the command again to set up the conformance suite:

   ```bash
   make setup-cs
   ```

### 5. Run with Conformance Suite

1. Then start the environment with:

   ```bash
   make run-with-cs
   ```
   
2. Once running, you can access the environment via:

   ```
   https://localhost:8443
   ```

Configuration for the tests is available in the `cs_config.json` file.

### 6. (Optional) Fix Dockerfile Issues
In some environments, Docker may fail to locate the `buster` image. If this occurs:

1. Edit the following three `Dockerfile` files inside the `conformance-suite` folder as it follows:
   - <img width="500" alt="image" src="https://github.com/user-attachments/assets/24a28bec-fa78-4fd3-ae2e-ef357bce0dca" />
   - <img width="500" alt="image" src="https://github.com/user-attachments/assets/74c2fa98-f6ce-4c3d-8488-9e5a2a36dae6" />
   - <img width="500" alt="image" src="https://github.com/user-attachments/assets/fc88a84f-c291-4768-be95-88e1c4e4ff4d" />
2. Ensure they are pointing to valid base images (e.g., replace `buster` with an available image that matches your environment).
3. Return to step [5. Run with Conformance Suite](#5-run-with-conformance-suite)

---

## Obtaining a Token Using Certificates

To request a token using the client certificate:

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
