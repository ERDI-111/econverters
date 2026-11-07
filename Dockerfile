FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/econverter-0.0.1-SNAPSHOT.jar"]