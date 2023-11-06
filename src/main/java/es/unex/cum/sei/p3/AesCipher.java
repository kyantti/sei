package es.unex.cum.sei.p3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import es.unex.cum.sei.p1y2.math.Matrix;

class AesCipher {

    private Cipher cipher;

    public byte[] encrypt(String plainText, SecretKey key, boolean padding, boolean useCBC, IvParameterSpec iv) {
        cipher = null;

        try {
            if (useCBC) {
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
            
                /* Genera un vector de inicialización aletario
                SecureRandom random = new SecureRandom();
                byte[] ivBytes = new byte[16];
                random.nextBytes(ivBytes);
                IvParameterSpec iv = new IvParameterSpec(ivBytes);*/

                cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            }
            else {
                if (padding){
                    cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                }
                else {
                    cipher = Cipher.getInstance("AES/ECB/NoPadding");
                }

                cipher.init(Cipher.ENCRYPT_MODE, key);
            }

            return cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public String decrypt(String cipherText, SecretKey key) {
        byte[] cipherBuffer = null;
        String encrypted = null;

        cipher.init(Cipher.DECRYPT_MODE, key);
        cipherBuffer = cipher.doFinal(cipherText.getBytes());
        encrypted = Base64.getEncoder().encodeToString(bufferCifrado);
    }

}