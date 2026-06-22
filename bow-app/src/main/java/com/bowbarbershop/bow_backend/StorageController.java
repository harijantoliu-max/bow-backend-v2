package com.bowbarbershop.bow_backend;

import com.bowbarbershop.shared.SupabaseClient;
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

    private final SupabaseClient supabase = new SupabaseClient();

    @PostMapping("/{bucket}/{filename}")
    public ResponseEntity<String> upload(@PathVariable String bucket, @PathVariable String filename, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"file is empty or missing\"}");
            }

            String userToken = request.getHeader("X-Supabase-Auth");
            HttpHeaders headers = supabase.storageHeaders(userToken, file.getContentType());

            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
            RestTemplate restTemplate = new RestTemplate();
            String url = supabase.getStorageUrl() + "/" + bucket + "/" + filename;

            ResponseEntity<String> upstream = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.status(upstream.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(upstream.getBody());

        } catch (IOException e) {
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"io_error\",\"message\":\"" + SupabaseClient.escape(e.getMessage()) + "\"}");

        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());

        } catch (Exception e) {
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"unexpected_error\",\"exception\":\"" + e.getClass().getSimpleName()
                            + "\",\"message\":\"" + SupabaseClient.escape(e.getMessage()) + "\"}");
        }
    }
}
