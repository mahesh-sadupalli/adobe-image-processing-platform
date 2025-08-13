# Simple single-stage build
FROM openjdk:17-jdk-slim

# Install curl for health checks and create app user
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    addgroup --system spring && \
    adduser --system spring --ingroup spring

# Set working directory
WORKDIR /app

# Copy the pre-built JAR (we'll build it locally first)
COPY upload-service/target/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user for security
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set memory optimization for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]