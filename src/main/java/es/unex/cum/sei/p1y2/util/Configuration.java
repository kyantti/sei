package es.unex.cum.sei.p1y2.util;

import es.unex.cum.sei.p1y2.Main;

import java.io.File;
import java.io.IOException;

public class Configuration {
    private static String encryptFlag;
    private static String debugFlag;
    private String inputFile;
    private String outputFile;
    private String keyFile;
    private boolean debugModeEnabled;
    private boolean success;

    public Configuration(String[] args) {
        success = true;
        parseArguments(args);
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
                        printErrorInfo("Falta el argumento del fichero de configuracion");
                        printUsage();
                        System.exit(1);
                    }
                }
                case "-h" -> {
                    printHelp();
                    System.exit(0);
                }
                default -> {
                    printErrorInfo("Argumento invalido: " + arg);
                    printUsage();
                    System.exit(1);
                }
            }
        }

        if (success){
            printDebugInfo("Ejecución finalizada con exito");
        }
        else {
            printErrorInfo("Ejecución finalizada con errores");
        }
    }

    private void parseConfigFile(String configFile) {

        if (debugModeEnabled){
            printDebugInfo("Leyendo y ejecutando el fichero de configuración: " + configFile);
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
                    printDebugInfo("Leyendo la linea: " + line);
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
                printErrorInfo("Linea invalida en el archivo de configuracion " + line);
            }
        }

    }


    private void parseFlag(String line) {
        if (debugModeEnabled) {
            printDebugInfo("Procesando la linea: " + line);
        }

        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            printErrorInfo("Formato de bandera invalido: " + line);
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
                    printErrorInfo("Bandera invalida: " + flagName);
                    success = false;
                }
            }
        }
        else {
            printErrorInfo("Valor faltante para la bandera '" + flagName + "'");
            success = false;
        }
    }


    private void handleCipheringFlag(String flagValue) {
        if (flagValue.equals("ON")) {
            encryptFlag = "ON";
        }
        else if (flagValue.equals("OFF")) {
            encryptFlag = "OFF";
        }
        else {
            encryptFlag = flagValue;
            printErrorInfo("Valor invalido para la bandera 'codifica': " + flagValue);
            success = false;
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
            System.err.println("Valor incorrecto para la bandera 'traza':" + flagValue + " .No se mostrará la traza de la ejecución del programa");
            success = false;
        }
    }

    private void parseCommand(String line) {

        if (debugModeEnabled){
            printDebugInfo("Procesando la linea: " + line);
        }

        String[] parts = line.split("\\s+");

        if (parts.length < 2) {
            printErrorInfo("Formato del comando invalido: " + line);
            success = false;
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
            printErrorInfo("Formato de comando invalido: " + line);
        }

    }

    private void handleSingleWordCommand(String commandName) {
        switch (commandName) {
            case "formateaentrada" -> {
                if (inputFile != null && outputFile != null){
                    Main.formatInput(inputFile, outputFile, debugModeEnabled);
                }
                else if (inputFile == null){
                    printErrorInfo("!Aiso! La entrada no será formateada, debido a que no se ha especificado un fichero de entrada");
                    success = false;
                }
                else if (outputFile == null){
                    printErrorInfo("!Aiso! No se ha especificado un fichero de salida.");
                    success = false;
                }
            }
            case "hill" -> {
                if (inputFile != null){
                    if (FileHelper.isFileFormatted(inputFile) && (keyFile == null || fileExists(keyFile))){
                        if (encryptFlag != null &&  encryptFlag.equals("ON")) {
                            Main.encrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                        }
                        else if (encryptFlag != null && encryptFlag.equals("OFF")) {
                            Main.decrypt(inputFile, keyFile, outputFile, debugModeEnabled);
                        }
                        else{
                            printErrorInfo("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que no se ha especificado un valor valido para la bandera 'codifica' " + encryptFlag);
                            success = false;
                        }
                    }
                    else if (!FileHelper.isFileFormatted(inputFile)){
                        printErrorInfo("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que el contenido de la entrada no ha sido formateado\nEntrada: " + FileHelper.readFromFile(inputFile));
                        success = false;
                    }
                    else if (keyFile != null && !fileExists(keyFile)){
                        printErrorInfo("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que el fichero: " + keyFile + " no existe");
                        success = false;
                    }
                }
                else{
                    printErrorInfo("!Aviso! El cifrado / descifrado no se llevará a cabo debido a que no se ha especificado el fichero de entrada");
                    success = false;
                }
            }
            case "ficheroentrada" ->{
                printErrorInfo("No se ha espeficado el fichero de entrada");
                success = false;
            }
            case "ficherosalida" ->{
                printErrorInfo("No se ha espeficado el fichero de salida.");
                success = false;
            }
            case "clave" -> printErrorInfo("No se ha espeficado el fichero clave, se utilizara la matriz clave por defecto\nMatriz clave:\n" + Main.getDefaulKeytMatrix().toString());

            default ->{
                printErrorInfo("Comando invalido: " + commandName);
                success = false;
            }
        }
    }

    private void handleTwoWordCommand(String commandName, String fileName) {
        switch (commandName) {
            case "ficheroentrada" ->{
                if (fileName != null && fileExists(fileName)){
                    inputFile = fileName;
                }
                else if (!fileExists(fileName)){
                    printErrorInfo("!Aviso!: El fichero de entrada especificado no existe");
                    success = false;
                }
            }
            case "ficherosalida" -> {
                if (fileName != null) {
                    outputFile = fileName;
                }
                if (fileExists(fileName)){
                    printErrorInfo("¡Aviso! El archivo de salida ya existe, será reescrito: " + fileName);
                    outputFile = fileName;
                }
            }
            case "clave" -> {
                if (!fileExists(fileName)) {
                    printErrorInfo("¡Error! No existe el fichero de entrada para la clave: " + fileName + ". El cifrado/descifrado no se llevará a cabo.");
                }
                keyFile = fileName;
            }
            default -> printErrorInfo("Comando invalido: " + commandName);
        }
    }

    private boolean fileExists(String fileName) {
        return FileHelper.fileExits(fileName);
    }

    private void printDebugInfo(String info){
        if (debugModeEnabled){
            System.err.println(info);
        }
    }

    private void printErrorInfo(String error){
        if (debugModeEnabled){
            System.err.println(error);
        }
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
