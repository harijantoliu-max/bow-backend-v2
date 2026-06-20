package com.bowbarbershop.bow_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    private final String supabaseUrl = System.getenv("SUPABASE_URL") + "/storage/v1/object";
    private final String supabaseKey = System.getenv("SUPABASE_ANON_KEY");

    @PostMapping("/{bucket}/{filename}")
    public ResponseEntity<String> upload(@PathVariable String bucket, @PathVariable String filename, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"file is empty or missing\"}");
            }

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
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"io_error\",\"message\":\"" + escape(e.getMessage()) + "\"}");

        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());

        } catch (Exception e) {
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"unexpected_error\",\"exception\":\"" + e.getClass().getSimpleName()
                            + "\",\"message\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "'").replace("\n", " ");
    }
}