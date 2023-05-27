import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Joshua Barbee, James Deal
 */
public class Util {

    static final SecureRandom RANDOM = new SecureRandom();

    static byte[] ASCIIStringToBytes(final String ASCII) {
        var chars = ASCII.toCharArray();
        var bytes = new byte[chars.length];

        for (int i = 0; i < chars.length; i++)
            bytes[i] = (byte) chars[i];

        return bytes;
    }

    static byte[] bigIntegerToBytes(final BigInteger integer) {
        byte[] asBytes = integer.toByteArray();

        if (asBytes[0] == 0 && !BigInteger.ZERO.equals(integer)) {
            final byte[] withoutByteForSignBit = new byte[asBytes.length - 1];
            System.arraycopy(asBytes, 1, withoutByteForSignBit, 0, withoutByteForSignBit.length);
            asBytes = withoutByteForSignBit;
        }

        return asBytes;
    }

    /**
     * Byte array concat for variable amounts of byte arrays.
     * @param sourceArrays array of source byte arrays.
     * @return single byte array from concat of source arrays.
     */
    static byte[] cat(final byte[]... sourceArrays) {
        int resSize = 0;
        for (byte[] ba: sourceArrays) {
            resSize += ba.length;
        }
        final byte[] result = new byte[resSize];
        int cursor = 0;
        for (byte[] ba: sourceArrays) {
            System.arraycopy(ba, 0, result, cursor, ba.length);
            cursor +=  ba.length;
        }
        return result;
    }

    static void split(final byte[][] destinationArrays, final byte[] sourceArray) {
        destinationArrays[0] = new byte[sourceArray.length / 2];
        destinationArrays[1] = new byte[sourceArray.length / 2];

        System.arraycopy(sourceArray, 0, destinationArrays[0], 0, sourceArray.length / 2);
        System.arraycopy(sourceArray, sourceArray.length / 2, destinationArrays[1], 0, sourceArray.length / 2);
    }

    /**
     * Turn a byte[] into formatted hex string array to make it look pretty.
     * @param pSourceByteArray source byte[] to format.
     * @return string[] of hex representation for byte[].
     */
    static String[] byteArrayToHexStringArray(final byte[] pSourceByteArray) {
        String[] output = new String[pSourceByteArray.length];

        for (int i = 0; i < pSourceByteArray.length; i++) {
            output[i] = String.format("%02X ", pSourceByteArray[i]);
        }

        return output;
    }

    /**
     * XOR's contents of two byte arrays, explodes if arrays are not same length.
     * @param first first byte array to xor.
     * @param second second byte array to xor.
     * @return byte array of xor'd values.
     */
    static byte[] xorByteArrays(final byte[] first, final byte[] second) {

        if (first.length != second.length) {
            throw new IllegalStateException("lengths of arrays to XOR do not match. L1:"
                    + first.length  + " L2:" +  second.length);
        }

        byte[] res = new byte[first.length];

        for (int i = 0; i < first.length; i++) {
            res[i] = (byte) (first[i] ^ second[i]);
        }

        return res;
    }

    static byte[] toBytes(final int intVal) {
        return new byte[]{
                (byte) (intVal >>> 24),
                (byte) (intVal >>> 16),
                (byte) (intVal >>> 8),
                (byte) intVal
        };
    }

    static int toInt(final byte[] bytes) throws IllegalArgumentException {
        if (bytes.length != 4)
            throw new IllegalArgumentException(
                    "4 bytes must be provided for an int"
            );

        return ((((int) bytes[0]) & 0xFF) << 24) |
               ((((int) bytes[1]) & 0xFF) << 16) |
               ((((int) bytes[2]) & 0xFF) << 8) |
               (((int) bytes[3]) & 0xFF);
    }
}
