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
     * Example usage of cSHAKE without KMACXOF256 involved.
     * Tests run are examples 3 and 4 from provided NIST test vector documentation.
     * @param args unused
     */
    public static void main(final String[] args) {
        //Required things to make tests happen.
        ShaObject sha = new ShaObject(true);
        byte[] d = {0, 1, 2, 3};
        String N = "";
        String S = "Email Signature";
        int L = 512;

        System.out.println("T3:");
        UserInterface.printByteArrayAsHex(cSHAKE256(d, L, N, S));

        d = new byte[200];
        for (int i = 0; i < 200; i++) {
            d[i] = (byte) i;
        }

        System.out.println("\n\nT4:");
        UserInterface.printByteArrayAsHex(cSHAKE256(d, L, N, S));

    }

    /**
     * Handles cSHAKE256 implementation, if N&S are empty uses SHAKE256 instead as per spec.
     * @param X data input as byte array.
     * @param L desired output bit-length from cSHAKE.
     * @param N Name of calling function.
     * @param S Customization bitstring.
     * @return byte array of encoded data.
     */
    static byte[] cSHAKE256(final byte[] X, final int L, final String N, final String S) {
        //SHAKE if (N.isEmpty() && S.isEmpty()), cSHAKE if !(N.isEmpty() && S.isEmpty())
        ShaObject sha = new ShaObject(!(N.isEmpty() && S.isEmpty()));

        byte[] nsbytepad = bytepad(Util.cat(encodeString(Util.ASCIIStringToBytes(N)),
                encodeString(Util.ASCIIStringToBytes(S))), 136);

        sha.shake256_init();
        sha.sha3_update(nsbytepad, nsbytepad.length);
        sha.sha3_update(X, X.length);
        sha.shake_xof();
        byte[] res = new byte[L >>> 3];
        sha.shake_out(res, L >>> 3);

        return res;
    }

    static byte[] bytepad(final byte[] X, final int w) {
        final byte[] leftEncodedW = leftEncode(Util.bigIntegerToBytes(BigInteger.valueOf(w)));
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
