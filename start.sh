#!/bin/sh

# Switch to root user to copy the .env file
if [ "$(id -u)" -ne 0 ]; then
    exec sudo "$0" "$@"
fi

# Copy the appropriate .env file based on NODE_ENV
if [ "$NODE_ENV" = "production" ]; then
    cp .env.production .env 
    echo "Switched to production mode"
else
    cp .env.local .env 
    echo "Switched to local mode"
fi

# Switch back to node user
exec su node -c "npm start"