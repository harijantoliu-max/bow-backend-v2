package com.bowbarbershop.shared;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementasi StorageRepository yang memakai Supabase Storage.
 */
public class SupabaseStorageRepository implements StorageRepository {

    private final SupabaseClient supabase = new SupabaseClient();

    @Override
    public ResponseEntity<String> upload(String bucket, String filename, byte[] data, String contentType, String userToken) {
        try {
            HttpHeaders headers = supabase.storageHeaders(userToken, contentType);
            HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
            RestTemplate restTemplate = new RestTemplate();
            String url = supabase.getStorageUrl() + "/" + bucket + "/" + filename;

            ResponseEntity<String> upstream = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.status(upstream.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(upstream.getBody());

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
