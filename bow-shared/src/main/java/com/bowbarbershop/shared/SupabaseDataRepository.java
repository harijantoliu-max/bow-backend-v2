package com.bowbarbershop.shared;

import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.Set;

/**
 * Implementasi DataRepository yang memakai Supabase REST API.
 * Semua logika "cara mengambil data" dipindah ke sini, keluar dari controller.
 */
public class SupabaseDataRepository implements DataRepository {

    private static final Set<String> ALLOWED_TABLES = Set.of(
        "branches", "services", "barbers", "bookings", "profiles",
        "memberships", "barber_off_days", "reviews"
    );

    private final SupabaseClient supabase = new SupabaseClient();
    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    @Override
    public ResponseEntity<String> query(String table, String queryString, String userToken) {
        return forward(HttpMethod.GET, table, queryString, null, userToken);
    }

    @Override
    public ResponseEntity<String> insert(String table, String queryString, String body, String userToken) {
        return forward(HttpMethod.POST, table, queryString, body, userToken);
    }

    @Override
    public ResponseEntity<String> modify(String table, String queryString, String body, String userToken) {
        return forward(HttpMethod.PATCH, table, queryString, body, userToken);
    }

    @Override
    public ResponseEntity<String> remove(String table, String queryString, String userToken) {
        return forward(HttpMethod.DELETE, table, queryString, null, userToken);
    }

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
}
