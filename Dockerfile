# syntax=docker/dockerfile:1

ARG NODE_VERSION=22.9.0

################################################################################
# Use node image for base image for all stages.
FROM node:${NODE_VERSION}-alpine AS base

# Set working directory for all build stages.
WORKDIR /usr/src/app

################################################################################
# Create a stage for installing production dependencies.
FROM base AS deps

# Download dependencies as a separate step to take advantage of Docker's caching.
RUN --mount=type=bind,source=package.json,target=package.json \
    --mount=type=bind,source=package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci --omit=dev

################################################################################
# Create a stage for building the application.
FROM deps AS build

# Accept build arguments for environment variables
ARG NEXT_PUBLIC_BACKEND_API_VERSION
ARG NEXT_PUBLIC_BACKEND_BASE_URL
ARG NEXT_PUBLIC_BACKEND_BASE_URL_PRODUCTION

# Download additional development dependencies before building
RUN --mount=type=bind,source=package.json,target=package.json \
    --mount=type=bind,source=package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci

# Copy the rest of the source files into the image.
COPY . .

# Set environment variables at build time
ENV NEXT_PUBLIC_BACKEND_API_VERSION=${NEXT_PUBLIC_BACKEND_API_VERSION}
ENV NEXT_PUBLIC_BACKEND_BASE_URL=${NEXT_PUBLIC_BACKEND_BASE_URL}
ENV NEXT_PUBLIC_BACKEND_BASE_URL_PRODUCTION=${NEXT_PUBLIC_BACKEND_BASE_URL_PRODUCTION}

# Run the build script
RUN npm run build

################################################################################
# Create a new stage to run the application with minimal runtime dependencies
FROM base AS final

# Use production node environment by default.
ENV NODE_ENV production

# Copy the production dependencies from the deps stage and the built application
COPY --from=deps /usr/src/app/node_modules ./node_modules
COPY --from=build /usr/src/app/.next ./.next
COPY --from=build /usr/src/app/public ./public
COPY --from=build /usr/src/app/package.json ./package.json

EXPOSE 3000

# Run the application
CMD ["npm", "start"]
