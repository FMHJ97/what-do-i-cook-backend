# Stage 1 — Build the project using Maven and Java 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 — Run the app using only the JAR (smaller final image)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/what-do-i-cook-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]