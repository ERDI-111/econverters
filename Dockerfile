FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    nodejs \
    && rm -rf /var/lib/apt/lists/*

# Added --break-system-packages to bypass the Debian environment restriction
RUN pip3 install --upgrade --break-system-packages yt-dlp

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]