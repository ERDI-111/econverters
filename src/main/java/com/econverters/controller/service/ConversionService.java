package com.econverters.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.UUID;

@Service
public class ConversionService {

    public File convertMedia(String videoUrl, String format) throws Exception {
        String outputDir = System.getProperty("java.io.tmpdir");
        String fileName = UUID.randomUUID().toString();
        String outputTemplate = outputDir + "/" + fileName + ".%(ext)s";

        ProcessBuilder processBuilder;
        if ("mp3".equalsIgnoreCase(format)) {
            processBuilder = new ProcessBuilder(
                "yt-dlp",
                "-4",
                "--extractor-args", "youtube:player_client=ios,web",
                "--user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
                "-x", "--audio-format", "mp3",
                "-o", outputTemplate,
                videoUrl
            );
        } else {
            processBuilder = new ProcessBuilder(
                "yt-dlp",
                "-4",
                "--extractor-args", "youtube:player_client=ios,web",
                "--user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
                "-f", "bestvideo+bestaudio/best",
                "--merge-output-format", "mp4",
                "-o", outputTemplate,
                videoUrl
            );
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp extraction failed.");
        }

        File dir = new File(outputDir);
        File[] matchedFiles = dir.listFiles((d, name) -> name.startsWith(fileName));
        if (matchedFiles != null && matchedFiles.length > 0) {
            return matchedFiles[0];
        }

        throw new RuntimeException("Converted output file not found.");
    }
}