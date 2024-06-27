# Use Maven image with JDK 17 for both build and runtime
FROM maven:3.8.4-eclipse-temurin-17 AS build-env

# Set the working directory in the container
WORKDIR /app

# Copy the project files into the container
COPY pom.xml .
COPY src src

# Clean and package the application using Maven
RUN mvn clean package

# Use a lightweight Alpine Linux image for the runtime
FROM eclipse-temurin:17-jre-alpine

# Set JAVA_HOME environment variable to point to the JDK 17 installation
ENV JAVA_HOME /opt/java/openjdk
ENV PATH $JAVA_HOME/bin:$PATH

# Set the working directory for your application
WORKDIR /app

# Copy the compiled JAR file from the build stage to the runtime stage
COPY --from=build-env /app/target/RecordLinkage.jar /app.jar



# Define the default command to run when the container starts
CMD ["java", "-jar", "/app.jar"]


