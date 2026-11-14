# ==============================================================================
# Stage 1: Build stage with official Maven and OpenJDK 21
# ==============================================================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build executable Spring Boot JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==============================================================================
# Stage 2: Production runtime stage with official OpenJDK 21 JRE
# ==============================================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Install curl for container health checks
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Create dedicated non-root application user for container security
RUN groupadd -r finova && useradd -r -g finova -d /app -s /sbin/nologin finova

# Create durable uploads and application storage directories
RUN mkdir -p /var/data/uploads /app/data && chown -R finova:finova /app /var/data

# Copy packaged JAR from builder stage
COPY --from=builder --chown=finova:finova /app/target/finova-backend-*.jar app.jar

# Switch to non-root user
USER finova

# Environment defaults (Render overrides $PORT dynamically)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV UPLOAD_DIR=/var/data/uploads
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

EXPOSE 8080

# Health check configuration for monitoring
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/api/health || exit 1

# Execute Spring Boot application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
