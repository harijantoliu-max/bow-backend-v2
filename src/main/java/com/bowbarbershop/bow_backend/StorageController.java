package com.bowbarbershop.bow_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    private final String supabaseUrl = "https://gupvljmvyjckseuycwfb.supabase.co/storage/v1/object";
    private final String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd1cHZsam12eWpja3NldXljd2ZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA3MTkyNjksImV4cCI6MjA5NjI5NTI2OX0._ABTAY5gKkMA7NSYQ_aF8nA7l38OeZp3dIWLuG15x9g";

    @PostMapping("/{bucket}/{filename}")
    public ResponseEntity<String> upload(@PathVariable String bucket, @PathVariable String filename, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String userToken = request.getHeader("X-Supabase-Auth");
            String authToken = (userToken != null && !userToken.isEmpty()) ? userToken : supabaseKey;
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", supabaseKey);
            headers.set("Authorization", "Bearer " + authToken);
            String ct = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            headers.setContentType(MediaType.parseMediaType(ct));

            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
            RestTemplate restTemplate = new RestTemplate();
            String url = supabaseUrl + "/" + bucket + "/" + filename;
            ResponseEntity<String> upstream = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.status(upstream.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(upstream.getBody());
        } catch (IOException e) {
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).contentType(MediaType.APPLICATION_JSON).body(e.getResponseBodyAsString());
        }
    }
}