FROM eclipse-temurin:17-jdk

# Install Python, FFmpeg, and Node.js (Required JavaScript runtime for yt-dlp)
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    nodejs \
    && rm -rf /var/lib/apt/lists/*

# Install / update yt-dlp to the latest version
RUN pip3 install --upgrade yt-dlp

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]