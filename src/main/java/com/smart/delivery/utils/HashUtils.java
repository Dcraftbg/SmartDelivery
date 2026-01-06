package com.smart.delivery.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static byte[] sha256(String s) {
        try {
            return MessageDigest.getInstance("SHA256").digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
