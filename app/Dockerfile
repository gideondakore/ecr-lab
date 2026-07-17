# syntax=docker/dockerfile:1

# ---- Build stage: compile the Maven project --------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# ---- Runtime stage: minimal JRE, non-root -----------------------------------
FROM eclipse-temurin:21-jre-alpine
ENV PORT=8080
WORKDIR /app

COPY --from=build /build/target/app.jar ./app.jar

# Unprivileged runtime user
RUN addgroup -S app && adduser -S app -G app
USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
