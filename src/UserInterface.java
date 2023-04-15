import java.util.Scanner;

public class UserInterface {

    /* Menu Text. */

    /** Main Menu Options. */
    private static final String MAIN_MENU = """
            Main Menu
            1: Compute plain cryptographic hash
            2: Compute authentication tag (MAC)
            3: Encrypt data symmetrically with passphrase
            4: Decrypt symmetric cryptogram with passphrase
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
            clearScreen(); //TODO only usage of clearScreen(), if (not) desired.
            choice = parseMenuInput(rawInput);

            switch (iCurrentMenu) {
                case MAIN_MENU -> mainMenuHandler(choice);
                case HASH_MENU -> hashMenuHandler(choice);
                case AUTH_MENU -> authMenuHandler(choice);
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
    static void mainMenuHandler(final int pChoice) {
        switch (pChoice) {
            case 1 -> iCurrentMenu = HASH_MENU;
            case 2 -> iCurrentMenu = AUTH_MENU;
            case 3 -> encryptMenuHandler();
            case 4 -> decryptMenuHandler();
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
    static void hashMenuHandler(final int pChoice) {
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
     * File->Byte[]: <a href="https://stackoverflow.com/questions/858980/file-to-byte-in-java">source</a>
     * Byte[]->File: <a href="https://stackoverflow.com/questions/4350084/byte-to-file-in-java">unused</a>
     */
    static void hashFromFile() {
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
    static void hashFromInput() {
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
    static void authMenuHandler(final int pChoice) {
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
    static void authFromFile() {
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
    static void authFromInput() {
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
    static void encryptMenuHandler() {
        System.out.println(ENCRYPT_MENU);
        System.out.println(FILE_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();
        //do stuff
        iCurrentMenu = MAIN_MENU;
    }

    /**
     * Functionality for decryption of file.
     * TODO: current code body just here for testing. To see if String->Hex conversion was successful use link.
     * <a href="https://string-functions.com/string-hex.aspx">String->Hex checker</a>
     */
    static void decryptMenuHandler() {
        System.out.println(DECRYPT_MENU);
        System.out.println(FILE_INPUT_PROMPT);
        String rawInput = TEIN.nextLine();

        //do stuff
        byte[] byteInput = Util.stringToFormattedBytes(rawInput);
        printByteArrayAsHex(byteInput);

        iCurrentMenu = MAIN_MENU;
    }

    /* Helper functions. */

    /**
     * Takes String to make into a nice int.
     * @param pInput String input from user.
     * @return menu choice if valid input, -1 otherwise.
     */
    static int parseMenuInput(final String pInput) {
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
    public static void clearScreen() {
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
