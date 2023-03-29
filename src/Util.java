public class Util {
    public static byte[] concatenate(final byte[] leftBytes, final byte[] rightBytes) {
        final byte[] result = new byte[leftBytes.length + rightBytes.length];

        System.arraycopy(leftBytes, 0, result, 0, leftBytes.length);
        System.arraycopy(rightBytes, 0, result, leftBytes.length, rightBytes.length);

        return result;
    }

    public static byte[] concatenate(final byte[] bytes, final byte newByte, final boolean prepend) {
        final byte[] result = new byte[bytes.length + 1];

        System.arraycopy(result, 0, bytes, prepend ? 1 : 0, bytes.length);
        result[prepend ? 0 : bytes.length - 1] = newByte;

        return result;
    }
}
