FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install --no-cache-dir --upgrade yt-dlp

WORKDIR /app

COPY target/econverter-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]