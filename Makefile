.PHONY: setup setup-cs run run-with-cs cs-config

setup:
	@$(MAKE) -C ./insurance-server-lambdas swagger
	@$(MAKE) -C ./insurance-server-lambdas certs

run:
	@$(MAKE) -C ./insurance-server-lambdas run

setup-cs:
	@$(MAKE) -C ./insurance-server-lambdas setup-cs

cs-config:
	@$(MAKE) -C ./insurance-server-lambdas cs-config

run-with-cs:
	@$(MAKE) -C ./insurance-server-lambdas run-with-cs
