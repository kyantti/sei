package es.unex.cum.sei.p3;

import es.unex.cum.sei.p1y2.math.Matrix;
import es.unex.cum.sei.p1y2.util.Configuration;
import es.unex.cum.sei.p1y2.util.FileHelper;
import es.unex.cum.sei.p3.cipher.AesCipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static AesCipher aesCipher;
    public static SecretKey secretKey;
    public static boolean padding;

    public Main(String[] args){
        aesCipher = new AesCipher();
        new Configuration(args);
    }

    private byte[] convertStringArrayToByteArray(String[] strings) {
        byte[] byteArray = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            try {
                // Convert each number string to byte
                byteArray[i] = (byte) Integer.parseInt(strings[i]);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing integer: " + strings[i]);
                e.printStackTrace();
            }
        }
        return byteArray;
    }

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

    public static void encryptUsingEcb(String inputFile, SecretKey secretKey, String outputFile, boolean debugModeEnabled, boolean padding){
        try{
            String plainText = FileHelper.readFromFile(inputFile);
            System.out.println("Texto a cifrar: " + plainText);
            System.out.println("Cifrando mediante el algoritmo AES...");

            byte[]  encryptedText = aesCipher.encryptUsingEcb(plainText, secretKey, padding);
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);

            System.out.println("Encrypted Text: " + base64EncryptedText);
            System.out.println("Encrypted Text (hex): " + bytesToHex(encryptedText));
            FileHelper.saveToFile(base64EncryptedText, outputFile);

            System.out.println("Texto cifrado: " + encryptedText);
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }

    public void encryptUsingCbc(String inputFile, String keyFile, String outputFile, boolean debugModeEnabled, String[] bytes){
        try{
            String textToEncrypt = FileHelper.readFromFile(inputFile);
            SecretKey key;

            if (keyFile != null){
                key = FileHelper.getMatrixFromFile(keyFile);
            }
            else{
                key = getDefaulKeytMatrix();
            }

            String encryptedText =
            FileHelper.saveToFile(encryptedText, outputFile);

            System.out.println("Texto a cifrar: " + textToEncrypt);
            System.out.println("Cifrando mediante el algoritmo AES...");
            if (debugModeEnabled){
                System.out.println("Clave: " + key.toString());
            }

            System.out.println("Texto cifrado: " + encryptedText);
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }

    public static SecretKey generateKey(String userPassword, int keyLength, boolean debugModeEnabled){
        SecretKey key = null;
        if (keyLength == 128 || keyLength == 192 || keyLength == 256 || (keyLength % 4 == 0)) {
            try {
                if (userPassword.length() >= (keyLength / 8)) {

                    if (debugModeEnabled){
                        System.out.println("Generar clave AES de tamaño: " + keyLength);
                        System.out.println("Generar clave AES con password de usuario: " + userPassword );
                    }

                    byte[] usuarioClaveByte = userPassword.getBytes("UTF-8");
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
        if (debugModeEnabled){
            System.out.println("Clave (hex): " + bytesToHex(key.getEncoded()));
        }
        return key;
    }
}
