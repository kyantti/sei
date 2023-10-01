package es.unex.cum.sei.p1y2.util;

import es.unex.cum.sei.p1y2.math.Matrix;

import java.io.*;

public class FileHelper {

    private String getResourceFilePath(String filePath) {
        return "es/unex/cum/sei/p1y2/" + filePath;
    }

    public String readFromFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(getResourceFilePath(filePath));
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public void formatAndSaveToFile(String input, String outputFile) throws IOException {
        StringBuilder formattedContent = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (!hasAccentMark(c)) {
                char normalizedChar = Character.toUpperCase(c);

                if (isInTheAlphabet(normalizedChar)) {
                    formattedContent.append(normalizedChar);
                }
            }
        }

        saveToFile(outputFile, String.valueOf(formattedContent));
    }

    private boolean isInTheAlphabet(char c) {
        return Alphabet.SPANISH.indexOf(c) != -1;
    }

    private boolean hasAccentMark(char c) {
        // Check if the character has an accent mark
        return c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == 'ü';
    }

    public void saveToFile(String fileName, String content) throws IOException {
        String filePath = getResourceFilePath(fileName);
        File file = new File(filePath);

        // Asegurarse de que el directorio exista
        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }



    public Matrix getMatrixFromFile(String fileName) throws IOException {
        int[][] data = new int[3][3];
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(getResourceFilePath(fileName))));

        String line = reader.readLine(); // Read the entire line

        if (line != null) {
            String[] tokens = line.split(" ");

            if (tokens.length == 9) {
                int index = 0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        data[i][j] = Integer.parseInt(tokens[index++]);
                    }
                }
            } else {
                System.err.println("Invalid input format. The file should contain 9 integers separated by spaces.");
            }
        } else {
            System.err.println("Empty file");
        }

        reader.close();
        return new Matrix(data);
    }

    public static void main(String[] args) throws IOException {
        FileHelper fileHelper = new FileHelper();
        String readed = fileHelper.readFromFile("input.txt");
        System.out.println(readed);
        //fileHelper.formatAndSaveToFile("input.txt", "output.txt");
        fileHelper.saveToFile("output.txt", readed);
    }

}
