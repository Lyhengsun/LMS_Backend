FROM gradle:jdk21-ubi-minimal AS build

WORKDIR /app

# Copy the Maven descriptor first (for dependency download optimizations)
COPY gradlew .
COPY gradle ./gradle
COPY settings.gradle .
COPY build.gradle .
COPY src ./src

# Pre-download dependencies (without running full build)
RUN ./gradlew dependencies --no-daemon || true

# Build the application (skip tests if desired)
RUN ./gradlew clean build -x test --no-daemon

# ──────────────────────────────
# 2) Runtime Stage
# ──────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the generated .jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]