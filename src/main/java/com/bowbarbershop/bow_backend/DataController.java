package com.bowbarbershop.bow_backend;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataController {
    private final String supabaseUrl = "https://gupvljmvyjckseuycwfb.supabase.co/rest/v1";
    private final String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd1cHZsam12eWpja3NldXljd2ZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA3MTkyNjksImV4cCI6MjA5NjI5NTI2OX0._ABTAY5gKkMA7NSYQ_aF8nA7l38OeZp3dIWLuG15x9g";
    private static final Set<String> ALLOWED_TABLES = Set.of(
        "branches", "services", "barbers", "bookings", "profiles",
        "memberships", "barber_off_days", "reviews"
    );
    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());
    private HttpHeaders buildHeaders(String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        String authToken = (userToken != null && !userToken.isEmpty()) ? userToken : supabaseKey;
        headers.set("Authorization", "Bearer " + authToken);
        headers.set("Content-Type", "application/json");
        headers.set("Prefer", "resolution=merge-duplicates,return=representation");
        return headers;
    }
    private ResponseEntity<String> forward(HttpMethod method, String table, String queryString, String body, String userToken) {
        if (!ALLOWED_TABLES.contains(table)) {
            return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).body("{\"error\":\"unknown table\"}");
        }
        String url = supabaseUrl + "/" + table + (queryString != null && !queryString.isEmpty() ? "?" + queryString : "");
        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders(userToken));
        try {
            ResponseEntity<String> upstream = restTemplate.exchange(url, method, entity, String.class);
            return ResponseEntity.status(upstream.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(upstream.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getResponseBodyAsString());
        }
    }
    @GetMapping("/{table}")
    public ResponseEntity<String> get(@PathVariable String table, HttpServletRequest request) {
        return forward(HttpMethod.GET, table, request.getQueryString(), null, request.getHeader("X-Supabase-Auth"));
    }
    @PostMapping("/{table}")
    public ResponseEntity<String> post(@PathVariable String table, HttpServletRequest request, @RequestBody(required = false) String body) {
        return forward(HttpMethod.POST, table, request.getQueryString(), body, request.getHeader("X-Supabase-Auth"));
    }
    @PatchMapping("/{table}")
    public ResponseEntity<String> patch(@PathVariable String table, HttpServletRequest request, @RequestBody(required = false) String body) {
        return forward(HttpMethod.PATCH, table, request.getQueryString(), body, request.getHeader("X-Supabase-Auth"));
    }
    @DeleteMapping("/{table}")
    public ResponseEntity<String> delete(@PathVariable String table, HttpServletRequest request) {
        return forward(HttpMethod.DELETE, table, request.getQueryString(), null, request.getHeader("X-Supabase-Auth"));
    }
}