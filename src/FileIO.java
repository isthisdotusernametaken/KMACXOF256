import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Joshua Barbee
 */
public class FileIO {

    static boolean readFromFile(final byte[][] fileContents, final String filename) {
        try {
            fileContents[0] = Files.readAllBytes(Path.of(filename));
            return true;
        } catch (IOException e) {
            System.out.println("File could not be read.");
            return false;
        }
    }

    static boolean writeToFileWithTimestamp(final byte[] dataToWrite, final String outputType) {
        return writeToFile(dataToWrite, outputType + "_" + System.currentTimeMillis(), true);
    }

    static boolean writeToFile(final byte[] dataToWrite, final String filename, final boolean appendBinExtension) {
        try {
            Files.write(Path.of(filename + (appendBinExtension ? ".bin" : "")), dataToWrite);
            return true;
        } catch (IOException e) {
            System.out.println("File could not be written.");
            return false;
        }
    }
}
