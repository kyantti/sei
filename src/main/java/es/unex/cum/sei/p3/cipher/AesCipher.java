package es.unex.cum.sei.p3.cipher;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignedObject;
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
    /**
     * Genera una clave secreta basada en la clave proporcionada por el usuario o
     * una clave generada aleatoriamente.
     *
     * @param userKey   La clave proporcionada por el usuario.
     * @param keyLength La longitud de la clave en bits (128, 192 o 256).
     * @return La clave secreta generada.
     */
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
    /**
     * Cifra un texto usando el modo ECB (Electronic Codebook) del algoritmo AES.
     *
     * @param plainText El texto plano a cifrar.
     * @param key       La clave secreta para el cifrado.
     * @param padding   Indica si se debe aplicar relleno PKCS5Padding.
     * @return El texto cifrado como un array de bytes.
     */
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
    /**
     * Realiza una operación XOR a nivel de byte entre dos arrays de bytes.
     *
     * @param a El primer array de bytes.
     * @param b El segundo array de bytes.
     * @return El resultado de la operación XOR.
     */
    private static byte[] xorByteArray(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
    /**
     * Divide una cadena de entrada en bloques del tamaño especificado.
     *
     * @param input     La cadena de entrada a dividir.
     * @param blockSize El tamaño de cada bloque.
     * @return Un array de cadenas que representa los bloques resultantes.
     */
    private static String[] divideStringIntoBlocks(String input, int blockSize) {
        int blockCount = input.length() / blockSize;
        String[] blocks = new String[blockCount];

        for (int i = 0; i < blockCount; i++) {
            int startIndex = i * blockSize;
            int endIndex = Math.min((i + 1) * blockSize, input.length());
            blocks[i] = input.substring(startIndex, endIndex);
        }

        return blocks;
    }
    /**
     * Convierte una cadena de texto en un array de bytes utilizando UTF-8.
     *
     * @param input La cadena de texto a convertir.
     * @return El array de bytes resultante.
     * @throws UnsupportedEncodingException Si ocurre un error al convertir la cadena.
     */
    private static byte[] convertStringToByteArray(String input) throws UnsupportedEncodingException {
        return input.getBytes(StandardCharsets.UTF_8);
    }
    /**
     * Convierte un array de bytes en una representación de cadena hexadecimal.
     *
     * @param byteArray El array de bytes a convertir.
     * @return La representación de cadena hexadecimal.
     */
    private static String byteArrayToString(byte[] byteArray) {
        StringBuilder result = new StringBuilder();
        for (byte b : byteArray) {
            result.append(String.format("%02x:", b));
        }
        return result.substring(0, result.length() - 1);
    }

    /**
     * Convierte un array de bytes en una cadena de texto utilizando UTF-8.
     *
     * @param byteArray El array de bytes a convertir.
     * @return La cadena de texto resultante.
     * @throws UnsupportedEncodingException Si ocurre un error al convertir el array de bytes.
     */
    private static String convertByteArrayToText(byte[] byteArray) throws UnsupportedEncodingException {
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * Concatena dos arrays de bytes.
     *
     * @param a El primer array de bytes.
     * @param b El segundo array de bytes.
     * @return El array de bytes resultante de la concatenación.
     */
    private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Cifra un texto usando el modo CBC (Cipher Block Chaining) del algoritmo AES.
     *
     * @param plainText        El texto plano a cifrar.
     * @param key              La clave secreta para el cifrado.
     * @param iv               El vector de inicialización (IV) para el modo CBC.
     * @param debugModeEnabled Indica si se debe habilitar el modo de depuración.
     * @return El texto cifrado como un array de bytes.
     */
    public byte[] encryptUsingCbc(String plainText, SecretKey key, byte[] iv, boolean debugModeEnabled) {
        try {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String[] blocks = divideStringIntoBlocks(plainText, 16);
            byte[] result = new byte[0];
            byte[] lastBlock = iv;

            for (int i = 0; i < blocks.length; i++) {
                if (debugModeEnabled){
                    System.out.println("Bloque "+ i + " (texto): " + blocks[i]);
                }

                byte[] byteArray = convertStringToByteArray(blocks[i]);

                if (debugModeEnabled){
                    System.out.println("Bloque "+ i + " (hex): " + byteArrayToString(byteArray));
                    System.out.println("VI: " + byteArrayToString(lastBlock));
                }

                byteArray = xorByteArray(byteArray, lastBlock);

                if (debugModeEnabled){
                    System.out.println("Resultado XOR: " + byteArrayToString(byteArray));
                }

                byte[] encryptedBlock = cipher.doFinal(byteArray);
                lastBlock = encryptedBlock;
                result = concatenateByteArrays(result, encryptedBlock);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Descifra un texto usando el modo ECB del algoritmo AES.
     *
     * @param cipherText El texto cifrado como array de bytes.
     * @param key        La clave secreta para el descifrado.
     * @param padding    Indica si se ha aplicado relleno PKCS5Padding.
     * @return El texto descifrado.
     */
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
    /**
     * Descifra un texto usando el modo CBC del algoritmo AES.
     *
     * @param cipherText       El texto cifrado como array de bytes.
     * @param key              La clave secreta para el descifrado.
     * @param iv               El vector de inicialización (IV) para el modo CBC.
     * @param debugModeEnabled Indica si se debe habilitar el modo de depuración.
     * @return El texto descifrado.
     */
    public static String decryptUsingCbc(byte[] cipherText, SecretKey key, byte[] iv, boolean debugModeEnabled) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = new byte[0];
            byte[] lastBlock = iv;

            for (int i = 0; i < cipherText.length / 16; i++) {
                byte[] encryptedBlock = Arrays.copyOfRange(cipherText, i * 16, (i + 1) * 16);

                if (debugModeEnabled){
                    String base64EncryptedBlock = Base64.getEncoder().encodeToString(encryptedBlock);
                    System.out.println("Bloque " + i + " cifrado (texto): "  + base64EncryptedBlock );
                    System.out.println("Bloque " + i + " cifrado (hex): " + byteArrayToString(encryptedBlock));
                }

                byte[] decryptedBlock = cipher.doFinal(encryptedBlock);

                if (debugModeEnabled){
                    System.out.println("Bloque " + i + " descifrado (hex): " + byteArrayToString(decryptedBlock));
                }

                decryptedBlock = xorByteArray(decryptedBlock, lastBlock);

                if (debugModeEnabled){
                    System.out.println("Resultado XOR: " + byteArrayToString(decryptedBlock));
                }

                lastBlock = encryptedBlock; // Use the encrypted block as the IV for the next iteration
                result = concatenateByteArrays(result, decryptedBlock);
            }
            return new String(result, StandardCharsets.US_ASCII);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public void testCbc() {
        try {
            System.out.println("Test AES en modo CBC");
            String userKey = "dostrescua";
            int keyLength = 128;
            byte[] iv = {67, 78, 31, -47, 99, 34, 33, 21, 67, 78, 31, -47, 99, 34, 33, 21};

            SecretKey secretKey = generateKey(userKey, keyLength);
            String hexKey = bytesToHex(secretKey.getEncoded());
            System.out.println("Clave (hex): " + hexKey);

            // Test encryption
            String plainText = "ariasmasajuanestosalebieno no ya veremos";
            System.out.println("Plain text: " + plainText);

            byte[] encryptedText = encryptUsingCbc(plainText, secretKey, iv, true);
            String decryptedText = decryptUsingCbc(encryptedText, secretKey, iv, true);

            System.out.println(decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AesCipher aesCipher = new AesCipher();
        aesCipher.testCbc();
        //aesCipher.testEcb();
    }

}