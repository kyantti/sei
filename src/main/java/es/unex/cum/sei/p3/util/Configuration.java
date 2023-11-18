package es.unex.cum.sei.p3.util;

import es.unex.cum.sei.p3.Main;
import es.unex.cum.sei.p1y2.util.FileHelper;

import javax.crypto.SecretKey;
import java.util.Arrays;

public class Configuration {
    private static String encryptFlag;
    private String inputFile;
    private String outputFile;
    private String keyFile;
    private boolean debugModeEnabled;
    private boolean success;
    private SecretKey key;
    private boolean padding;

    /**
     * Constructor de la clase Configuration.
     *
     * @param args Los argumentos de línea de comandos pasados al programa.
     */
    public Configuration(String[] args) {
        success = true;
        parseArguments(args);
    }
    /**
     * Analiza los argumentos de línea de comandos y el archivo de configuración.
     *
     * @param args Los argumentos de línea de comandos pasados al programa.
     */
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
                        System.err.println("¡Error! No se especificó el nombre del archivo de configuración");
                        printUsage();
                        System.exit(1);
                    }
                }
                case "-h" -> {
                    printHelp();
                    System.exit(0);
                }
                default -> {
                    System.err.println( "¡Error! Argumento desconocido: " + arg);
                    printUsage();
                    System.exit(1);
                }
            }
        }

        if (success){
            System.out.println("Ejecución finalizada con éxito");
        }
        else {
            System.err.println("Ejecución finalizada con errores");
        }
    }
    /**
     * Analiza un archivo de configuración y realiza las acciones especificadas en él.
     *
     * @param configFile El nombre del archivo de configuración.
     */
    private void parseConfigFile(String configFile) {

        if (debugModeEnabled){
            System.out.println("Leyendo el archivo de configuracion: " + configFile);
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
                    System.out.println("Leyendo la linea " + line);
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
    /**
     * Analiza una bandera en el archivo de configuración y realiza acciones basadas en su valor.
     *
     * @param line La línea que contiene la bandera en el archivo de configuración.
     */
    private void parseFlag(String line) {
        if (debugModeEnabled) {
            System.out.println("Procesando la linea: " + line);
        }

        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            System.err.println("Formato de bandera invalido: " + line);
            success = false;
            return;
        }

        String flagName = parts[1].toLowerCase();

        if (parts.length >= 3) {
            String flagValue = parts[2].toUpperCase();
            switch (flagName) {
                case "codifica" -> handleCipheringFlag(flagValue);
                case "traza" -> handleDebugFlag(flagValue);
                default -> {
                    System.err.println("Bandera invalida: " + flagName);
                    success = false;
                }
            }
        }
        else {
            System.err.println("Valor faltante para la bandera '" + flagName + "'");
            success = false;
        }
    }
    /**
     * Maneja la bandera "codifica" en el archivo de configuración.
     *
     * @param flagValue El valor de la bandera "codifica".
     */
    private void handleCipheringFlag(String flagValue) {
        if (flagValue.equals("ON")) {
            encryptFlag = "ON";
        }
        else if (flagValue.equals("OFF")) {
            encryptFlag = "OFF";
        }
        else {
            encryptFlag = flagValue;
            System.err.println("Valor incorrecto para la bandera 'codifica':" + flagValue + " .No se realizará el cifrado/descifrado");
            success = false;
        }
    }
    /**
     * Maneja la bandera "traza" en el archivo de configuración.
     *
     * @param flagValue El valor de la bandera "traza".
     */
    private void handleDebugFlag(String flagValue) {
        if (flagValue.equals("ON")) {
            debugModeEnabled = true;
        }
        else if (flagValue.equals("OFF")) {
            debugModeEnabled = false;
        }
        else {
            System.err.println("Valor incorrecto para la bandera 'traza':" + flagValue + " .No se mostrará la traza de la ejecución del programa");
            success = false;
        }
    }
    /**
     * Analiza un comando en el archivo de configuración y realiza las acciones especificadas en él.
     *
     * @param line La línea que contiene el comando en el archivo de configuración.
     */
    private void parseCommand(String line) {

        if (debugModeEnabled){
            System.out.println("Procesando la linea: " + line);
        }

        String[] parts = line.split("\\s+");

        String commandName = parts[1].toLowerCase();

        if (parts.length == 3) {
            String value = parts[2];
            handleTwoWordCommand(commandName, value);
        }
        else if (parts.length > 3){
            String keySize = parts[2];
            String algorithm = parts[3];
            String[] keyCharacters = Arrays.copyOfRange(parts, 4, parts.length);
            String array = String.join("", keyCharacters).trim();
            handleGenerateKeyCommand(keySize, algorithm, array);
        }
        else if (parts.length == 17 && commandName.equals("cbc")) {
            handleCBCCipherCommand(Arrays.copyOfRange(parts, 2, parts.length));
        }
        else {
            System.err.println("Comando invalido: " + commandName);
            success = false;
        }

    }

    private void handleCBCCipherCommand(String[] strings) {
        if(encryptFlag.equals("ON")){
            Main.encryptUsingCbc(inputFile, outputFile, strings);
        }
        else if (encryptFlag.equals("OFF")){
            Main.decryptUsingCbc(inputFile, outputFile, strings);
        }
        else {
            System.err.println("¡Error! No se especificó la bandera codifica");
            success = false;
        }
    }

    private void handleGenerateKeyCommand(String keySize, String algorithm, String array) {
       if (algorithm.isEmpty() || !algorithm.equals("AES")){
           SecretKey secretKey = Main.generateKey(array, Integer.parseInt(keySize), debugModeEnabled);
           FileHelper.saveSecretKeyToFile(secretKey, keyFile);
           key = secretKey;
       }
       else{
           System.err.println("¡Error! Algoritmo invalido: " + algorithm);
           success = false;
       }
    }

    /**
     * Maneja un comando de dos palabras en el archivo de configuración.
     *
     * @param commandName El nombre del comando de dos palabras.
     * @param value El nombre del archivo asociado al comando.
     */
    private void handleTwoWordCommand(String commandName, String value) {
        switch (commandName) {
            case "ficheroentrada" ->{
                if (value != null && FileHelper.fileExits(value)){
                    inputFile = value;
                }
                else if (!FileHelper.fileExits(value)){
                    System.err.println("!Aviso!: El fichero de entrada especificado no existe");
                    success = false;
                }
            }
            case "ficherosalida" -> {
                if (value != null) {
                    outputFile = value;
                }
                if (FileHelper.fileExits(value)) {
                    System.err.println("¡Aviso! El archivo de salida ya existe, será reescrito: " + value);
                    outputFile = value;
                }
            }
            case "fichero_clave" -> {
                if (!FileHelper.fileExits(value)) {
                    System.err.println("¡Error! No existe el fichero de entrada para la clave: " + value + ". El cifrado/descifrado no se llevará a cabo.");
                }
                keyFile = value;
            }
            case "carga_clave" -> {
                if (value.equals("AES")) {
                    if (FileHelper.fileExits(keyFile)) {
                        SecretKey secretKey = FileHelper.readSectetKeyFromFile(keyFile);
                        if (secretKey != null) {
                            key = secretKey;
                            System.out.println("Clave cargada con éxito");
                        }
                        else {
                            System.err.println("¡Error! No se pudo cargar la clave");
                        }
                    }
                    else {
                        System.err.println("¡Error! No existe el fichero de entrada para la clave: " + keyFile + ". El cifrado/descifrado no se llevará a cabo.");
                    }
                }
                else {
                    System.err.println("¡Error! No se especificó el algoritmo");
                }
            }
            case "AES" ->{
                if (value.equals("conRelleno")){
                    padding = true;
                    if (encryptFlag.equals("ON")){
                        Main.encryptUsingEcb(inputFile, key, outputFile, debugModeEnabled, padding);
                    }
                    else if (encryptFlag.equals("OFF")){
                        Main.decryptUsingEcb(inputFile, key, outputFile, debugModeEnabled, padding);
                    }
                    else {
                        System.err.println("¡Error! No se especificó la bandera codifica");
                        success = false;
                    }
                }
                else if (value.equals("sinRelleno")){
                    padding = false;
                    if (encryptFlag.equals("ON")){
                        Main.encryptUsingEcb(inputFile, key, outputFile, debugModeEnabled, padding);
                    }
                    else if (encryptFlag.equals("OFF")){
                        Main.decryptUsingEcb(inputFile, key, outputFile, debugModeEnabled, padding);
                    }
                    else {
                        System.err.println("¡Error! No se especificó la bandera codifica");
                        success = false;
                    }
                }
                else {
                    System.err.println("¡Error! Valor invalido para el comando AES: " + value);
                }
            }
            default -> System.err.println("Comando invalido: " + commandName);
        }
    }

    /**
     * Imprime el mensaje de uso del programa.
     */
    private void printUsage() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.""");
    }
    /**
     * Imprime la ayuda del programa.
     */
    private void printHelp() {
        System.out.println("""
                La sintaxis del programa debe ser:
                P1_si2023 [-f fichero] | [-h]
                El argumento asociado a –f es el fichero de configuracion
                El argumento –h indica ayuda  y hará que el programa informe al usuario de cuáles son sus posibilidades respecto al contenido y los parametros.
                Si no se especifica el fichero no hará nada""");
    }
}