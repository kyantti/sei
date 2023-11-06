package es.unex.cum.sei.p3;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Main
 */
public class Main {

    public static SecretKey generateKey(String userKey, int keyLength) {
        SecretKey key = null;

        if (userKey != null && userKey.length() >= 16) {
            byte[] userByteKey = null;
            try {
                userByteKey = userKey.getBytes(StandardCharsets.UTF_8);
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                userByteKey = sha.digest(userByteKey);
                key = new SecretKeySpec(userByteKey, "AES");
            }
            catch (NoSuchAlgorithmException exception) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
                return null;
            }
        }
        else {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(keyLength, new SecureRandom());
                key = keyGenerator.generateKey();
            }
            catch (NoSuchAlgorithmException exception) {
                exception.printStackTrace();
            }
        }

        return key;
    }

    public static void main(String[] args) {
        try {
            // Ejemplo de generación de clave aleatoria de 256 bits
            SecretKey key = generateKey("h", 128);
            if (key != null) {
                System.out.println("Clave generada: " + Arrays.toString(key.getEncoded()));
            } else {
                System.out.println("No se pudo generar la clave.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}