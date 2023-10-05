package es.unex.cum.sei.p1y2.cipher;

import es.unex.cum.sei.p1y2.util.Alphabet;
import es.unex.cum.sei.p1y2.math.Matrix;

import java.util.Random;

public class HillCipher implements  Cipher{

    private String divideIntoTriplets(String message) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < message.length(); i += 3) {
            StringBuilder subString = new StringBuilder(message.substring(i, Math.min(i + 3, message.length())));

            while (subString.length() < 3) {
                char randomChar = Alphabet.SPANISH.charAt(random.nextInt(Alphabet.SPANISH.length()));
                subString.append(randomChar);
            }

            result.append(subString.toString().toUpperCase()).append(" ");
        }

        return result.toString().trim();
    }

    private Matrix getMatrix(String text) {
        String[] triplets = divideIntoTriplets(text).split(" ");
        int[][] data = new int[3][triplets.length];

        for (int i = 0; i < triplets.length; i++) {
            String triplet = triplets[i];
            for (int j = 0; j < 3; j++) {
                char charInTheTriplet = triplet.charAt(j);
                int indexInTheAlphabet = Alphabet.SPANISH.indexOf(charInTheTriplet);
                data[j][i] = indexInTheAlphabet;
            }
        }

        return new Matrix(data);
    }

    public String encrypt(String message, Matrix key) throws IllegalArgumentException {
        return getString(message, key);

    }

    private String getString(String message, Matrix a) throws IllegalArgumentException {
        Matrix b = getMatrix(message);

        Matrix c = a.modularMultiply(b, 27);
        StringBuilder encrypted = new StringBuilder();

        for (int col = 0; col < c.getNumCols(); col++) {
            for (int row = 0; row < c.getNumRows(); row++) {
                int element = c.getElement(row, col);
                char encryptedChar = Alphabet.SPANISH.charAt(element);
                encrypted.append(encryptedChar);
            }
        }

        return encrypted.toString();
    }

    public String decrypt(String message, Matrix key) throws IllegalArgumentException {
        Matrix a = key.modularInverse(27);
        return getString(message, a);
    }

}
