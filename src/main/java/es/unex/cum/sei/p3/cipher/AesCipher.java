package es.unex.cum.sei.p3.cipher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class AesCipher {

    private Cipher cipher;

    public AesCipher() {
        cipher = null;
    }

    public SecretKey generateKey(String userKey, int keyLength) {
        SecretKey key = null;
        if (keyLength == 128 || keyLength == 192 || keyLength == 256 || (keyLength % 4 == 0)) {
            try {
                if (userKey.length() >= (keyLength / 8)) {
                    byte[] usuarioClaveByte = userKey.getBytes("UTF-8");
                    MessageDigest sh = MessageDigest.getInstance("SHA-256");
                    usuarioClaveByte = sh.digest(usuarioClaveByte);
                    usuarioClaveByte = Arrays.copyOf(usuarioClaveByte, keyLength / 8); // Determina el tamaño basado en tamClave

                    key = new SecretKeySpec(usuarioClaveByte, "AES");
                } else {
                    KeyGenerator aesKeyGenerator = KeyGenerator.getInstance("AES");
                    aesKeyGenerator.init(keyLength);
                    key = aesKeyGenerator.generateKey();

                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            System.out.println(" -- Error: Tamaño de clave no válido para AES. Debe ser 128, 192 o 256 bits o un numero múltiplo de 4.");
        }
        return key;
    }

    public byte[] encryptUsingEcb(String plainText, SecretKey key, boolean padding) {
        try {
            if (padding) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            } else {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
            }
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] encryptUsingCbc(String plainText, SecretKey key, byte[] iv) {
        try {
            // Convert IV to IvParameterSpec
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Convert plaintext to bytes
            byte[] plaintextBytes = plainText.getBytes();

            // Add PKCS5Padding to plaintext if needed
            int paddingLength = 16 - (plaintextBytes.length % 16);
            byte[] paddedPlaintext = Arrays.copyOf(plaintextBytes, plaintextBytes.length + paddingLength);
            Arrays.fill(paddedPlaintext, plaintextBytes.length, paddedPlaintext.length, (byte) paddingLength);

            // XOR padded plaintext with IV
            byte currentByteOfPaddedPlaintext = 0;
            byte correspondingByteOfIV = 0;

            for (int i = 0; i < paddedPlaintext.length; i++) {
                currentByteOfPaddedPlaintext = paddedPlaintext[i];
                correspondingByteOfIV = iv[i % iv.length];

                // Perform bitwise XOR operation
                paddedPlaintext[i] = (byte) (currentByteOfPaddedPlaintext ^ correspondingByteOfIV);
            }

            // Encrypt the modified plaintext using ECB
            byte[] encryptedData = encryptUsingEcb(new String(paddedPlaintext), key, false);

            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String decryptUsingEcb(byte[] cipherText, SecretKey key, boolean padding) {
        try {
            if (padding) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            } else {
                cipher = Cipher.getInstance("AES/ECB/NoPadding");
            }
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptUsingCbc(byte[] encryptedData, SecretKey key, byte[] iv) {
        try {
            // Convert IV to IvParameterSpec
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Decrypt the encrypted data using ECB
            byte[] decryptedData = decryptUsingEcb(encryptedData, key, false).getBytes();

            // XOR decrypted data with IV to reverse the operation
            byte currentByteOfDecryptedData = 0;
            byte correspondingByteOfIV = 0;

            for (int i = 0; i < decryptedData.length; i++) {
                currentByteOfDecryptedData = decryptedData[i];
                correspondingByteOfIV = iv[i % iv.length];
                // Perform bitwise XOR operation
                decryptedData[i] = (byte) (currentByteOfDecryptedData ^ correspondingByteOfIV);
            }

            // Convert the decrypted bytes to a String
            return new String(decryptedData, StandardCharsets.UTF_8).trim();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Helper method to convert a byte array to a hexadecimal string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
            hexString.append(':');
        }
        // Remove the last ":"
        hexString.setLength(hexString.length() - 1);
        return hexString.toString();
    }

    public void testEcb() {
        try {
            System.out.println("Test AES en modo ECB");
            String userKey = "12345678901234567890123456789012345678901234";
            int keyLength = 192;
            SecretKey secretKey = generateKey(userKey, keyLength);

            System.out.println("Password de usuario: " + userKey);

            String hexKey = bytesToHex(secretKey.getEncoded());
            System.out.println("Clave (hex): " + hexKey);

            // Test encryption
            String plainText = "holacomoestas";
            boolean usePadding = true;
            byte[] encryptedText = encryptUsingEcb(plainText, secretKey, usePadding);

            // Convert the encrypted bytes to a Base64-encoded string for easier display
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);
            System.out.println("Encrypted Text: " + base64EncryptedText);
            System.out.println("Encrypted Text (hex): " + bytesToHex(encryptedText));

            // Test decryption
            String decryptedText = decryptUsingEcb(Base64.getDecoder().decode(base64EncryptedText), secretKey, usePadding);
            System.out.println("Decrypted Text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testCbc(){
        try {
            System.out.println("Test AES en modo CBC");
            String userKey = "dostrescua";
            int keyLength = 128;
            byte[] iv = {67, 78, 31, 123, 3, 99, 34, 33, 21, 67, 78, 31, 123, 3, 99, 34};

            SecretKey secretKey = generateKey(userKey, keyLength);
            String hexKey = bytesToHex(secretKey.getEncoded());
            System.out.println("Clave (hex): " + hexKey);

            // Test encryption
            String plainText = "ariasmasajuanestosalebieno no ya veremos";
            System.out.println("Plain text: " + plainText);

            byte[] encryptedText = encryptUsingCbc(plainText, secretKey, iv);

            // Convert the encrypted bytes to a Base64-encoded string for easier display
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);
            System.out.println("Encrypted Text: " + base64EncryptedText);
            System.out.println("Encrypted Text (hex): " + bytesToHex(encryptedText));

            // Test decryption
            String decryptedText = decryptUsingCbc(Base64.getDecoder().decode(base64EncryptedText), secretKey, iv);
            System.out.println("Decrypted Text: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AesCipher aesCipher = new AesCipher();
        aesCipher.testCbc();
        aesCipher.testEcb();
    }

}