package com.bowbarbershop.shared;

import org.springframework.http.ResponseEntity;

/**
 * Abstraksi akses penyimpanan file (Repository pattern).
 */
public interface StorageRepository {
    ResponseEntity<String> upload(String bucket, String filename, byte[] data, String contentType, String userToken);
}
