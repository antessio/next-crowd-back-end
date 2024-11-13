# Use an official Maven image to build the application
FROM maven:3.9.6-eclipse-temurin-22-jammy AS build
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY domain ./domain
COPY infrastructure ./infrastructure

RUN mvn clean install -DskipTests
RUN mvn clean package -DskipTests -f ./infrastructure/pom.xml

# Use an official Amazon Corretto image to run the application
FROM amazoncorretto:22
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/infrastructure/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
