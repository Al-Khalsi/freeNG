#!/bin/sh

# Copy the appropriate .env file based on NODE_ENV
if [ "$NODE_ENV" = "production" ]; then
      cp .env.production .env
    echo "Switched to production mode"
else
    cp .env.local .env
    echo "Switched to local mode"
fi

# Start the application
npm start