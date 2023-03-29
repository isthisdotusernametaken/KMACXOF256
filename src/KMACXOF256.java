import java.math.BigInteger;

public class KMACXOF256 {

    static void rightEncode(final BigInteger x) {
        final byte[] bytes = x.toByteArray();
        reverseByteEndianness(bytes);
    }

    private static void reverseByteEndianness(final byte[] bytes) {
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = enc8(bytes[i]);
    }

    private static byte enc8(final byte bigEndian) {
        // Reverse the order of the bits to convert from big-endian to little-endian
        return (byte) (
                ((bigEndian & 0b0000_0001) << 7) |  // Move bit 7 to bit 0
                ((bigEndian & 0b0000_0010) << 5) |  // Bit 6 to bit 1
                ((bigEndian & 0b0000_0100) << 3) |  // Bit 5 to bit 2
                ((bigEndian & 0b0000_1000) << 1) |  // Bit 4 to bit 3
                ((bigEndian & 0b0001_0000) >>> 1) |  // Bit 3 to bit 4
                ((bigEndian & 0b0010_0000) >>> 3) |  // Bit 2 to bit 5
                ((bigEndian & 0b0100_0000) >>> 5) |  // Bit 1 to bit 6
                ((bigEndian & 0b1000_0000) >>> 7)    // Bit 0 to bit 7 — Because this is the only
                                                        // case where the sign bit is 1, this is
                                                        // technically the only case that needs an
                                                        // unsigned right shift
        );
    }
}
