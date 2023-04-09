import java.math.BigInteger;

public class Util {

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    static byte[] ASCIIStringToBytes(final String ASCII) {
        if (ASCII.isEmpty()) {
            return new byte[1];
        }
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

    static String bytesToByteSpacedHexString(final byte[] bytes) {
        var builder = new StringBuilder();

        for (byte b : bytes)
            builder.append(HEX_CHARS[(b >>> 4) & 0x0F]).append(HEX_CHARS[b & 0x0F])
                   .append(' ');

        return bytes.length == 0 ?
               "" :
               builder.deleteCharAt(builder.length() - 1).toString();
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

    static byte[] cat(final byte[] bytes, final byte newByte, final boolean prepend) {
        final byte[] result = new byte[bytes.length + 1];

        System.arraycopy(result, 0, bytes, prepend ? 1 : 0, bytes.length);
        result[prepend ? 0 : bytes.length - 1] = newByte;

        return result;
    }
}
