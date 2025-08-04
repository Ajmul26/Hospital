# Multi-stage Docker build for Telecommunications Network Optimizer
# This Dockerfile builds both the React frontend and Spring Boot backend

# Stage 1: Build React Frontend
FROM node:18-alpine AS frontend-build

WORKDIR /app/frontend

# Copy package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy frontend source
COPY frontend/ ./

# Build the React application
RUN npm run build

# Stage 2: Build Spring Boot Backend
FROM maven:3.9-openjdk-17 AS backend-build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml ./

# Download dependencies (for better caching)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/

# Build the application
RUN mvn clean package -DskipTests

# Stage 3: Runtime Environment
FROM openjdk:17-jre-slim

# Install curl for health checks
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r telecom && useradd -r -g telecom telecom

# Set working directory
WORKDIR /app

# Copy built artifacts
COPY --from=backend-build /app/target/*.jar app.jar
COPY --from=frontend-build /app/frontend/build ./static/

# Create necessary directories
RUN mkdir -p logs && \
    chown -R telecom:telecom /app

# Switch to non-root user
USER telecom

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xmx1024m -Xms512m" \
    SPRING_PROFILES_ACTIVE=docker \
    MONGODB_URI=mongodb://mongodb:27017/telecom_network_optimizer

# Start the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]