# Étape 1 : Build avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
# Télécharger les dépendances en cache
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests -B

# Étape 2 : Image de production
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Le profil Spring Boot est passé via la variable d'environnement
ENV SPRING_PROFILES_ACTIVE=create

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
