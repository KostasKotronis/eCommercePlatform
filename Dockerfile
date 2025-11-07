# ---- Build stage: compiles the JAR inside Docker ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage: runs the built JAR on a slim JRE ----
FROM eclipse-temurin:21-jre
ENV TZ=Europe/Athens
RUN useradd -ms /bin/bash appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
USER appuser
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
