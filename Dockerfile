# Etapa 1: compilação da aplicação
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Compila a aplicação e executa os testes automatizados
RUN mvn -B clean package

# Etapa 2: execução da aplicação
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/industrial-maintenance-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]