# Utiliza una imagen base de OpenJDK 17 en Alpine Linux
FROM adoptopenjdk/openjdk17:alpine-jre

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación Spring Boot al contenedor
COPY target/tu-aplicacion.jar app.jar

# Define un volumen para montar la carpeta de archivos en el host
VOLUME /ruta/local/donde/se/guardaran/los/archivos

# Expone el puerto en el que tu aplicación Spring Boot está escuchando
EXPOSE 8080

# Establece el comando de inicio para ejecutar tu aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]