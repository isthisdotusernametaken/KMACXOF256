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
        byte[] lengthAsBytes = BigInteger.valueOf(S.length * 8L).toByteArray();

        if (lengthAsBytes[0] == 0) {
            final byte[] withoutByteForSignBit = new byte[lengthAsBytes.length - 1];
            System.arraycopy(lengthAsBytes, 1, withoutByteForSignBit, 0, withoutByteForSignBit.length);
            lengthAsBytes = withoutByteForSignBit;
        }

        return Util.cat(leftEncode(lengthAsBytes), S);
    }

    static byte[] rightEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, false);
    }

    static byte[] leftEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, true);
    }

    private static byte[] encodeLeftOrRight(final byte[] bytesOfX, final boolean prependLength) {
        final byte[] bytesOfO = new byte[bytesOfX.length + 1];

        System.arraycopy(bytesOfX, 0, bytesOfO, prependLength ? 1 : 0, bytesOfX.length);
        bytesOfO[prependLength ? 0 : bytesOfO.length - 1] = (byte) bytesOfX.length;

        return bytesOfO;
    }
}
