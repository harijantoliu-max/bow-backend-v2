package com.bowbarbershop.bow_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BranchController {

    private final String supabaseUrl = "https://gupvljmvyjckseuycwfb.supabase.co/rest/v1";
    private final String supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imd1cHZsam12eWpja3NldXljd2ZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA3MTkyNjksImV4cCI6MjA5NjI5NTI2OX0._ABTAY5gKkMA7NSYQ_aF8nA7l38OeZp3dIWLuG15x9g";

    private HttpEntity<String> buildEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> get(String path) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> upstream = restTemplate.exchange(supabaseUrl + path, HttpMethod.GET, buildEntity(), String.class);
        return ResponseEntity.status(upstream.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(upstream.getBody());
    }

    @GetMapping("/branches")
    public ResponseEntity<String> getBranches() {
        return get("/branches?select=*");
    }

    @GetMapping("/services")
    public ResponseEntity<String> getServices() {
        return get("/services?select=*");
    }

    @GetMapping("/barbers")
    public ResponseEntity<String> getBarbers() {
        return get("/barbers?select=*");
    }

    @GetMapping("/bookings")
    public ResponseEntity<String> getBookings() {
        return get("/bookings?select=*");
    }

    @GetMapping("/profiles")
    public ResponseEntity<String> getProfiles() {
        return get("/profiles?select=*");
    }
}