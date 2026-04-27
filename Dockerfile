# Stage 1: Build the JAR using Gradle
FROM gradle:8-jdk21 AS build
WORKDIR /home/gradle/src
# Copy project files
COPY --chown=gradle:gradle . .
# Build the JAR (skipping tests to speed up deployment)
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the runner image
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
