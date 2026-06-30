package com.bowbarbershop.bow_backend;

import com.bowbarbershop.shared.DataRepository;
import com.bowbarbershop.shared.SupabaseDataRepository;
import com.bowbarbershop.shared.SupabaseClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataController {

    private final DataRepository repository = new SupabaseDataRepository();
    private final SupabaseClient supabase = new SupabaseClient();

    /**
     * Tentukan token apa yang dipakai untuk request ini:
     * - Kalau header X-Admin-Secret cocok dengan ADMIN_API_SECRET di env -> pakai service_role key (bypass RLS, hanya untuk admin panel terverifikasi)
     * - Kalau ada X-Supabase-Auth (user login biasa) -> pakai itu
     * - Kalau tidak ada keduanya -> fallback ke anon key (otomatis ditangani SupabaseClient)
     */
    private String token(HttpServletRequest request) {
        String adminSecret = request.getHeader("X-Admin-Secret");
        if (supabase.isValidAdminSecret(adminSecret)) {
            return supabase.getServiceRoleKey();
        }
        return request.getHeader("X-Supabase-Auth");
    }

    @GetMapping("/{table}")
    public ResponseEntity<String> get(@PathVariable String table, HttpServletRequest request) {
        return repository.query(table, request.getQueryString(), token(request));
    }

    @PostMapping("/{table}")
    public ResponseEntity<String> post(@PathVariable String table, HttpServletRequest request, @RequestBody(required = false) String body) {
        return repository.insert(table, request.getQueryString(), body, token(request));
    }

    @PatchMapping("/{table}")
    public ResponseEntity<String> patch(@PathVariable String table, HttpServletRequest request, @RequestBody(required = false) String body) {
        return repository.modify(table, request.getQueryString(), body, token(request));
    }

    @DeleteMapping("/{table}")
    public ResponseEntity<String> delete(@PathVariable String table, HttpServletRequest request) {
        return repository.remove(table, request.getQueryString(), token(request));
    }
}