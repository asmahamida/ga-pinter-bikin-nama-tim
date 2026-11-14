/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
        private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom RANDOM =
            new SecureRandom();

    // Mengubah password menjadi hash
    public static String hashPassword(String password) {

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hash = generateHash(password, salt);

        return ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    // Memeriksa password yang dimasukkan pengguna
    public static boolean verifyPassword(
            String password,
            String storedHash) {

        try {
            String[] parts = storedHash.split(":");

            int iterations = Integer.parseInt(parts[0]);

            byte[] salt = Base64.getDecoder()
                    .decode(parts[1]);

            byte[] expectedHash = Base64.getDecoder()
                    .decode(parts[2]);

            byte[] actualHash = generateHash(
                    password, salt, iterations
            );

            return MessageDigest.isEqual(
                    expectedHash, actualHash
            );

        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] generateHash(
            String password, byte[] salt) {

        return generateHash(password, salt, ITERATIONS);
    }

    private static byte[] generateHash(
            String password, byte[] salt, int iterations) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Gagal memproses password.", e
            );

        } finally {
            spec.clearPassword();
        }
    }
}
