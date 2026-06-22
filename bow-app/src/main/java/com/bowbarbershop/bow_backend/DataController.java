package com.bowbarbershop.bow_backend;

import com.bowbarbershop.shared.DataRepository;
import com.bowbarbershop.shared.SupabaseDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataController {

    private final DataRepository repository = new SupabaseDataRepository();

    private String token(HttpServletRequest request) {
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
