# Build stage
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:22-jdk-alpine
COPY --from=build /app/target/WaterDeliveryApplication-0.0.1-SNAPSHOT.jar /WaterDeliveryApplication.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/WaterDeliveryApplication.jar"]