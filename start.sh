#!/bin/sh

# Copy the appropriate .env file based on NODE_ENV
if [ "$NODE_ENV" = "production" ]; then
    cp .env.production .env.local
    echo "Switched to production mode"
else
    cp .env.local .env.local
    echo "Switched to local mode"
fi

# Start the application
npm start