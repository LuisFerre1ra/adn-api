# ========================================
# ETAPA 1: BUILD (Compilación)
# ========================================
FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk update

# Copiar código fuente
COPY . .

# Dar permisos al wrapper Gradle
RUN chmod +x ./gradlew

# Compilar usando el profile "prod" y renombrando el JAR a app.jar
RUN ./gradlew bootJar --no-daemon -Pspring.profiles.active=prod

# ========================================
# ETAPA 2: RUNTIME (Ejecución)
# ========================================
FROM eclipse-temurin:21-jre-alpine

# Puerto expuesto
EXPOSE 8080

# Activar el perfil prod en runtime
ENV SPRING_PROFILES_ACTIVE=prod

# Copiar el jar generado desde la etapa anterior
COPY --from=build /build/libs/adn-api-0.0.1-SNAPSHOT.jar /app/app.jar

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]