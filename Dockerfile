FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S sa && adduser -S sa -G sa

WORKDIR /app

COPY --from=build --chown=sa:sa /app/target/solution-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m"

EXPOSE 8080

USER sa

ENTRYPOINT ["java", "-jar", "app.jar"]