.PHONY: setup setup-cs run run-with-cs image swagger mock-service-os cs cs-config

ORGANIZATION_ID=76b370e3-def5-4798-8b6a-915cb5d6dd74
PATH_TO_MOCK_OS="../mock-service-os"
PATH_TO_SWAGGER="../insurance-swagger"
SWAGGER_BUILD_NUMBER=2

ifeq ($(OS),Windows_NT)
    SHELL := pwsh.exe
    SHELLFLAGS := -NoProfile -Command
    MKDIR := New-Item -ItemType Directory
    MAKE_IMAGE_OPTIONAL := $choice = Read-Host "Do you want to build the API image? (y/N)"; if ($choice -eq 'y') { make image } else { Write-Host "Skipping image build" }
    MAKE_IMAGE := ./gradlew.bat optimizedDockerBuild -x test
    MAKE_SWAGGER := Set-Location $(PATH_TO_SWAGGER); ./gradlew.bat publishToMavenLocal -PBUILD_NUMBER=$(SWAGGER_BUILD_NUMBER)
else
    SHELL := /bin/sh
    MKDIR := mkdir -p
    MAKE_IMAGE_OPTIONAL := read -p "Do you want to build the API image? (y/N) " choice && if [ "$$choice" = "y" ]; then make image; else echo "Skipping image build"; fi
    MAKE_IMAGE := ./gradlew optimizedDockerBuild -x test
    MAKE_SWAGGER := cd $(PATH_TO_SWAGGER); BUILD_NUMBER=$(SWAGGER_BUILD_NUMBER) ./gradlew publishToMavenLocal
endif

setup:
	@$(MAKE) swagger
	@$(MAKE) mock-service-os
	@$(MAKE) build-auth-mtls

setup-cs:
	@$(MAKE) cs
	@$(MAKE) cs-config

build-auth-mtls:
	@docker-compose build auth
	@docker-compose build mtls

hosts:
	@sudo -- sh -c -e "echo '\n127.0.0.1 api.local\n127.0.0.1 matls-api.local\n127.0.0.1 auth.local\n127.0.0.1 matls-auth.local\n127.0.0.1 directory' >> /etc/hosts";

run:
	@$(SHELL) -c '$(MAKE_IMAGE_OPTIONAL)'
	@docker-compose --profile main up

run-with-cs:
	@$(SHELL) -c '$(MAKE_IMAGE_OPTIONAL)'
	@docker-compose --profile main --profile cs up

image:
	@echo "Building API docker image"
	@$(SHELL) -c '$(MAKE_IMAGE)'

swagger:
	@if test ! -d "$(PATH_TO_SWAGGER)"; then \
	  echo "Cloning the insurance-swagger repo"; \
	  git clone git@github.com:raidiam/insurance-swagger.git $(PATH_TO_SWAGGER); \
	fi

	@echo "Building Swagger JAR"
	@$(SHELL) -c '$(MAKE_SWAGGER)'

mock-service-os:
	@if test ! -d "$(PATH_TO_MOCK_OS)"; then \
	  echo "Cloning the mock-service-os repo"; \
	  git clone git@github.com:raidiam/mock-service-os.git $(PATH_TO_MOCK_OS); \
	fi

	@echo "Setting Up the Mock Service OS"
	@ORG_ID="OPIBR-$(ORGANIZATION_ID)" $(MAKE) -C $(PATH_TO_MOCK_OS) setup

certs:
	@echo "Setting Up the Mock Service OS Keys"
	@ORG_ID="OPIBR-$(ORGANIZATION_ID)" $(MAKE) -C $(PATH_TO_MOCK_OS) certs

cs:
	# Clone and build the Open Insurance Conformance Suite.
	# Note: The Dockerfile to build the conformance suite jar is missing, then it is being added it manually.
	@if test ! -d "conformance-suite"; then \
	  echo "Cloning open insurance conformance suite repository..."; \
	  git clone --branch main --single-branch --depth=1 https://gitlab.com/raidiam-conformance/open-insurance/open-insurance-brasil.git conformance-suite; \
	  $(MKDIR) conformance-suite/server-dev; \
	  echo 'FROM openjdk:17-jdk-slim\n\nRUN apt-get update && apt-get install redir' > conformance-suite/server-dev/Dockerfile; \
	fi

	@echo "Building Conformance Suite"
	@docker compose run cs-builder

# Create a Conformance Suite configuration file using the client certs in /certs.
cs-config:
	@jq -n \
	   --arg orgId "$(ORGANIZATION_ID)" \
	   --arg clientOneCert "$$(<../mock-service-os/certs/client_one.crt)" \
	   --arg clientOneKey "$$(<../mock-service-os/certs/client_one.key)" \
	   --arg clientTwoCert "$$(<../mock-service-os/certs/client_two.crt)" \
	   --arg clientTwoKey "$$(<../mock-service-os/certs/client_two.key)" \
	   --argjson clientOneJwks "$$(jq . < ../mock-service-os/certs/client_one.jwks)" \
	   --argjson clientTwoJwks "$$(jq . < ../mock-service-os/certs/client_two.jwks)" \
	   '{ \
		  "alias": "mock", \
		  "client": { \
	        "client_id": "client_one", \
			"jwks": $$clientOneJwks \
	      }, \
		  "mtls": { \
		    "cert": $$clientOneCert, \
			"key": $$clientOneKey, \
		  }, \
		  "client2": { \
	        "client_id": "client_two", \
			"jwks": $$clientTwoJwks \
	      }, \
		  "mtls2": { \
		    "cert": $$clientTwoCert, \
			"key": $$clientTwoKey, \
		  }, \
		  "server": { \
			"discoveryUrl": "https://auth.local/.well-known/openid-configuration" \
	      }, \
		  "resource": { \
	        "brazilOrganizationId": $$orgId, \
	        "consentUrl": "https://api.local/open-insurance/consents/v2/consents", \
            "brazilCpf": "76109277673" \
	      }, \
		  "directory": { \
		    "discoveryUrl": "https://directory/.well-known/openid-configuration", \
		    "apibase": "https://directory", \
		    "client_id": "random_client" \
		  } \
	    }' > cs_config.json

	@echo "New Conformance Suite config successfully written to cs_config.json"
