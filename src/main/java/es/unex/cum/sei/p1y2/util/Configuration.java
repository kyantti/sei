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

    public Configuration(String[] args) {
        parseArguments(args);
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

        if (debugModeEnabled){
            System.out.println("Leyendo y ejecutando el fichero de configuración: " + configFile);
        }

        String configContents = FileHelper.readFromFile(configFile);

        String[] lines = configContents.split("\\n");

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty() ) {
                continue;
            }
            if (line.startsWith("#")){
                if(debugModeEnabled){
                    System.out.println("Leyendo la linea: " + line);
                }
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

    }


    private void parseFlag(String line) {

        if (debugModeEnabled){
            System.out.println("Procesando la linea: " + line);
        }

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
        }
        else if (flagValue.equals("OFF")) {
            enableDecryptionMode();
        }
        else {
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

    private boolean parseCommand(String line) {

        boolean succes = true;

        if (debugModeEnabled){
            System.out.println("Procesando la linea: " + line);
        }

        String[] parts = line.split("\\s+");

        if (parts.length < 2) {
            System.err.println("Formato del comando invalido: " + line);
            succes = false;
        }

        String commandName = parts[1].toLowerCase();

        if (parts.length == 2) {
            succes = handleSingleWordCommand(commandName);
        }
        else if (parts.length == 3) {
            String fileName = parts[2];
            succes = handleTwoWordCommand(commandName, fileName);
        }
        else {
            System.err.println("Invalid command format: " + line);
        }

        return succes;
    }

    private boolean handleSingleWordCommand(String commandName) {
        boolean success = true;
        switch (commandName) {
            case "formateaentrada" -> {
                if (inputFile != null && outputFile != null){
                    Main.formatInput(inputFile, outputFile, debugModeEnabled);
                }
                else if (inputFile == null){
                    System.err.println("!Aiso! La entrada no será formateada, debido a que no se ha especificado un fichero de entrada");
                    success = false;
                }
                else if (outputFile == null){
                    System.err.println("!Aiso! No se ha especificado un fichero de salida.");
                    success = false;
                }
            }
            case "hill" -> {
                if (inputFile != null){
                    if (FileHelper.isFileFormatted(inputFile)){
                        if (encryptionModeEnabled) {
                            Main.encrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                        }
                        else if (decryptionModeEnabled) {
                            Main.decrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                        }
                    }
                    else {
                        System.err.println("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que el contenido de la entrada no ha sido formateado\nEntrada: " + FileHelper.readFromFile(inputFile));
                        success = false;
                    }
                }
                else{
                    System.err.println("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que no se ha especificado el fichero de entrada");
                    success = false;
                }
            }
            case "ficheroentrada" ->{
                System.err.println("No se ha espeficado el fichero de entrada");
                success = false;
            }
            case "ficherosalida" ->{
                System.err.println("No se ha espeficado el fichero de salida.");
                success = false;
            }
            case "clave" -> System.err.println("No se ha espeficado el fichero clave, se utilizara la matriz clave por defecto\nMatriz clave:\n" + Main.getDefaulKeytMatrix().toString());

            default -> throw new IllegalArgumentException("Comando invalido: " + commandName);
        }
        return  success;
    }

    private boolean handleTwoWordCommand(String commandName, String fileName) {
        boolean succes = true;
        switch (commandName) {
            case "ficheroentrada" ->{
                if (fileName != null && fileExists(fileName)){
                    inputFile = fileName;
                }
                else if (!fileExists(fileName)){
                    System.err.println("!Aviso!: El fichero de entrada especificado no existe");
                    succes = false;
                }
            }
            case "ficherosalida" -> {
                if (fileName != null) {
                    outputFile = fileName;
                }
                if (fileExists(fileName)){
                    System.err.println("¡Aviso! El archivo de salida ya existe, será reescrito: " + fileName);
                    outputFile = fileName;
                }
            }
            case "clave" -> {
                if (!fileExists(fileName)) {
                    System.err.println("¡Error! No existe el fichero de entrada para la clave: " + fileName);
                }
                keyFile = fileName;
            }
            default -> System.err.println("Comando invalido: " + commandName);
        }
        return  succes;
    }

    private boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    private void printUsage() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.""");
    }

    private void printHelp() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.
                Si no se especifica el fichero no hará nada""");
    }
}
