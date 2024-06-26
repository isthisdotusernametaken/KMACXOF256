import java.util.Scanner;

/**
 * @author James Deal
 */
public class UserInterface {

    /* Menu Text. */

    /** Main Menu Options. */
    private static final String MAIN_MENU = """
            Main Menu
            1: Compute plain cryptographic hash
            2: Compute authentication tag (MAC)
            3: Encrypt data symmetrically with passphrase
            4: Decrypt symmetric cryptogram with passphrase
            5: Test included functionality
            6: Generate a Schnorr/DHIES key pair with passphrase
            7: Encrypt using Schnorr/DHIES with key
            8: Decrypt using Schnorr/DHIES with passphrase
            9: Generate signature using Schnorr/DHIES with passphrase
            10: Verify signature using Schnorr/DHIES with passphrase
            0: Exit""";

    /** Hash Menu Options. */
    private static final String HASH_MENU = """
            Function: Cryptographic Hash
            1: File input source
            2: Direct input from terminal
            0: Back to main menu""";

    /** Auth Menu Options. */
    private static final String AUTH_MENU = """
            Function: Authentication Tag (MAC)
            1: File input source
            2: Direct input from terminal
            0: Back to main menu""";

    /** Hash Menu Options. */
    private static final String SD_ENCRYPT_MENU = """
            Function: Schnorr/DHIES Encryption
            1: File input source
            2: Direct input from terminal
            0: Back to main menu""";

    /** Hash Menu Options. */
    private static final String SD_SIGN_MENU = """
            Function: Schnorr/DHIES Signing
            1: File input source
            2: Direct input from terminal
            0: Back to main menu""";

    /** Encrypt Menu. */
    private static final String ENCRYPT_MENU = "Function: Encrypt file with given passphrase";

    /** Decrypt Menu. */
    private static final String DECRYPT_MENU = "Function: Decrypt file with given passphrase";

    /* Prompts. */

    /** Menu option dialogue. */
    private static final String MENU_INPUT_PROMPT = "Type the number of the desired function and press enter: ";
    /** File input dialogue. */
    private static final String FILE_INPUT_PROMPT = "Input location of file: ";
    /** Text input dialogue. */
    private static final String TEXT_INPUT_PROMPT = "Input text: ";
    /** Passphrase input dialogue. */
    private static final String PASSPHRASE_INPUT_PROMPT = "Input passphrase: ";

    /* Other Stuff. */

    /** User input. */
    private static final Scanner TEIN = new Scanner(System.in);

    /** Menu location. */
    private static String iCurrentMenu;
    /** Flag for when to quit. */
    private static boolean iKeepGoing;

    /* Methods. */

    /** Unused. */
    private UserInterface() {
        //overriding default constructor.
    }

    /**
     * Menu Navigation.
     */
    static void menuNav() {
        int choice;
        iCurrentMenu = MAIN_MENU;
        iKeepGoing = true;

        while (iKeepGoing) {
            System.out.println(iCurrentMenu);
            System.out.println(MENU_INPUT_PROMPT);

            String rawInput = TEIN.nextLine();
            clearScreen();
            choice = parseMenuInput(rawInput);

            switch (iCurrentMenu) {
                case MAIN_MENU -> mainMenuHandler(choice);
                case HASH_MENU -> hashMenuHandler(choice);
                case AUTH_MENU -> authMenuHandler(choice);
                case SD_ENCRYPT_MENU -> sdEncryptMenu(choice);
                case SD_SIGN_MENU -> sdSigningMenu(choice);
                default -> {
                    System.out.println("Error, current menu: " + iCurrentMenu);
                    return;
                }
            }

        }
    }

    /**
     * Choice handler for main menu.
     * @param pChoice choice in menu.
     */
    private static void mainMenuHandler(final int pChoice) {
        switch (pChoice) {
            case 1 -> iCurrentMenu = HASH_MENU;
            case 2 -> iCurrentMenu = AUTH_MENU;
            case 3 -> encryptMenuHandler();
            case 4 -> decryptMenuHandler();
            case 5 -> testMenuHandler();
            case 6 -> sdKeyGenerate();
            case 7 -> iCurrentMenu = SD_ENCRYPT_MENU;
            case 8 -> sdDecryptHandler();
            case 9 -> iCurrentMenu = SD_SIGN_MENU;
            case 10 -> sdVerifySignature();
            case 0 -> {
                System.out.println("Goodbye.");
                iKeepGoing = false;
            }
            case -1 -> System.out.print(""); //do nothing on purpose.
            default -> System.out.println("Invalid choice: " + pChoice);
        }
    }

    /**
     * Navigation of hash menu.
     * @param pChoice choice in menu.
     */
    private static void hashMenuHandler(final int pChoice) {
        switch (pChoice) {
            case 1 -> hashFromFile();
            case 2 -> hashFromInput();
            case 0 -> iCurrentMenu = MAIN_MENU;
            case -1 -> System.out.print(""); //do nothing on purpose.
            default -> System.out.println("Invalid choice: " + pChoice);
        }
    }

    /**
     * Functionality for obtaining cryptographic hash of a file.
     */
    private static void hashFromFile() {
        //say hello, get input.
        System.out.println(HASH_MENU.substring(0, 28));
        System.out.println(FILE_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();

        //do work.
        byte[][] fileContent = new byte[1][];
        byte[] output;

        if (FileIO.readFromFile(fileContent, rawInput)) {
            output = Services.cryptographicHash(fileContent[0]);

            if (FileIO.writeToFileWithTimestamp(output, "hash")) {
                System.out.println("Hash generated from input:");
                UserInterface.printByteArrayAsHex(output);
            }
        }

        //back to the top.
        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Functionality for obtaining cryptographic hash of user input.
     */
    private static void hashFromInput() {
        //say hello, get input.
        System.out.println(HASH_MENU.substring(0, 28));
        System.out.println(TEXT_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();

        //do work.
        byte[] byteInput = Util.ASCIIStringToBytes(rawInput);
        byte[] output = Services.cryptographicHash(byteInput);

        System.out.println("Hash generated from input:");
        printByteArrayAsHex(output);

        //back to the top.
        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Navigation of auth tag menu.
     * @param pChoice choice in menu.
     */
    private static void authMenuHandler(final int pChoice) {
        switch (pChoice) {
            case 1 -> authFromFile();
            case 2 -> authFromInput();
            case 0 -> iCurrentMenu = MAIN_MENU;
            case -1 -> System.out.print(""); //do nothing on purpose.
            default -> System.out.println("Invalid choice: " + pChoice);
        }
    }

    /**
     * Functionality for obtaining auth tag of a file.
     */
    private static void authFromFile() {
        System.out.println(AUTH_MENU.substring(0, 34));
        System.out.println(FILE_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();

        //do work.
        byte[][] fileContent = new byte[1][];
        byte[] output;
        byte[] bytePw = Util.ASCIIStringToBytes(rawPwInput);

        if (FileIO.readFromFile(fileContent, rawInput)) {
            output = Services.authenticationTag(fileContent[0], bytePw);

            if (FileIO.writeToFileWithTimestamp(output, "mac")) {
                System.out.println("Auth Tag generated from input:");
                UserInterface.printByteArrayAsHex(output);
            }
        }

        //back to the top.
        iCurrentMenu = MAIN_MENU;
    }


    /**
     * Functionality for obtaining auth tag of user input.
     */
    private static void authFromInput() {
        System.out.println(AUTH_MENU.substring(0, 34));
        System.out.println(TEXT_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();

        //do work.
        byte[] byteInput = Util.ASCIIStringToBytes(rawInput);
        byte[] bytePw = Util.ASCIIStringToBytes(rawPwInput);

        byte[] output = Services.authenticationTag(byteInput, bytePw);

        System.out.println("Auth Tag generated from input:");
        printByteArrayAsHex(output);

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Functionality for encryption of file.
     */
    private static void encryptMenuHandler() {
        System.out.println(ENCRYPT_MENU);
        System.out.println(FILE_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();
        System.out.println("What should the output file be named:");
        String outputName = TEIN.nextLine();

        //do work
        byte[][] fileContent = new byte[1][];
        SymmetricCryptogram output;
        byte[] bytePw = Util.ASCIIStringToBytes(rawPwInput);

        if (FileIO.readFromFile(fileContent, rawInput)) {
            output = Services.encryptSymm(fileContent[0], bytePw);

            if (FileIO.writeArraysToFile(
                    outputName,
                    output.z(), output.c(), output.t()
            ))
                System.out.println("File written successfully.");
        }

        //back to the top.
        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Functionality for decryption of file.
     */
    private static void decryptMenuHandler() {
        System.out.println(DECRYPT_MENU);
        System.out.println("Input filename given during encryption process:");
        String rawInput = TEIN.nextLine();
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();
        System.out.println("Input name of desired output file with extension:");
        String rawOutInput = TEIN.nextLine();

        //do stuff
        byte[][][] fileContents = new byte[1][][];
        byte[] bytePw = Util.ASCIIStringToBytes(rawPwInput);

        if (FileIO.readArraysFromFile(fileContents, rawInput)) {
            SymmetricCryptogram inData = new SymmetricCryptogram(
                    fileContents[0][0], fileContents[0][1], fileContents[0][2]
            );

            byte[][] m = new byte[1][];
            if (Services.decryptSymm(m, inData, bytePw)) {
                System.out.println("Decrypt successful.");

                if (FileIO.writeToFile(m[0], rawOutInput, false)) {
                    System.out.println("Files written successfully.");
                }
            } else {
                System.out.println("Decrypt failed: t did not match t'.");
            }
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Runs included test suite for functionality.
     */
    private static void testMenuHandler() {
        System.out.println("Running included tests.");
        TestValidity.runTests();
        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Generates a Schnorr/DHIES key pair with a given passphrase.
     */
    private static void sdKeyGenerate() {
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();
        System.out.println("What should the output file be named:");
        String outputName = TEIN.nextLine();

        //Make Elliptical key pair
        EllipticKeyPair ekp = Services.generateKeyPair(Util.ASCIIStringToBytes(rawPwInput));

        //Save public key
        if (FileIO.writeArraysToFile(outputName, ekp.V().toBytes())) {
            System.out.println("Success, public key written to: " + outputName + ".bin");
        } else {
            System.out.println("File writing did not work right!");
            return;
        }

        //encrypt private key and save
        SymmetricCryptogram encPrivateKey = Services.encryptSymm(ekp.s(), Util.ASCIIStringToBytes(rawPwInput));

        if (FileIO.writeArraysToFile(
                outputName + "_private",
                encPrivateKey.z(), encPrivateKey.c(), encPrivateKey.t()
        ))
            System.out.println("Private key encrypted and saved using given passphrase.");
    }

    /**
     * Navigation of Schnorr/DHIES Encrypt menu.
     * @param pChoice choice in menu.
     */
    private static void sdEncryptMenu(final int pChoice) {
        switch (pChoice) {
            case 1 -> sdEncryptFromFile();
            case 2 -> sdEncryptFromInput();
            case 0 -> iCurrentMenu = MAIN_MENU;
            case -1 -> System.out.print(""); //do nothing on purpose.
            default -> System.out.println("Invalid choice: " + pChoice);
        }
    }

    /**
     * Encrypt file using Schnorr/DHIES.
     */
    private static void sdEncryptFromFile() {
        System.out.println("What should the output file be named:");
        String outputName = TEIN.nextLine();
        System.out.println("Source File including extension:");
        String sourceFile = TEIN.nextLine();
        System.out.println("Source Public Key:");
        String sourceKey = TEIN.nextLine();

        byte[][] sourceFileContent = new byte[1][];
        if (!FileIO.readFromFile(sourceFileContent, sourceFile)) {
            System.out.println("Content File could not be read.");
            return;
        }

        byte[][][] sourceKeyContent = new byte[1][][];
        if (!FileIO.readArraysFromFile(sourceKeyContent, sourceKey)) {
            System.out.println("Key File could not be read.");
            return;
        }

        DHIESCryptogram dc = Services.encryptAsymm(sourceFileContent[0],
                new Ed448GoldilocksPoint(sourceKeyContent[0][0], sourceKeyContent[0][1]));

        if (!FileIO.writeArraysToFile(outputName, dc.Z().x.toByteArray(), dc.Z().y.toByteArray(), dc.c(), dc.t())) {
            System.out.println("Output file could not be written.");
            return;
        } else {
            System.out.println("File written successfully: " + outputName + ".bin");
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Encrypt terminal input using Schnorr/DHIES.
     */
    private static void sdEncryptFromInput() {
        System.out.println("What should the output file be named:");
        String outputName = TEIN.nextLine();
        System.out.println("Source Text:");
        byte[] sourceText = Util.ASCIIStringToBytes(TEIN.nextLine());
        System.out.println("Source Public Key:");
        String sourceKey = TEIN.nextLine();

        byte[][][] sourceKeyContent = new byte[1][][];
        if (!FileIO.readArraysFromFile(sourceKeyContent, sourceKey)) {
            System.out.println("Key File could not be read.");
            return;
        }

        DHIESCryptogram dc = Services.encryptAsymm(sourceText,
                new Ed448GoldilocksPoint(sourceKeyContent[0][0], sourceKeyContent[0][1]));

        if (!FileIO.writeArraysToFile(outputName, dc.Z().x.toByteArray(), dc.Z().y.toByteArray(), dc.c(), dc.t())) {
            System.out.println("Output file could not be written.");
            return;
        } else {
            System.out.println("File written successfully: " + outputName + ".bin");
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Decrypt sd file from given passphrase and write to new file.
     */
    private static void sdDecryptHandler() {
        System.out.println("What should the output file be named including file extension:");
        String outputName = TEIN.nextLine();
        System.out.println("Source File:");
        String sourceFile = TEIN.nextLine();
        System.out.println("Input Passphrase:");
        String sourceKey = TEIN.nextLine();

        byte[][][] sourceFileContent = new byte[1][][];
        if (!FileIO.readArraysFromFile(sourceFileContent, sourceFile)) {
            System.out.println("Content File could not be read.");
            return;
        }

        Ed448GoldilocksPoint Z = new Ed448GoldilocksPoint(sourceFileContent[0][0], sourceFileContent[0][1]);
        DHIESCryptogram dc = new DHIESCryptogram(Z, sourceFileContent[0][2], sourceFileContent[0][3]);

        byte[][] mOut = new byte[1][];
        if (Services.decryptAsymm(mOut, dc, Util.ASCIIStringToBytes(sourceKey))) {
            System.out.println("Passphrase verified, content decrypted.");

            if (!FileIO.writeToFile(mOut[0], outputName, false)) {
                System.out.println("Output file could not be written.");
                return;
            } else {
                System.out.println("File written successfully: " + outputName);
            }
        } else {
            System.out.println("Passphrase incorrect.");
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Navigation of Schnorr/DHIES Signing menu.
     * @param pChoice choice in menu.
     */
    private static void sdSigningMenu(final int pChoice) {
        switch (pChoice) {
            case 1 -> sdSignFile();
            case 2 -> sdSignInput();
            case 0 -> iCurrentMenu = MAIN_MENU;
            case -1 -> System.out.print(""); //do nothing on purpose.
            default -> System.out.println("Invalid choice: " + pChoice);
        }
    }

    /**
     * Sign file using Schnorr/DHIES with passphrase and write to new file.
     */
    private static void sdSignFile() {
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();
        System.out.println("What should the output signature file be named:");
        String outputName = TEIN.nextLine();
        System.out.println("Source File to sign including extension:");
        String sourceFile = TEIN.nextLine();

        byte[][] sourceFileContent = new byte[1][];
        if (!FileIO.readFromFile(sourceFileContent, sourceFile)) {
            System.out.println("File could not be read.");
            return;
        }

        SchnorrSignature ss = Services.signFile(sourceFileContent[0], Util.ASCIIStringToBytes(rawPwInput));

        if (FileIO.writeArraysToFile(outputName, ss.h(), ss.z())) {
            System.out.println("Success! Signature written to: " + outputName + ".bin");
        } else {
            System.out.println("File writing did not work right!");
            return;
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Sign user input using Schnorr/DHIES with passphrase and write to new file.
     */
    private static void sdSignInput() {
        System.out.println(PASSPHRASE_INPUT_PROMPT);
        String rawPwInput = TEIN.nextLine();
        System.out.println("What should the output signature file be named:");
        String outputName = TEIN.nextLine();
        System.out.println("Text to sign:");
        byte[] sourceData = Util.ASCIIStringToBytes(TEIN.nextLine());
        System.out.println("File to write text to:");
        String sourceFile = TEIN.nextLine();

        if (!FileIO.writeToFile(sourceData, sourceFile + ".txt", false)) {
            System.out.println("Source data could not be written to file.");
            return;
        }

        SchnorrSignature ss = Services.signFile(sourceData, Util.ASCIIStringToBytes(rawPwInput));

        if (FileIO.writeArraysToFile(outputName, ss.h(), ss.z())) {
            System.out.println("Success! Signature written to: " + outputName + ".bin");
        } else {
            System.out.println("File writing did not work right!");
            return;
        }

        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Verify data file and signature file using given public key file.
     */
    private static void sdVerifySignature() {
        System.out.println("Source File to check signature for including extension:");
        String sourceFile = TEIN.nextLine();
        System.out.println("Signature file:");
        String sigFile = TEIN.nextLine();
        System.out.println("Public key file:");
        String pubKeyFile = TEIN.nextLine();

        byte[][][] sigData = new byte[1][][];
        byte[][][] pubKey = new byte[1][][];
        byte[][] sourceData = new byte[1][];

        if (!FileIO.readArraysFromFile(sigData, sigFile)) {
            System.out.println("Signature file could not be read.");
            return;
        }
        SchnorrSignature hz = new SchnorrSignature(sigData[0][0], sigData[0][1]);

        if (!FileIO.readArraysFromFile(pubKey, pubKeyFile)) {
            System.out.println("Public Key file could not be read.");
            return;
        }
        Ed448GoldilocksPoint V = new Ed448GoldilocksPoint(pubKey[0][0], pubKey[0][1]);

        if (!FileIO.readFromFile(sourceData, sourceFile)) {
            System.out.println("Public Key file could not be read.");
            return;
        }

        if (Services.verifySignature(hz, V, sourceData[0])) {
            System.out.println("Signature verified.");
        } else {
            System.out.println("WARNING: Signature invalid.");
        }
    }

    /* Helper functions. */

    /**
     * Takes String to make into a nice int.
     * @param pInput String input from user.
     * @return menu choice if valid input, -1 otherwise.
     */
    private static int parseMenuInput(final String pInput) {
        int res = -1;
        try {
            res = Integer.parseInt(pInput);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid input given, value not a number: " + pInput);
        }

        return res;
    }

    /**
     * Print byte[] as formatted hex string to make it look pretty.
     * @param pSourceByteArray source byte[] to print.
     */
    static void printByteArrayAsHex(final byte[] pSourceByteArray) {
        String[] hexIt = Util.byteArrayToHexStringArray(pSourceByteArray);

        for (int i = 0; i < hexIt.length; i++) {
            System.out.print(hexIt[i]);
            if ((i + 1) % 16 == 0 && (i + 1) < hexIt.length) {
                System.out.print('\n');
            } else {
                System.out.print(' ');
            }
        }
        System.out.print('\n');
    }

    /**
     * Reused from previous project work: <a href="https://github.com/seburoh/crabnicholson">Crab Nicholson</a>
     * Clears the terminal by invoking the environment's clear command.
     * Differs between Windows and Unix
     *
     * @see <a href="https://stackoverflow.com/questions/2979383/java-clear-the-console">java clear the console</a>
     */
    private static void clearScreen() {
        try { //windows
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) { //mac and linux
            try {
                String term = System.getenv("TERM"); // https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#getenv-java.lang.String-
                if (term != null && !term.equals("dumb")) {
                    new ProcessBuilder("clear").inheritIO().start().waitFor();
                }
            } catch (Exception ignored) {
                //empty on purpose.
            }
        }
    }
}
