import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    static boolean writeToFile(final byte[] dataToWrite, final String outputType) {
        try {
            Files.write(Path.of(outputType + "_" + System.currentTimeMillis() + ".bin"), dataToWrite);
            return true;
        } catch (IOException e) {
            System.out.println("File could not be written.");
            return false;
        }
    }
}
