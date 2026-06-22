package com.bowbarbershop.bow_backend;

import com.bowbarbershop.shared.SupabaseClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataController {

    private final SupabaseClient supabase = new SupabaseClient();

    private static final Set<String> ALLOWED_TABLES = Set.of(
        "branches", "services", "barbers", "bookings", "profiles",
        "memberships", "barber_off_days", "reviews"
    );

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    private ResponseEntity<String> forward(HttpMethod method, String table, String queryString, String body, String userToken) {
        if (!ALLOWED_TABLES.contains(table)) {
            return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).body("{\"error\":\"unknown table\"}");
        }
        String url = supabase.getRestUrl() + "/" + table + (queryString != null && !queryString.isEmpty() ? "?" + queryString : "");
        URI uri = URI.create(url);
        HttpEntity<String> entity = new HttpEntity<>(body, supabase.restHeaders(userToken));
        try {
            ResponseEntity<String> upstream = restTemplate.exchange(uri, method, entity, String.class);
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
