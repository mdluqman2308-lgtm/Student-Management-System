package com.studentmanagement.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Salted SHA-256 password hashing using only Java's standard library.
 * The formula is SHA-256(salt + password), written as lowercase hex, which is
 * exactly what MySQL's SHA2(CONCAT(salt, password), 256) produces.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available on this JVM", e);
        }
    }

    /** Compares hashes in constant time. */
    public static boolean matches(String password, String salt, String expectedHash) {
        byte[] actual = hash(salt, password).getBytes(StandardCharsets.UTF_8);
        byte[] expected = expectedHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(actual, expected);
    }
}
