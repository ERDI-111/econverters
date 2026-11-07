package com.econverters.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import java.util.UUID;

@Service
public class ConversionService {

    public File convertMedia(String videoUrl, String format) throws Exception {
        // Example using a public or RapidAPI endpoint provider
        // You can plug in any reliable third-party video download API here
        String apiUrl = "https://your-chosen-rapid-api-endpoint.com/download?url=" + videoUrl + "&format=" + format;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "YOUR_API_KEY_HERE"); // Optional if using RapidAPI
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Call external API to get the direct media file URL
        ResponseEntity response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        
        // Extract direct file download link provided by the API
        String downloadLink = (String) response.getBody().get("download_url");

        // Download the file locally to temp directory to stream to user
        String outputDir = System.getProperty("java.io.tmpdir");
        File downloadedFile = new File(outputDir, UUID.randomUUID().toString() + "." + format);
        FileUtils.copyURLToFile(new URL(downloadLink), downloadedFile);

        return downloadedFile;
    }
}