
FROM eclipse-temurin:17-jre
LABEL maintainer="moses@gmail.com"

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
