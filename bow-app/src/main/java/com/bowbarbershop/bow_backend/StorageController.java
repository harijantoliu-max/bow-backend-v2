package com.bowbarbershop.bow_backend;

import com.bowbarbershop.shared.StorageRepository;
import com.bowbarbershop.shared.SupabaseStorageRepository;
import com.bowbarbershop.shared.SupabaseClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "*")
public class StorageController {

    private final StorageRepository repository = new SupabaseStorageRepository();

    @PostMapping("/{bucket}/{filename}")
    public ResponseEntity<String> upload(@PathVariable String bucket, @PathVariable String filename,
                                         @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(400).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"file is empty or missing\"}");
        }

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"io_error\",\"message\":\"" + SupabaseClient.escape(e.getMessage()) + "\"}");
        }

        String userToken = request.getHeader("X-Supabase-Auth");
        return repository.upload(bucket, filename, data, file.getContentType(), userToken);
    }
}
