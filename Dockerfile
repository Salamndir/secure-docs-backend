

# ==========================================
# Runtime Stage (CI-Driven Build)
# ==========================================

# Using a lightweight JRE image since the application is already compiled by GitHub Actions
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Configure Timezone (Critical for correct database timestamps)
RUN apk add --no-cache tzdata
ENV TZ=Asia/Riyadh

# Copy the executable JAR from the builder stage
COPY target/*.jar app.jar
# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]