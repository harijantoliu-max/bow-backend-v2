FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY bow-shared/pom.xml bow-shared/pom.xml
COPY bow-app/pom.xml bow-app/pom.xml
COPY bow-shared/src bow-shared/src
COPY bow-app/src bow-app/src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /app/bow-app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
