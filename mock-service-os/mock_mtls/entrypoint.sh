#!/bin/sh

# Check if the config file exists
if [ -n "$CONFIG_FILE" ] && [ -f "$CONFIG_FILE" ]; then
    echo "Config file found: $CONFIG_FILE"
    # Read the content of the config file and set it as an environment variable
    export API_GATEWAY_URI=$(cat $CONFIG_FILE)
else
    echo "Config file not found: $CONFIG_FILE"
    echo "Using default gateway URI"
    export API_GATEWAY_URI="http://mockapi:8080"
fi

echo "API Gateway: $API_GATEWAY_URI"

# Execute the passed command
exec "$@"