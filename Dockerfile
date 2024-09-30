FROM openjdk:17-jdk-alpine
COPY target/files-0.0.1-SNAPSHOT.jar files-app.jar
ENTRYPOINT ["java", "-jar", "files-app.jar"]
EXPOSE 8080