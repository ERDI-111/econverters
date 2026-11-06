FROM eclipse-temurin:17-jdk-jammy

# Install Python, pip, ffmpeg, curl, unzip, and maven
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    ffmpeg \
    curl \
    unzip \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Install latest yt-dlp
RUN pip3 install --no-cache-dir --upgrade yt-dlp

# Install Deno (automatically recognized by yt-dlp as the default JS runtime)
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH="/root/.deno/bin:$PATH"

WORKDIR /app
COPY . .

# Build using standard system maven
RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/econverter-0.0.1-SNAPSHOT.jar"]