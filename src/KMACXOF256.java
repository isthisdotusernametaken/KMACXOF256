import java.math.BigInteger;

public class KMACXOF256 {

    /**
     * KMACXOF256 implementation.
     * @param K key bit string.
     * @param X main input bit string.
     * @param L integer representing desired output length in bits.
     * @param S optional customization bitstring, if no customization desired, S is empty string.
     * @return result of math.
     */
    static byte[] runKMACXOF256(final byte[] K, final byte[] X, final int L, final String S) {
        byte[] newX = Util.cat(bytepad(encodeString(K), 136), X, rightEncode(new byte[]{0}));
        return cSHAKE256(newX, L, "KMAC", S);
    }

    /**
     * TODO This is debug for cSHAKE256 vs expected output documentation.
     * @param args unused
     */
    public static void main(String[] args) {
        byte[] d = {0, 1, 2, 3};
        byte[] res = cSHAKE256(d, 512, "", "Email Signature");
        UserInterface.printByteArrayAsHex(res);
    }

    static byte[] cSHAKE256(final byte[] X, final int L, final String N, final String S) {
        if (N.isEmpty() && S.isEmpty()) {
            return ShaObject.shake256(X, L, false);
        }
        //return KECCAK[512](bytepad(encode_string(N) || encode_string(S), 136) || X || 00, L).
        BigInteger bi = new BigInteger(Util.cat(
                bytepad(Util.cat(encodeString(Util.ASCIIStringToBytes(N)),
                        encodeString(Util.ASCIIStringToBytes(S))), 136), X)).shiftLeft(2);
        return ShaObject.shake256(Util.bigIntegerToBytes(bi), L, true);
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
        return Util.cat(
                leftEncode(
                        Util.bigIntegerToBytes(BigInteger.valueOf(S.length * 8L))
                ),
                S
        );
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
