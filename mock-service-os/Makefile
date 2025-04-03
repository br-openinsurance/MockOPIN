.PHONY: setup run certs

ifeq ($(OS),Windows_NT)
    SHELL := pwsh.exe
    SHELLFLAGS := -NoProfile -Command
	MAKE_CERTS := if (-not $$env:ORG_ID) { $$env:ORG_ID = Read-Host "Enter Organization ID" }; docker-compose run certmaker ./main --org_id="$$env:ORG_ID"
else
    SHELL := /bin/sh
	MAKE_CERTS := if [ -z "$$ORG_ID" ]; then read -p "Enter Organization ID: " ORG_ID; fi; docker-compose run certmaker ./main --org_id="$$ORG_ID"
endif

setup:
	@$(MAKE) certs

run:
	@docker-compose build && docker-compose up

certs:
	@echo "Making new certs"
	@$(SHELL) -c '$(MAKE_CERTS)'
