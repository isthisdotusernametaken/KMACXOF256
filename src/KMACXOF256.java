import java.math.BigInteger;

public class KMACXOF256 {

//    static byte[] cSHAKE256(final byte[] X, final int L, final byte[] N, final byte[] S) {
//        if (N.length == 0 && S.length == 0)
//            return SHAKE256(X, L);
//
//        return Keccak.Keccak512(
//                new BigInteger(Util.cat(
//                        bytepad(
//                                Util.cat(encodeString(N), encodeString(S)),
//                                136
//                        ),
//                        X
//                )).shiftLeft(2).toByteArray(),
//                L
//        );
//    }

    static byte[] bytepad(final byte[] X, final int w) {
        BigInteger z = new BigInteger(Util.cat(
                leftEncode(BigInteger.valueOf(w).toByteArray()),
                X
        ));

        while (z.bitLength() % 8 != 0)
            z = z.shiftLeft(1);
        while ((z.bitLength() / 8) % w != 0)
            z = z.shiftLeft(8);

        return z.toByteArray();
    }

    static byte[] encodeString(final byte[] S) {
        return Util.cat(leftEncode(BigInteger.valueOf(S.length).toByteArray()), S);
    }

    static byte[] rightEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, false);
    }

    static byte[] leftEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, true);
    }

    private static byte[] encodeLeftOrRight(final byte[] bytesOfX, final boolean prependLength) {
        //reverseByteEndianness(bytesOfX);

        final byte[] bytesOfO = new byte[bytesOfX.length + 1];
        System.arraycopy(bytesOfX, 0, bytesOfO, prependLength ? 1 : 0, bytesOfX.length);
        bytesOfO[prependLength ? 0 : bytesOfO.length - 1] = (byte) bytesOfX.length; // enc8((byte) bytesOfX.length);

        return bytesOfO;
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
