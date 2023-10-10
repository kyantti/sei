package es.unex.cum.sei.p1y2.cipher;

import es.unex.cum.sei.p1y2.math.Matrix;

public interface Cipher {
    public String encrypt(String text, Matrix key);
    public String encrypt(String text, String key);
    public String decrypt(String text, Matrix key);
    public String decrypt(String text, String key);

}
