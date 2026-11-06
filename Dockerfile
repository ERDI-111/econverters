FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y \
    maven \
    python3 \
    python3-pip \
    ffmpeg \
    nodejs \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install --upgrade --break-system-packages yt-dlp

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]