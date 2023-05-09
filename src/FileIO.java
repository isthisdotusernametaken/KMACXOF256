import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        } catch (IOException | SecurityException e) {
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
        } catch (IOException | SecurityException e) {
            System.out.println("File could not be written.");
            return false;
        }
    }

    static boolean writeArraysToFile(final String filename, final byte[]... arrays) {
        try (var output = new FileOutputStream(filename + ".bin")) {
            // File format:
            // Number of arrays (4 bytes),
            // Length x1 of first array (4 bytes), Length x2 of second array
            //      (4 bytes), ..., Length xn of last array (4 bytes),
            // First array (x1 bytes), Second array (x2 bytes), ..., Last array
            //      (xn bytes)

            output.write(Util.toBytes(arrays.length));
            for (var array : arrays)
                output.write(Util.toBytes(array.length));
            for (var array : arrays)
                output.write(array);

            return true;
        } catch (IOException | SecurityException e) {
            System.out.println("File could not be written.");
            return false;
        }
    }

    static boolean readArraysFromFile(final byte[][][] fileContents, final String filename) {
        try (var input = new FileInputStream(filename + ".bin")) {
            // File format: See writeArraysToFile

            var arrays = new byte[Util.toInt(input.readNBytes(4))][];
            for (int i = 0; i < arrays.length; i++)
                arrays[i] = new byte[Util.toInt(input.readNBytes(4))];
            for (var array : arrays)
                if (input.read(array) != array.length)
                    throw new EOFException();

            fileContents[0] = arrays; // Return value (used so that an explicit
                                      // boolean can indicate success/failure)

            return true;
        } catch (IllegalArgumentException | EOFException e) {
            System.out.println("File could not be read (invalid format).");
            return false;
        } catch (IOException | SecurityException e) {
            System.out.println("File could not be read.");
            return false;
        }
    }
}
