FROM eclipse-temurin:17-jdk-jammy

# Install base utilities, curl, unzip, and system maven
RUN apt-get update && apt-get install -y \
    software-properties-common \
    curl \
    unzip \
    maven \
    python3 \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

# Install the absolute latest static build of ffmpeg directly from John Van Sickle's builds
RUN curl -L https://www.johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz -o ffmpeg.tar.xz \
    && mkdir -p /tmp/ffmpeg \
    && tar -xf ffmpeg.tar.xz -C /tmp/ffmpeg --strip-components=1 \
    && cp /tmp/ffmpeg/ffmpeg /usr/local/bin/ \
    && cp /tmp/ffmpeg/ffprobe /usr/local/bin/ \
    && rm -rf ffmpeg.tar.xz /tmp/ffmpeg

# Install the absolute latest yt-dlp binary directly from GitHub (bypassing outdated pip packages)
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp \
    && chmod a+rx /usr/local/bin/yt-dlp

# Install Deno for JS runtime challenge solving
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH="/root/.deno/bin:$PATH"

WORKDIR /app
COPY . .

# Build using system maven
RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/econverter-0.0.1-SNAPSHOT.jar"]