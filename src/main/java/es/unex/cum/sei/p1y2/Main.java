package es.unex.cum.sei.p1y2;

import es.unex.cum.sei.p1y2.cipher.HillCipher;
import es.unex.cum.sei.p1y2.math.Matrix;
import es.unex.cum.sei.p1y2.util.Configuration;
import es.unex.cum.sei.p1y2.util.FileHelper;

import java.io.IOException;

public class Main {
    private static HillCipher hillCipher;
    private static FileHelper fileHelper;
    private Configuration configuration;
    public static FileHelper getFileHelper() {
        return fileHelper;
    }

    public Main(String[] args){
        hillCipher = new HillCipher();
        fileHelper = new FileHelper();
        configuration = new Configuration(args);
    }
    public static void formatInput(String inputFile, String outputFile, boolean debugModeEnabled){

        try {
            String unformattedText = fileHelper.readFromFile(inputFile);
            fileHelper.formatAndSaveToFile(unformattedText, outputFile);
            String formattedText = fileHelper.readFromFile(outputFile);

            if (debugModeEnabled){
                System.out.println("Texto sin formatear: " + unformattedText);
                System.out.println("Texto formateado: " + formattedText);
            }
        }
        catch (IOException ioException){
            System.err.println(ioException);
        }

    }

    public static void encrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToEncrypt = fileHelper.readFromFile(inputFile);
            Matrix keyMatrix = fileHelper.getMatrixFromFile(keyMatrixFile);
            String encryptedText = hillCipher.encrypt(textToEncrypt, keyMatrix);
            fileHelper.saveToFile(encryptedText, outputFile);

            if (debugModeEnabled){
                System.out.println("Texto a cifrar: " + textToEncrypt);
                System.out.println("Matriz clave: " + keyMatrix.toString());
                System.out.println("Texto cifrado: " + encryptedText);
            }
        }
        catch (IOException | IllegalArgumentException ioException){
            System.out.println(ioException);
        }

    }

    public static void decrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToDecrypt = fileHelper.readFromFile(inputFile);
            Matrix keyMatrix = fileHelper.getMatrixFromFile(keyMatrixFile);
            String decryptedText = hillCipher.decrypt(textToDecrypt, keyMatrix);
            fileHelper.saveToFile(outputFile, decryptedText);

            if (debugModeEnabled){
                System.out.println("Texto a descifrar: " + textToDecrypt);
                System.out.println("Matriz clave: " + keyMatrix);
                System.out.println("Texto descifrado: " + decryptedText);
            }
        }
        catch (IOException | IllegalArgumentException ioException){
            System.out.println(ioException);
        }
    }

    public static void main(String[] args) {
        Main main = new Main(args);
    }
}
