# Stage 1: Build the JAR using Gradle
FROM gradle:8-jdk21 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

# Fix permission denied error
RUN chmod +x gradlew

# Build the JAR
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the runner image
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
