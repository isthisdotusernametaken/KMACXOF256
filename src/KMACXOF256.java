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

    /**
     * Function that does all the work, probably.
     * @param K key bit string.
     * @param X main input bit string.
     * @param L integer representing desired output length in bits.
     * @param S optional customization bitstring, if no customization desired, S is empty string.
     * @return result of math.
     */
    static byte[] runKMACXOF256(final byte[] K, final byte[] X, final int L, final byte[] S) {
        byte[] newX = Util.cat(bytepad(encodeString(K), 136), X, rightEncode(new byte[]{0}));
        return newX; //TODO swap to returning cSHAKE256(newXm L, "KMAC", S);
    }

    static byte[] bytepad(final byte[] X, final int w) {
        final byte[] leftEncodedW = leftEncode(BigInteger.valueOf(w).toByteArray());
        final byte[] z = new byte[((leftEncodedW.length + X.length + (w - 1)) / w) * w];

        System.arraycopy(leftEncodedW, 0, z, 0, leftEncodedW.length);
        System.arraycopy(X, 0, z, leftEncodedW.length, X.length);
        // The final values to the right are already initialized to 0 by default,
        // handling the 0 padding the formal description in NIST SP 800-815 includes

        return z;
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
        reverseByteEndianness(bytesOfX);

        final byte[] bytesOfO = new byte[bytesOfX.length + 1];
        System.arraycopy(bytesOfX, 0, bytesOfO, prependLength ? 1 : 0, bytesOfX.length);
        bytesOfO[prependLength ? 0 : bytesOfO.length - 1] = enc8((byte) bytesOfX.length);

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
