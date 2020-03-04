package se.kyh;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

public class HashClass {

    private static final String PepperValues = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String hash(String input) {
        SecureRandom random = new SecureRandom();
        int pepperIndex = random.nextInt(PepperValues.length());
        char pepper = PepperValues.charAt(pepperIndex);
        byte[] salt = generateSalt();
        return hash(input + pepper, salt);
    }

    public String hash(String password, byte[] salt) {

        int iterations = 1000;
        char[] passwordChars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, 64 * 8);
        String result = null;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] encodedHash = skf.generateSecret(spec).getEncoded();
            result = bytesToHex(salt) + ":" + bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return result;
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public String bytesToHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[value >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[value & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len / 2; i++) {
            bytes[i] = (byte) ((Character.digit(hexString.charAt(i * 2), 16) << 4)
                    + Character.digit(hexString.charAt(i * 2 + 1), 16));
        }
        return bytes;
    }

    public boolean verify(String password, String salt, String hash) {
        byte[] byteSalt = hexToBytes(salt);

        boolean success = false;
        for (int i = 0; i < PepperValues.length(); i++) {
            String hashedPassword = hash(password + PepperValues.charAt(i), byteSalt).split(":")[1];
            if (hashedPassword.equals(hash)) {
                success = true;
                break;
            }
        }
        return success;
    }
}