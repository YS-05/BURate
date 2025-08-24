FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Gradle files
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew clean build

# Copy the JAR file (not the plain one)
RUN cp build/libs/CourseGraderBackend-*-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]