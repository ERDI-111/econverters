package com.econverters.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ConverterController {

    @GetMapping("/convert")
    public ResponseEntity<Object> convertMedia(
            @RequestParam("url") String rawUrl,
            @RequestParam("format") String format,
            @RequestParam(value = "quality", defaultValue = "best") String quality) {

        // Clean YouTube playlist parameters
        String url = rawUrl;
        if (url.contains("&list=")) {
            url = url.substring(0, url.indexOf("&list="));
        } else if (url.contains("?list=")) {
            url = url.substring(0, url.indexOf("?list="));
        }

        String fileId = UUID.randomUUID().toString();
        // Use %(title)s so yt-dlp automatically saves it with the exact original video/audio title
        String outputTemplate = System.getProperty("java.io.tmpdir") + "/" + fileId + "_%(title)s.%(ext)s";
        
        ProcessBuilder processBuilder;

        if ("mp3".equalsIgnoreCase(format)) {
            // Fast audio extraction
            processBuilder = new ProcessBuilder(
                "yt-dlp", "--no-playlist", "-x", "--audio-format", "mp3", 
                "--audio-quality", "5", "-o", outputTemplate, url
            );
        } else {
            // Fast MP4 format selection based on user quality choice
            String formatSelector;
            if ("1080".equalsIgnoreCase(quality)) {
                formatSelector = "bv*[height<=1080][ext=mp4]+ba[ext=m4a]/b[height<=1080]";
            } else if ("720".equalsIgnoreCase(quality)) {
                formatSelector = "bv*[height<=720][ext=mp4]+ba[ext=m4a]/b[height<=720]";
            } else {
                // 'fast' or 'best' pre-merged format for maximum speed without heavy transcoding lag
                formatSelector = "best[ext=mp4]/best";
            }

            processBuilder = new ProcessBuilder(
                "yt-dlp", "--no-playlist", "-f", formatSelector, "--merge-output-format", "mp4", "-o", outputTemplate, url
            );
        }

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("yt-dlp error: " + errorOutput);
            }

            // Find the generated file in temp folder matching our unique ID
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File[] matchedFiles = tempDir.listFiles((dir, name) -> name.startsWith(fileId));

            if (matchedFiles == null || matchedFiles.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Converted file not found.");
            }

            File downloadedFile = matchedFiles[0];
            InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadedFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Clean filename for HTTP header transmission
            String safeFilename = downloadedFile.getName().substring(fileId.length() + 1);
            headers.setContentDispositionFormData("attachment", safeFilename);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(downloadedFile.length())
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}