package es.unex.cum.sei.p1y2.cipher;

import javax.crypto.SecretKey;

import es.unex.cum.sei.p1y2.math.Matrix;

public interface Cipher {
    public String encrypt(String text, Matrix key);
    public String encrypt(String text, SecretKey key, boolean padding);
    public String decrypt(String text, Matrix key);
    public String decrypt(String text, SecretKey key);
}
