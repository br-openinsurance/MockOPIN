.PHONY: setup setup-cs run run-with-cs cs-config

ifeq ($(OS),Windows_NT)
    COPY_CMD := cmd /C copy /Y
else
    COPY_CMD := cp
endif

setup:
	$(MAKE) -C ./insurance-server-lambdas swagger
	$(MAKE) -C ./insurance-server-lambdas certs

run:
	$(MAKE) -C ./insurance-server-lambdas run

setup-cs:
	$(MAKE) -C ./insurance-server-lambdas setup-cs
	$(MAKE) -C cs-config

cs-config:
	$(MAKE) -C ./insurance-server-lambdas cs-config
	$(COPY_CMD) ./insurance-server-lambdas/cs_config.json ./cs_config.json

run-with-cs:
	$(MAKE) -C ./insurance-server-lambdas run-with-cs
