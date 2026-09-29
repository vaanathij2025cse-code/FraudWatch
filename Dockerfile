# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for caching
COPY demo/pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy application source code and package the jar
COPY demo/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Lean runtime container
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the generated JAR file
COPY --from=build /app/target/*.jar app.jar

# Dynamic port for Render
ENV PORT=8080
EXPOSE 8080

# Optimize JVM memory footprint for Render free tier (512 MB limit)
ENV JAVA_OPTS="-Xmx384m -Xms128m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
