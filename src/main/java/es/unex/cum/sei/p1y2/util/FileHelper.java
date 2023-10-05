package es.unex.cum.sei.p1y2.util;

import es.unex.cum.sei.p1y2.math.Matrix;

import java.io.*;

public class FileHelper {

    public String readFromFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;  // Agregamos una bandera para controlar la primera línea
            while ((line = reader.readLine()) != null) {
                if (!isFirstLine) {
                    content.append("\n");  // Agrega un salto de línea solo si no es la primera línea
                }
                content.append(line);
                isFirstLine = false;  // Después de la primera línea, cambiamos la bandera
            }
        }
        return content.toString();
    }


    public void formatAndSaveToFile(String content, String outputFile) throws IOException {
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

    private boolean isInTheAlphabet(char c) {
        return Alphabet.SPANISH.indexOf(c) != -1;
    }

    private boolean hasAccentMark(char c) {
        // Check if the character has an accent mark
        return c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == 'ü';
    }

    public void saveToFile(String content, String fileName) throws IOException {
        File file = new File(fileName);
        //file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }



    public Matrix getMatrixFromFile(String fileName) throws IOException {
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

}
