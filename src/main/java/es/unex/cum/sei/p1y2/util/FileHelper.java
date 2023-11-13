package es.unex.cum.sei.p1y2.util;

import es.unex.cum.sei.p1y2.math.Matrix;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class FileHelper {
    
    private static FileHelper instance = null;

    private FileHelper() {
        // Private constructor to prevent external instantiation.
    }

    public static FileHelper getInstance() {
        if (instance == null) {
            instance = new FileHelper();
        }
        return instance;
    }

    public static boolean fileExits(String fileName){
        File file = new File(fileName);
        return file.exists();
    }
    /**
     * Comprueba si un archivo está formateado correctamente para su procesamiento.
     *
     * @param fileName El nombre del archivo a verificar.
     * @return `true` si el archivo está formateado correctamente, `false` en caso contrario.
     */
    public static boolean isFileFormatted(String fileName) {
        String content = readFromFile(fileName);

        for (char c : content.toCharArray()) {
            if (Character.isWhitespace(c) || hasAccentMark(c) || !Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Lee el contenido de un archivo y lo devuelve como una cadena.
     *
     * @param fileName El nombre del archivo a leer.
     * @return El contenido del archivo como una cadena.
     */
    public static String readFromFile(String fileName){
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!isFirstLine) {
                    content.append("\n");
                }
                content.append(line);
                isFirstLine = false;
            }
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
        return content.toString();
    }
    /**
     * Formatea y guarda el contenido en un archivo específico.
     *
     * @param content    El contenido a formatear y guardar.
     * @param outputFile El nombre del archivo de salida donde se guardará el contenido formateado.
     */
    public static void formatAndSaveToFile(String content, String outputFile){
        StringBuilder formattedContent = new StringBuilder();

        for (char c : content.toCharArray()) {
            if (!hasAccentMark(c)) {
                char normalizedChar = Character.toUpperCase(c);

                if (isInTheAlphabet(normalizedChar)) {
                    formattedContent.append(normalizedChar);
                }
            }
        }

        saveToFile(String.valueOf(formattedContent), outputFile);
    }
    /**
     * Verifica si un carácter está en el alfabeto español.
     *
     * @param c El carácter a verificar.
     * @return `true` si el carácter está en el alfabeto español, `false` en caso contrario.
     */
    private static boolean isInTheAlphabet(char c) {
        return Alphabet.SPANISH.indexOf(c) != -1;
    }
    /**
     * Verifica si un carácter tiene una tilde o diéresis.
     *
     * @param c El carácter a verificar.
     * @return `true` si el carácter tiene una tilde o diéresis, `false` en caso contrario.
     */
    private static boolean hasAccentMark(char c) {
        return c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == 'ü';
    }
    /**
     * Guarda un contenido en un archivo específico.
     *
     * @param content   El contenido a guardar en el archivo.
     * @param fileName  El nombre del archivo donde se guardará el contenido.
     */
    public static void saveToFile(String content, String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
    /**
     * Obtiene una matriz de una representación de cadena en un archivo.
     *
     * @param fileName El nombre del archivo que contiene la representación de matriz.
     * @return La matriz obtenida del archivo o `null` si el formato es incorrecto.
     */
    public static Matrix getMatrixFromFile(String fileName){
        String fileContent = readFromFile(fileName);
        String[] tokens = fileContent.split("\\s+");

        if (tokens.length != 9) {
            
            System.err.println("Invalid input format. The file should contain 9 integers separated by spaces.");
            return null;
        }

        int[][] data = new int[3][3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = Integer.parseInt(tokens[index++]);
            }
        }

        return new Matrix(data);
    }

    public static SecretKey readSectetKeyFromFile(String file) {
        SecretKey secretKey = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            byte[] keyData = (byte[]) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("AES");
            secretKey = new SecretKeySpec(keyData, "AES");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return secretKey;
    }

}
