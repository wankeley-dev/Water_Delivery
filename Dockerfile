# Build stage
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage (Use OpenJDK 22 instead of Java 17)
FROM openjdk:22
COPY --from=build /app/target/*.jar /WaterDeliveryApplication.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/WaterDeliveryApplication.jar"]
