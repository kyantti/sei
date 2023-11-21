package es.unex.cum.sei.p3;

import es.unex.cum.sei.p1y2.util.FileHelper;
import es.unex.cum.sei.p3.cipher.AesCipher;
import es.unex.cum.sei.p3.util.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static AesCipher aesCipher;

    public Main(String[] args){
        aesCipher = new AesCipher();
        new Configuration(args);
    }

    /**
     * Convierte un array de strings en un array de bytes.
     *
     * @param strings El array de strings a convertir.
     * @return Un array de bytes.
     */
    private static byte[] convertStringArrayToByteArray(String[] strings) {
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

    /**
     * Convierte un array de bytes en una cadena hexadecimal.
     *
     * @param bytes El array de bytes a convertir.
     * @return Una cadena hexadecimal.
     */
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

    /**
     * Cifra un archivo utilizando el modo ECB.
     *
     * @param inputFile         El nombre del archivo de entrada.
     * @param secretKey         La clave secreta.
     * @param outputFile        El nombre del archivo de salida.
     * @param debugModeEnabled  Indica si se debe habilitar el modo de depuración.
     * @param padding           Indica si se debe aplicar relleno.
     */
    public static void encryptUsingEcb(String inputFile, SecretKey secretKey, String outputFile, boolean debugModeEnabled, boolean padding){
        try{
            String plainText = FileHelper.readFromFile(inputFile);
            System.out.println("Texto a cifrar: " + plainText);
            System.out.println("Cifrando mediante el algoritmo AES ECB...");

            byte[]  encryptedText = aesCipher.encryptUsingEcb(plainText, secretKey, padding);
            String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);

            System.out.println("Texto cifrado: " + base64EncryptedText);
            System.out.println("Texto cifrado (hex): " + bytesToHex(encryptedText));
            FileHelper.saveToFile(base64EncryptedText, outputFile);
            System.out.println("Texto cifrado guardado en: " + outputFile);

        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }
    /**
     * Descifra un archivo utilizando el modo CBC.
     *
     * @param inputFile         El nombre del archivo de entrada.
     * @param secretKey         La clave secreta.
     * @param outputFile        El nombre del archivo de salida.
     * @param debugModeEnabled  Indica si se debe habilitar el modo de depuración.
     * @param bytes             El array de bytes para el vector de inicialización.
     */
    public static void encryptUsingCbc(String inputFile, SecretKey secretKey, String outputFile, boolean debugModeEnabled, String[] bytes){
       try {
           String plainText = FileHelper.readFromFile(inputFile);
           System.out.println("Texto a cifrar: " + plainText);
           if (plainText.getBytes().length >= 16){
               System.out.println("Cifrando mediante el algoritmo AES CBC...");

               byte[] iv = convertStringArrayToByteArray(bytes);
               byte[] encryptedText = aesCipher.encryptUsingCbc(plainText, secretKey, iv, debugModeEnabled);
               String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);

               System.out.println("Texto cifrado: " + base64EncryptedText);
               System.out.println("Texto cifrado (hex): " + bytesToHex(encryptedText));
               FileHelper.saveToFile(base64EncryptedText, outputFile);
               System.out.println("Texto cifrado guardado en: " + outputFile);
           }
           else {
               System.out.println("El texto a cifrar debe tener al menos 16 bytes");
           }
       }
       catch (IllegalArgumentException illegalArgumentException){
           illegalArgumentException.printStackTrace();
       }
    }
    /**
     * Descifra un archivo utilizando el modo ECB.
     *
     * @param inputFile         El nombre del archivo de entrada.
     * @param key               La clave secreta.
     * @param outputFile        El nombre del archivo de salida.
     * @param debugModeEnabled  Indica si se debe habilitar el modo de depuración.
     * @param padding           Indica si se debe aplicar relleno.
     */
    public static void decryptUsingEcb(String inputFile, SecretKey key, String outputFile, boolean debugModeEnabled, boolean padding){
        try{
            byte[] encryptedText = Base64.getDecoder().decode(FileHelper.readFromFile(inputFile).getBytes());
            String decryptedText = aesCipher.decryptUsingEcb(encryptedText, key, padding);
            System.out.println("Texto descifrado: " + decryptedText);
            FileHelper.saveToFile(decryptedText, outputFile);
            System.out.println("Texto descifrado guardado en: " + outputFile);
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }

    private static String convertByteArrayToText(byte[] byteArray) throws UnsupportedEncodingException {
        // Use the appropriate encoding (UTF-8 in this example)
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * Descifra un archivo utilizando el modo CBC.
     *
     * @param inputFile         El nombre del archivo de entrada.
     * @param secretKey         La clave secreta.
     * @param outputFile        El nombre del archivo de salida.
     * @param debugModeEnabled  Indica si se debe habilitar el modo de depuración.
     * @param bytes             El array de bytes para el vector de inicialización.
     */
    public static void decryptUsingCbc(String inputFile, SecretKey secretKey, String outputFile, boolean debugModeEnabled, String[] bytes){
        try{
            byte[] encryptedText = Base64.getDecoder().decode(FileHelper.readFromFile(inputFile).getBytes());
            if (encryptedText.length % 16 == 0){
                byte[] iv = convertStringArrayToByteArray(bytes);
                String hexKey = bytesToHex(secretKey.getEncoded());

                System.out.println("Clave (hex): " + hexKey);
                String base64EncryptedText = Base64.getEncoder().encodeToString(encryptedText);
                System.out.println("Texto a descifrar: " + base64EncryptedText);
                System.out.println("Descifrando mediante el algoritmo AES CBC...");

                String decryptedText = aesCipher.decryptUsingCbc(encryptedText, secretKey, iv, debugModeEnabled);

                System.out.println("Texto descifrado: " + decryptedText);
                FileHelper.saveToFile(decryptedText, outputFile);
                System.out.println("Texto descifrado guardado en: " + outputFile);
            }
            else {
                System.out.println("El criptograma a descifrar debe tener un tamaño múltiplo de 16 bytes");
            }
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }
    /**
     * Genera una clave secreta utilizando el algoritmo AES.
     *
     * @param userPassword      La contraseña del usuario.
     * @param keyLength         La longitud de la clave.
     * @param debugModeEnabled  Indica si se debe habilitar el modo de depuración.
     * @return La clave secreta generada.
     */
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

    public static void main(String[] args) {
        //aesCipher = new AesCipher();
        //SecretKey secretKey = generateKey("dostrescua", 128, true);
        //String[] iv = {"67","78", "31", "1233", "99", "34", "33", "21", "67", "78", "31", "1233", "99", "34", "33", "21"};
        //encryptUsingCbc("src/main/resources/quijoteparacbc.txt", secretKey, "src/main/resources/hola.txt", true, iv );
        new Main(args);
    }
}
