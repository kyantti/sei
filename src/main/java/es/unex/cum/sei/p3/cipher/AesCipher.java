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
                    KeyGenerator generadorAES = KeyGenerator.getInstance("AES");
                    generadorAES.init(keyLength);
                    key = generadorAES.generateKey();

                }
            } catch (Exception e) {
                System.out.println(e.toString());
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
    public byte[] encryptUsingCbc(String plainText, SecretKey key, boolean padding, byte[] iv) {
        try {
            // Check if the IV is provided and has the correct length
            if (iv == null || iv.length != 16) {
                throw new IllegalArgumentException("IV must be 16 bytes long");
            }

            // Convert plaintext to bytes
            byte[] plaintextBytes = plainText.getBytes(StandardCharsets.UTF_8);

            // Apply padding if needed
            if (padding) {
                // Implement PKCS5 padding
                int paddingLength = 16 - (plaintextBytes.length % 16);
                byte[] paddedBytes = new byte[plaintextBytes.length + paddingLength];
                System.arraycopy(plaintextBytes, 0, paddedBytes, 0, plaintextBytes.length);
                Arrays.fill(paddedBytes, plaintextBytes.length, paddedBytes.length, (byte) paddingLength);
                plaintextBytes = paddedBytes;
            }

            // Initialize the result array with the correct size
            byte[] result = new byte[plaintextBytes.length];

            // Initialize the previous ciphertext block with the IV
            byte[] prevCiphertextBlock = iv;

            // Iterate over blocks of plaintext
            for (int i = 0; i < plaintextBytes.length; i += 16) {
                // XOR the plaintext block with the previous ciphertext block
                for (int j = 0; j < 16; j++) {
                    plaintextBytes[i + j] ^= prevCiphertextBlock[j];
                }

                // Encrypt the XORed block using ECB
                byte[] encryptedBlock = encryptUsingEcb(Arrays.toString(Arrays.copyOfRange(plaintextBytes, i, i + 16)), key, true);

                // Copy the encrypted block to the correct position in the result array
                System.arraycopy(encryptedBlock, 0, result, i, 16);

                // Update the previous ciphertext block
                prevCiphertextBlock = encryptedBlock;
            }

            return result;
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
    public String decryptUsingCbc(byte[] cipherText, SecretKey key, boolean padding, byte[] iv) {
        try {
            // Check if the IV is provided and has the correct length
            if (iv == null || iv.length != 16) {
                throw new IllegalArgumentException("IV must be 16 bytes long");
            }

            // Initialize the result array with the correct size
            byte[] result = new byte[cipherText.length];

            // Initialize the previous ciphertext block with the IV
            byte[] prevCiphertextBlock = iv;

            // Iterate over blocks of ciphertext
            for (int i = 0; i < cipherText.length; i += 16) {
                // Extract the current ciphertext block
                byte[] ciphertextBlock = Arrays.copyOfRange(cipherText, i, i + 16);

                // Decrypt the ciphertext block using ECB
                byte[] decryptedBlock = decryptUsingEcb(ciphertextBlock, key, true).getBytes();

                // XOR the decrypted block with the previous ciphertext block
                for (int j = 0; j < 16; j++) {
                    decryptedBlock[j] ^= prevCiphertextBlock[j];
                }

                // Copy the XORed block to the correct position in the result array
                System.arraycopy(decryptedBlock, 0, result, i, 16);

                // Update the previous ciphertext block
                prevCiphertextBlock = ciphertextBlock;
            }

            // Remove padding if needed
            if (padding) {
                int paddingLength = result[result.length - 1];
                result = Arrays.copyOfRange(result, 0, result.length - paddingLength);
            }

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void testEcb() {
        try {
            String userKey = "12345678901234567890123456789012345678901234";
            int keyLength = 192;
            SecretKey secretKey = generateKey(userKey, keyLength);
            System.out.println("Clave: " + secretKey.getEncoded().toString());

            // Test encryption
            String plainText = "holacomoestas";
            boolean usePadding = true;
            byte[] encryptedText = encryptUsingEcb(plainText, secretKey, usePadding);

            // Convert the encrypted bytes to a Base64-encoded string for easier display
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);
            System.out.println("Encrypted Text: " + base64EncryptedText);

            // Test decryption
            String decryptedText = decryptUsingEcb(Base64.getDecoder().decode(base64EncryptedText), secretKey, usePadding);
            System.out.println("Decrypted Text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCbc(){
        try {
            String userKey = "dostrescua";
            int keyLength = 128;
            byte[] iv = {67, 78, 31, 123, 3, 99, 34, 33, 21, 67, 78, 31, 123, 3, 99, 34};

            SecretKey secretKey = generateKey(userKey, keyLength);
            System.out.println("Clave: " + Arrays.toString(secretKey.getEncoded()));

            // Test encryption
            String plainText = "ariasmasajuanestosalebieno no ya veremos";
            System.out.println("Plain text: " + plainText);
            boolean usePadding = true;

            byte[] encryptedText = encryptUsingCbc(plainText, secretKey, usePadding, iv);

            // Convert the encrypted bytes to a Base64-encoded string for easier display
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);
            System.out.println("Encrypted Text: " + base64EncryptedText);

            // Test decryption
            String decryptedText = decryptUsingCbc(Base64.getDecoder().decode(base64EncryptedText), secretKey, usePadding, iv);
            System.out.println("Decrypted Text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AesCipher aesCipher = new AesCipher();
        aesCipher.testCbc();
    }

}