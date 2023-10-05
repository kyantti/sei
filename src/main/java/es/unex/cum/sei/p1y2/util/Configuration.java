package es.unex.cum.sei.p1y2.util;

import es.unex.cum.sei.p1y2.Main;

import java.io.File;
import java.io.IOException;

public class Configuration {
    private static final String FLAG_CODIFICA = "codifica";
    private static final String FLAG_TRAZA = "traza";
    private String inputFile;
    private String outputFile;
    private String keyFile;
    private boolean encryptionModeEnabled;
    private boolean decryptionModeEnabled;
    private boolean debugModeEnabled;

    private Configuration() {
        // This constructor is empty to prevent instantiation
    }

    public static void createConfiguration(String[] args) {
        Configuration config = new Configuration();
        config.parseArguments(args);
    }

    public void enableEncryptionMode(){
        decryptionModeEnabled = false;
        encryptionModeEnabled = true;
    }

    public void enableDecryptionMode(){
        encryptionModeEnabled = false;
        decryptionModeEnabled = true;
    }

    public void enableDebugMode(){
        debugModeEnabled = true;
    }

    public void disableDebugMode(){
        debugModeEnabled = false;
    }

    private void parseArguments(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();

            switch (arg) {
                case "-f" -> {
                    if (i + 1 < args.length) {
                        parseConfigFile(args[i + 1]);
                        i++;
                    }
                    else {
                        System.err.println("Falta el argumento del fichero de configuracion");
                        printUsage();
                        System.exit(1);
                    }
                }
                case "-h" -> {
                    printHelp();
                    System.exit(0);
                }
                default -> {
                    System.err.println("Argumento invalido: " + arg);
                    printUsage();
                    System.exit(1);
                }
            }
        }
    }

    private void parseConfigFile(String configFile) {
        Main.debugIfEnabled("Leyendo y ejecutando el fichero de configuración: " + configFile, debugModeEnabled);

        try {
            String configContents = Main.getFileHelper().readFromFile(configFile);

            String[] lines = configContents.split("\\n");

            for (String line : lines) {

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    // Skip empty lines and comments
                    continue;
                }
                if (line.startsWith("@")) {
                    parseFlag(line);
                }
                else if (line.startsWith("&")) {
                    parseCommand(line);
                }
                else {
                    System.err.println("Linea invalida en el archivo de configuracion " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el arhivo de configuracion: " + e.getMessage());
            System.exit(1);
        }

    }


    private void parseFlag(String line) {

        Main.debugIfEnabled("Procesando la linea: " + line, debugModeEnabled);

        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Formato de bandera invalido: " + line);
        }

        String flagName = parts[1].toLowerCase();
        String flagValue = parts[2].toUpperCase();

        switch (flagName) {
            case FLAG_CODIFICA -> handleCipheringFlag(flagValue);
            case FLAG_TRAZA -> handleDebugFlag(flagValue);
            default -> throw new IllegalArgumentException("Bandera invalida: " + flagName);
        }
    }

    private void handleCipheringFlag(String flagValue) {
        if (flagValue.equals("ON")) {
            enableEncryptionMode();
        } else if (flagValue.equals("OFF")) {
            enableDecryptionMode();
        } else {
            throw new IllegalArgumentException("Invalid value for 'codifica' flag: " + flagValue);
        }
    }

    private void handleDebugFlag(String flagValue) {
        if (flagValue.equals("ON")) {
            enableDebugMode();
        }
        else if (flagValue.equals("OFF")) {
            disableDebugMode();
        }
        else {
            throw new IllegalArgumentException("Valor incorrecto para la bandera 'traza':" + flagValue);
        }
    }

    private void parseCommand(String line) {

        Main.debugIfEnabled("Procesando la linea: " + line, debugModeEnabled);

        String[] parts = line.split("\\s+");

        if (parts.length < 2) {
            System.err.println("Formato del comando invalido: " + line);
            return;
        }

        String commandName = parts[1].toLowerCase();

        if (parts.length == 2) {
            handleSingleWordCommand(commandName);
        }
        else if (parts.length == 3) {
            String fileName = parts[2];
            handleTwoWordCommand(commandName, fileName);
        }
        else {
            System.err.println("Invalid command format: " + line);
        }
    }

    private void handleSingleWordCommand(String commandName) {
        switch (commandName) {
            case "formateaentrada" -> Main.formatInput(inputFile, outputFile, debugModeEnabled);
            case "hill" -> {
                if (encryptionModeEnabled) {
                    Main.encrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                }
                else if (decryptionModeEnabled) {
                    Main.decrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                }
            }
            default -> System.err.println("Comando invalido: " + commandName);
        }
    }

    private void handleTwoWordCommand(String commandName, String fileName) {
        switch (commandName) {
            case "ficheroentrada" -> inputFile = fileName;
            case "ficherosalida" -> {
                if (fileExists(fileName)) {
                    System.err.println("¡Aviso! El archivo de salida ya existe, será reescrito: " + fileName);
                }
                outputFile = fileName;
            }
            case "clave" -> {
                if (!fileExists(fileName)) {
                    System.err.println("¡Error! No existe el fichero de entrada para la clave: " + fileName);
                }
                keyFile = fileName;
            }
            default -> System.err.println("Comando invalido: " + commandName);
        }
    }

    private boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    private void printUsage() {
        Main.printUsage();
    }

    private void printHelp() {
        Main.printHelp();
    }
}
