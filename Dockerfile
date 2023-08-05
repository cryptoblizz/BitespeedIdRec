# Use a base image with Maven
FROM maven:3.8.1-openjdk-17-slim
# Set the working directory inside the container
WORKDIR /app

# Copy the application source code and pom.xml
COPY pom.xml .
COPY src/ ./src/

# Build the application using Maven
RUN mvn clean package -DskipTests=true

# Expose the default port

# Set environment variables (if necessary)
ENV SPRING_PROFILES_ACTIVE=production

# Specify the startup command
CMD ["java", "-jar", "target/identityRec-1.0.0.jar"]
