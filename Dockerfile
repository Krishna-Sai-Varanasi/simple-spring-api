# Multistage Dockerfile for building Java Spring Boot REST API application
# Stage 1 - Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml
COPY pom.xml .

# Download maven dependencies and cache them
# This goal tells Maven to download all project dependencies, plugins, and reports without building the project.
# -B flag is for batch mode, which is useful in CI/CD environments.
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2 - Create the final image with the built application
# Alpine version of the JRE is used for a smaller image size
# Use a minimal JRE image to run the application
FROM eclipse-temurin:21-jre-alpine

# Run as a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership of the JAR file to the non-root user
RUN chown spring:spring app.jar

# Switch to the non-root user
USER spring:spring

# Expose the port the application runs on
EXPOSE 9595

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]