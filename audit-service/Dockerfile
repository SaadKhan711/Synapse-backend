# Use a lightweight, official OpenJDK base image for Java 17
FROM openjdk:17-slim

# Set a working directory inside the container
WORKDIR /app

# Copy the compiled JAR file from the 'target' directory into the container.
# The JAR file is created when we build the project with Maven.
# The wildcard (*) is used because the JAR file name includes the version number.
COPY target/*.jar app.jar

# Expose the port that the Spring Boot application will run on.
# This is just for documentation; the actual port mapping is done in docker-compose.yml.
EXPOSE 8081

# The command to run when the container starts up.
ENTRYPOINT ["java", "-jar", "app.jar"]
