#
# Build stage
#
FROM maven:3.8.5-openjdk-17 AS build
# download and cache maven dependencies
COPY pom.xml /app/pom.xml
RUN mvn -f /app/pom.xml verify --fail-never

COPY src /app/src
RUN mvn -f /app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM openjdk:17.0.1
LABEL authors="musht"

COPY --from=build /app/target/*.jar /usr/local/lib/chat_backend.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/chat_backend.jar"]