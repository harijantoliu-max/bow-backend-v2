package com.bowbarbershop.shared;

import org.springframework.http.ResponseEntity;

/**
 * Abstraksi akses data tabel (Repository pattern).
 * Controller cukup tahu interface ini, tidak peduli datanya dari
 * Supabase REST, JDBC, atau sumber lain. Mau ganti sumber data nanti?
 * Cukup bikin implementasi baru, controller tidak perlu diubah.
 */
public interface DataRepository {
    ResponseEntity<String> query(String table, String queryString, String userToken);
    ResponseEntity<String> insert(String table, String queryString, String body, String userToken);
    ResponseEntity<String> modify(String table, String queryString, String body, String userToken);
    ResponseEntity<String> remove(String table, String queryString, String userToken);
}
