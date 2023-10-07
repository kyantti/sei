package es.unex.cum.sei.p1y2.cipher;

import es.unex.cum.sei.p1y2.util.Alphabet;
import es.unex.cum.sei.p1y2.math.Matrix;

import java.util.Random;

public class HillCipher implements  Cipher{

    /**
     * Divide el mensaje en tripletes y los completa con caracteres aleatorios si es necesario.
     *
     * @param message El mensaje a dividir en tripletes.
     * @return Una cadena con los tripletes formateados.
     */
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
    /**
     * Obtiene una matriz a partir de una cadena de texto.
     *
     * @param text El texto para el que se desea crear una matriz.
     * @return La matriz correspondiente al texto.
     */
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
    /**
     * Cifra un mensaje utilizando una clave dada.
     *
     * @param message El mensaje a cifrar.
     * @param key     La clave para el cifrado.
     * @return El mensaje cifrado.
     * @throws IllegalArgumentException Si la operación no se puede realizar debido a la clave incorrecta.
     */
    public String encrypt(String message, Matrix key) throws IllegalArgumentException {
        return getString(message, key);

    }
    /**
     * Realiza el cifrado o descifrado de una cadena utilizando una matriz dada.
     *
     * @param message El mensaje a cifrar o descifrar.
     * @param a       La matriz para la operación.
     * @return El mensaje cifrado o descifrado.
     * @throws IllegalArgumentException Si la operación no se puede realizar debido a la matriz incorrecta.
     */
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
    /**
     * Descifra un mensaje utilizando una clave dada.
     *
     * @param message El mensaje a descifrar.
     * @param key     La clave para la descifrar.
     * @return El mensaje descifrado.
     * @throws IllegalArgumentException Si la operación no se puede realizar debido a la clave incorrecta.
     */
    public String decrypt(String message, Matrix key) throws IllegalArgumentException {
        Matrix a = key.modularInverse(27);
        return getString(message, a);
    }

}
