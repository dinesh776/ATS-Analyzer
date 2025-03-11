# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-slim

LABEL author="Dinesh Gvns"

LABEL maintainer="dineshgvns776@gmail.com"

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file into the container at /app
COPY target/Analyzer-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on (default 8080)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
