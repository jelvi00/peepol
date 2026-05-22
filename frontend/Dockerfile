# Build stage
FROM node:22-slim AS build
RUN corepack enable && corepack prepare pnpm@10 --activate
WORKDIR /app

# Install dependencies separately to leverage Docker cache
COPY frontend/package.json frontend/pnpm-lock.yaml* ./
RUN pnpm install --frozen-lockfile

# Copy source code and build
COPY frontend/ .
RUN pnpm build

# Run stage
FROM node:22-slim

ARG PORT=3000

WORKDIR /app

# Set production environment
ENV NODE_ENV production
ENV PORT=$PORT
ENV HOSTNAME "0.0.0.0"

# Create a non-root user for security
RUN addgroup --system --gid 1001 nodejs && \
    adduser --system --uid 1001 nextjs

# Copy standalone build and necessary files
COPY --from=build /app/public ./public
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static

# Ensure the app runs as the non-root user
USER nextjs

# Next.js standalone listens on 3000 by default,
# can be overridden by PORT env var
EXPOSE $PORT

CMD ["node", "server.js"]
