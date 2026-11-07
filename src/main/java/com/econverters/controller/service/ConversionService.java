package com.econverters.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class ConversionService {

    public File convertMedia(String videoUrl, String format) throws Exception {
        String apiUrl = "https://your-chosen-rapid-api-endpoint.com/download?url=" + videoUrl + "&format=" + format;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "YOUR_API_KEY_HERE");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        
        if (response.getBody() == null) {
            throw new RuntimeException("Received empty response from external API.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();

        if (!responseBody.containsKey("download_url")) {
            throw new RuntimeException("Failed to retrieve download URL from external API.");
        }

        String downloadLink = (String) responseBody.get("download_url");

        String outputDir = System.getProperty("java.io.tmpdir");
        File downloadedFile = new File(outputDir, UUID.randomUUID().toString() + "." + format);

        try (InputStream in = URI.create(downloadLink).toURL().openStream()) {
            Files.copy(in, downloadedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return downloadedFile;
    }
}