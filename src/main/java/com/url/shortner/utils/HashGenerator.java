package com.url.shortner.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashGenerator {

    public static String generateHash(String originalUrl) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(hash).substring(0, 8);
    }
}
