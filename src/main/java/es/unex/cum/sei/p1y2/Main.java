package es.unex.cum.sei.p1y2;

import es.unex.cum.sei.p1y2.cipher.HillCipher;
import es.unex.cum.sei.p1y2.math.Matrix;
import es.unex.cum.sei.p1y2.util.Configuration;
import es.unex.cum.sei.p1y2.util.FileHelper;

import java.io.IOException;

public class Main {
    private static HillCipher hillCipher;
    private static FileHelper fileHelper;

    public static FileHelper getFileHelper() {
        return fileHelper;
    }

    public Main(String[] args){
        hillCipher = new HillCipher();
        fileHelper = new FileHelper();
        Configuration.createConfiguration(args);
    }
    private static Matrix getDefaulKeytMatrix(){
        int [][] data = {{1, 2, 3}, {0, 4, 5}, {1, 0, 6}};
        return new Matrix(data);
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
            ioException.printStackTrace();
        }

    }

    public static void encrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToEncrypt = fileHelper.readFromFile(inputFile);
            Matrix keyMatrix;

            if (keyMatrixFile != null){
                keyMatrix = fileHelper.getMatrixFromFile(keyMatrixFile);
            }
            else{
                keyMatrix = getDefaulKeytMatrix();
            }

            String encryptedText = hillCipher.encrypt(textToEncrypt, keyMatrix);
            fileHelper.saveToFile(encryptedText, outputFile);

            if (debugModeEnabled){
                System.out.println("Texto a cifrar: " + textToEncrypt);
                System.out.println("Matriz clave:\n" + keyMatrix.toString());
                System.out.println("Texto cifrado: " + encryptedText);
            }
        }
        catch (IOException | IllegalArgumentException ioException){
            ioException.printStackTrace();
        }

    }

    public static void decrypt(String inputFile, String keyMatrixFile, String outputFile, boolean debugModeEnabled){
        try{
            String textToDecrypt = fileHelper.readFromFile(inputFile);
            Matrix keyMatrix;
            if (keyMatrixFile != null){
                keyMatrix = fileHelper.getMatrixFromFile(keyMatrixFile);
            }
            else{
                keyMatrix = getDefaulKeytMatrix();
            }
            String decryptedText = hillCipher.decrypt(textToDecrypt, keyMatrix);
            fileHelper.saveToFile(decryptedText, outputFile);

            if (debugModeEnabled){
                System.out.println("Texto a descifrar: " + textToDecrypt);
                System.out.println("Matriz clave:\n" + keyMatrix.modularInverse(27));
                System.out.println("Texto descifrado: " + decryptedText);
            }
        }
        catch (IOException | IllegalArgumentException ioException){
            ioException.printStackTrace();
        }
    }
    public static void debugIfEnabled(String line, boolean debugModeEnabled) {
        if (debugModeEnabled){
            System.out.println(line);
        }
    }

    public static void printUsage() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.""");
    }

    public static void printHelp() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.
                Si no se especifica el fichero no hará nada""");
    }

    public static void main(String[] args) {
        new Main(args);
    }

}
