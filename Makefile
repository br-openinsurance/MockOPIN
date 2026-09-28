.PHONY: setup setup-cs certs cs-config down clean-run run run-with-cs build rebuild

setup:
	cd insurance-swagger; BUILD_NUMBER=2 ./gradlew publishToMavenLocal

certs:
	cd insurance-server-lambdas; $(MAKE) certs

cs-config:
	cd insurance-server-lambdas; $(MAKE) cs-config
	mv insurance-server-lambdas/cs_config.json cs_config.json
	@echo "Conformance Suite config written to cs_config.json"

run:
	cd insurance-server-lambdas; ./gradlew optimizedDockerBuild -x test
	cd insurance-server-lambdas; docker-compose --profile main up

setup-cs:
	@if test ! -d "insurance-server-lambdas/conformance-suite"; then \
	  echo "Cloning open insurance conformance suite repository..."; \
	  git clone --branch main --single-branch --depth=1 https://gitlab.com/raidiam-conformance/open-insurance/open-insurance-brasil.git insurance-server-lambdas/conformance-suite; \
	fi
	
	@cd insurance-server-lambdas; docker compose run cs-builder

run-with-cs:
	cd insurance-server-lambdas; ./gradlew optimizedDockerBuild -x test
	cd insurance-server-lambdas; docker-compose --profile main --profile cs up

build:
	cd insurance-swagger; BUILD_NUMBER=2 ./gradlew publishToMavenLocal
	cd insurance-server-lambdas; ./gradlew optimizedDockerBuild -x test
	cd insurance-server-lambdas; docker compose build auth
	cd insurance-server-lambdas; docker compose build mtls

down:
	-cd insurance-server-lambdas; $(MAKE) down-volumes

clean-run:
	@$(MAKE) down
	@$(MAKE) certs
	@$(MAKE) cs-config
	@$(MAKE) setup-cs
	@$(MAKE) build
	cd insurance-server-lambdas; docker-compose --profile main --profile cs up
