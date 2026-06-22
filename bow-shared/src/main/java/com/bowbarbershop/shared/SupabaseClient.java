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

    /** Header untuk REST API (tabel). */
    public HttpHeaders restHeaders(String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + resolveAuthToken(userToken));
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

    /** Bersihkan string supaya aman dimasukkan ke pesan JSON. */
    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "'").replace("\n", " ");
    }
}
