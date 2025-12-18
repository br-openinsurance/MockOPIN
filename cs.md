# Conformance Suite

To run the Conformance Suite locally and integrate it with a local instance of the Mock Open Insurance (Mock OPIN) server, we had to implement a few adjustments.

Our setup is based on the official docker-compose.yml provided by the Conformance Suite repository. In our project, we use insurance-server-lambdas/docker-compose.yml, where the services prefixed with cs- represent the Conformance Suite server and its dependencies.

The Conformance Suite, running inside a container, makes requests to hosts such as:

- matls-auth.local
- auth.local
- matls-api.local
- api.local
- directory

These hosts are resolved by the mtls container, implemented in mock-service-os/mock_mtls. This container acts as a forward proxy, routing the requests to:

- auth → representing the Authorization Server (AS)
- mockapi → representing the Open Insurance Resource Server

The mtls container also serves mock participant information, which is defined in insurance-server-lambdas/participants.json.

## Clients and Authentication

To authenticate with the Authorization Server, the Conformance Suite uses pre-configured clients such as client_one and client_two. These clients are seeded into the auth container using files located in mock-service-os/mock_as/mongo-seed.

The clients use certificates stored in mock-service-os/mock_as/certs and are configured with allowed redirect URIs such as:

```bash
https://localhost.emobix.co.uk:8443/test/a/mock/callback
```

This URI corresponds to the redirect endpoint that the Conformance Suite builds for the alias mock, as specified in the configuration file `cs_config.json`.

## Endpoint Configuration

When executing tests, the Conformance Suite sends requests to auth.local and api.local, as these values are defined in the test configuration file.
