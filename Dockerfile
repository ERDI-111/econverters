FROM eclipse-temurin:17-jdk

# Install Maven, Python, FFmpeg, and Node.js inside the container
RUN apt-get update && apt-get install -y \
    maven \
    python3 \
    python3-pip \
    ffmpeg \
    nodejs \
    && rm -rf /var/lib/apt/lists/*

# Install yt-dlp for media extraction
RUN pip3 install --upgrade --break-system-packages yt-dlp

WORKDIR /app

# Copy all your project files to the container
COPY . .

# Build the Spring Boot app automatically during deployment
RUN mvn clean package -DskipTests

EXPOSE 8080

# Run the newly built jar from the target folder
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]