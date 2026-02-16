# ==========================================
# Stage 1: Build Stage
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy configuration first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the artifact
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime Stage
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Configure Timezone (Critical for correct database timestamps)
RUN apk add --no-cache tzdata
ENV TZ=Asia/Riyadh

# Copy the executable JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]