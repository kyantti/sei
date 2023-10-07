package es.unex.cum.sei.p1y2;

import es.unex.cum.sei.p1y2.cipher.HillCipher;
import es.unex.cum.sei.p1y2.math.Matrix;
import es.unex.cum.sei.p1y2.util.Configuration;
import es.unex.cum.sei.p1y2.util.FileHelper;

import java.io.IOException;

public class Main {
    private static HillCipher hillCipher;
    /**
     * Constructor de la clase Main que inicializa una instancia de HillCipher y Configuration.
     *
     * @param args Los argumentos de línea de comandos.
     */
    public Main(String[] args){
        hillCipher = new HillCipher();
        new Configuration(args);
    }
    /**
     * Obtiene una matriz clave predeterminada.
     *
     * @return La matriz clave predeterminada.
     */
    public static Matrix getDefaulKeytMatrix(){
        int [][] data = {{1, 2, 3}, {0, 4, 5}, {1, 0, 6}};
        return new Matrix(data);
    }
    /**
     * Formatea la entrada y guarda el resultado en un archivo de salida.
     *
     * @param inputFile        El archivo de entrada a formatear.
     * @param outputFile       El archivo de salida donde se guarda el texto formateado.
     * @param debugModeEnabled Indica si el modo de depuración está habilitado.
     */
    public static void formatInput(String inputFile, String outputFile, boolean debugModeEnabled){
        String unformattedText = FileHelper.readFromFile(inputFile);
        FileHelper.formatAndSaveToFile(unformattedText, outputFile);
        String formattedText = FileHelper.readFromFile(outputFile);

        if (debugModeEnabled){
            System.out.println("Texto sin formatear: " + unformattedText);
            System.out.println("Texto formateado: " + formattedText);
        }

    }
    /**
     * Cifra un archivo de entrada utilizando la matriz clave especificada o una matriz clave predeterminada.
     *
     * @param inputFile        El archivo de entrada a cifrar.
     * @param keyMatrixFile    El archivo que contiene la matriz clave (opcional).
     * @param outputFile       El archivo de salida donde se guarda el texto cifrado.
     * @param debugModeEnabled Indica si el modo de depuración está habilitado.
     */
    public static void encrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToEncrypt = FileHelper.readFromFile(inputFile);
            Matrix keyMatrix;

            if (keyMatrixFile != null){
                keyMatrix = FileHelper.getMatrixFromFile(keyMatrixFile);
            }
            else{
                keyMatrix = getDefaulKeytMatrix();
            }

            String encryptedText = hillCipher.encrypt(textToEncrypt, keyMatrix);
            System.out.println("antes de guardar");
            FileHelper.saveToFile(encryptedText, outputFile);
            System.out.println("guardado");

            if (debugModeEnabled){
                System.out.println("Texto a cifrar: " + textToEncrypt);
                System.out.println("Matriz clave:\n" + keyMatrix.toString());
                System.out.println("Texto cifrado: " + encryptedText);
            }
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }

    }
    /**
     * Descifra un archivo de entrada utilizando la matriz clave especificada o una matriz clave predeterminada.
     *
     * @param inputFile        El archivo de entrada a descifrar.
     * @param keyMatrixFile    El archivo que contiene la matriz clave (opcional).
     * @param outputFile       El archivo de salida donde se guarda el texto descifrado.
     * @param debugModeEnabled Indica si el modo de depuración está habilitado.
     */
    public static void decrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToDecrypt = FileHelper.readFromFile(inputFile);
            Matrix keyMatrix;
            if (keyMatrixFile != null){
                keyMatrix = FileHelper.getMatrixFromFile(keyMatrixFile);
            }
            else{
                keyMatrix = getDefaulKeytMatrix();
            }

            String decryptedText = hillCipher.decrypt(textToDecrypt, keyMatrix);
            FileHelper.saveToFile(decryptedText, outputFile);

            if (debugModeEnabled){
                System.out.println("Texto a descifrar: " + textToDecrypt);
                System.out.println("Matriz clave:\n" + keyMatrix.modularInverse(27));
                System.out.println("Texto descifrado: " + decryptedText);
            }
        }
        catch (IllegalArgumentException illegalArgumentException){
            illegalArgumentException.printStackTrace();
        }
    }
    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Los argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        new Main(args);
    }

}
