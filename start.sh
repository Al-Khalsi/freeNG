#!/bin/sh

# Copy the appropriate .env file based on NODE_ENV
if [ "$NODE_ENV" = "production" ]; then
    cp .env.production .env.local
else
    cp .env.local .env.local
fi

# Start the application
npm start