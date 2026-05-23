# Build stage
FROM gradle:jdk-25-and-25 AS build
WORKDIR /home/gradle/src

# Copy Gradle wrapper and settings
COPY --chown=gradle:gradle gradlew .
RUN chmod +x gradlew
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle settings.gradle.kts .
COPY --chown=gradle:gradle backend/build.gradle.kts backend/

# Prefetch dependencies to cache them in a layer
RUN ./gradlew :backend:dependencies --no-daemon > /dev/null || true

# Copy source code and build
COPY --chown=gradle:gradle backend/src backend/src
RUN ./gradlew :backend:build -x test --no-daemon

# Run stage
FROM eclipse-temurin:25-jre-alpine

ARG PORT=4556

RUN addgroup --system runner && adduser --system runner --ingroup runner
USER runner
WORKDIR /app

COPY --from=build /home/gradle/src/backend/build/libs/backend-1.0-SNAPSHOT.jar app.jar

# Port should match application.yml or be overridden via SERVER_PORT env var
EXPOSE $PORT

ENTRYPOINT ["java", "-jar", "app.jar"]
