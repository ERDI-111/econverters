FROM eclipse-temurin:17-jdk-jammy

# Add deadsnakes PPA to get Python 3.11+ (bypassing the deprecation error)
RUN apt-get update && apt-get install -y software-properties-common \
    && add-apt-repository ppa:deadsnakes/ppa \
    && apt-get update && apt-get install -y \
    python3.11 \
    python3.11-distutils \
    ffmpeg \
    curl \
    unzip \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Set Python 3.11 as the default python3
RUN update-alternatives --install /usr/bin/python3 python3 /usr/bin/python3.11 1

# Install pip for Python 3.11
RUN curl -sS https://bootstrap.pypa.io/get-pip.py | python3.11

# Install latest yt-dlp
RUN python3 -m pip install --no-cache-dir --upgrade yt-dlp

# Install Deno for signature decryption
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH="/root/.deno/bin:$PATH"

WORKDIR /app
COPY . .

# Build using system maven
RUN mvn clean package -DskipTests

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/econverter-0.0.1-SNAPSHOT.jar"]