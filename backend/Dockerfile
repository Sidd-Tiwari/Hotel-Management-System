FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package > build.log 2>&1 || (cat build.log && exit 1)

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/hotel-management-backend.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
