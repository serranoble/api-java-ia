# Etapa 1: compilación del JAR con Maven Wrapper y JDK 21
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

# Etapa 2: imagen final liviana y autocontenida con JRE 21
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN mkdir -p /app/data

COPY --from=build /workspace/target/products-api-1.0.0.jar /app/app.jar

ENV SPRING_DATASOURCE_URL=jdbc:sqlite:/app/data/products.db

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
