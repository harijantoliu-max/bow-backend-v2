package com.bowbarbershop.shared;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Pusat semua logika koneksi ke Supabase.
 * Sebelumnya logika ini ke-copy di DataController dan StorageController.
 * Sekarang ditulis sekali di sini, dipakai bersama oleh seluruh modul.
 */
public class SupabaseClient {

    private final String supabaseUrl = System.getenv("SUPABASE_URL");
    private final String supabaseKey = System.getenv("SUPABASE_ANON_KEY");
    private final String serviceRoleKey = System.getenv("SUPABASE_SERVICE_ROLE_KEY");

    public String getRestUrl() {
        return supabaseUrl + "/rest/v1";
    }

    public String getStorageUrl() {
        return supabaseUrl + "/storage/v1/object";
    }

    public String getKey() {
        return supabaseKey;
    }

    /** Pakai token user kalau ada, kalau tidak fallback ke anon key. */
    public String resolveAuthToken(String userToken) {
        return (userToken != null && !userToken.isEmpty()) ? userToken : supabaseKey;
    }

    /** Header untuk REST API (tabel). Otomatis pakai service_role key kalau token yang diberikan adalah service role key (bypass RLS, hanya untuk request admin terverifikasi). */
    public HttpHeaders restHeaders(String userToken) {
        HttpHeaders headers = new HttpHeaders();
        String resolvedToken = resolveAuthToken(userToken);
        boolean isServiceRole = serviceRoleKey != null && serviceRoleKey.equals(resolvedToken);
        headers.set("apikey", isServiceRole ? serviceRoleKey : supabaseKey);
        headers.set("Authorization", "Bearer " + resolvedToken);
        headers.set("Content-Type", "application/json");
        headers.set("Prefer", "resolution=merge-duplicates,return=representation");
        return headers;
    }

    /** Header untuk Storage (upload file). Content-Type ikut tipe file. */
    public HttpHeaders storageHeaders(String userToken, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + resolveAuthToken(userToken));
        headers.setContentType(MediaType.parseMediaType(
                contentType != null ? contentType : "application/octet-stream"));
        return headers;
    }

    /** Cek apakah secret yang dikirim cocok dengan ADMIN_API_SECRET di env. */
    public boolean isValidAdminSecret(String secret) {
        String expected = System.getenv("ADMIN_API_SECRET");
        return expected != null && !expected.isEmpty() && expected.equals(secret);
    }

    public String getServiceRoleKey() {
        return serviceRoleKey;
    }

    /** Bersihkan string supaya aman dimasukkan ke pesan JSON. */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "'").replace("\n", " ");
    }
}