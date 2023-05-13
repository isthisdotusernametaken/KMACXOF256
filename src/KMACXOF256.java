// The cSHAKE256 and bytepad functions are partially based on the example code
// from the course's Canvas page:
// https://canvas.uw.edu/courses/1642546/files/104776117
//
// The remaining methods are based on the pseudocode for the functions of the
// same names (adapted to standard Java naming conventions) given in NIST
// Special Publication 800-185:
// https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf

import java.math.BigInteger;

/**
 * @author Joshua Barbee, James Deal
 */
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

    /**
     * Generates a byte array with contents of X using length w, padded.
     * Adopted from provided project documentation.
     * @param X source data.
     * @param w length specification.
     * @return new byte array.
     */
    static byte[] bytepad(final byte[] X, final int w) {
        final byte[] leftEncodedW = leftEncode(Util.bigIntegerToBytes(BigInteger.valueOf(w)));
        final byte[] z = new byte[((leftEncodedW.length + X.length + (w - 1)) / w) * w];

        System.arraycopy(leftEncodedW, 0, z, 0, leftEncodedW.length);
        System.arraycopy(X, 0, z, leftEncodedW.length, X.length);
        // The final values to the right are already initialized to 0 by default,
        // handling the 0 padding the formal description in NIST SP 800-815 includes

        return z;
    }

    /**
     * Encodes provided string as byte array to be used in KMACX.
     * @param S byte array of string desired.
     * @return encoded byte array.
     */
    static byte[] encodeString(final byte[] S) {
        return Util.cat(
                leftEncode(
                        Util.bigIntegerToBytes(BigInteger.valueOf(S.length * 8L))
                ),
                S
        );
    }

    /**
     * Calls encodeLeftOrRight with goal of encoding source byte array.
     * @param bytes byte array to encode.
     * @return result of encodeLeftOrRight
     */
    static byte[] rightEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, false);
    }

    /**
     * Calls encodeLeftOrRight with goal of encoding source byte array.
     * @param bytes byte array to encode.
     * @return result of encodeLeftOrRight
     */
    static byte[] leftEncode(final byte[] bytes) {
        return encodeLeftOrRight(bytes, true);
    }

    /**
     * Prepends or Appends 0 values to provided byte array.
     * @param bytesOfX source byte array.
     * @param prependLength if encode is left or right aligned.
     * @return result of encoding.
     */
    private static byte[] encodeLeftOrRight(final byte[] bytesOfX, final boolean prependLength) {
        final byte[] bytesOfO = new byte[bytesOfX.length + 1];

        System.arraycopy(bytesOfX, 0, bytesOfO, prependLength ? 1 : 0, bytesOfX.length);
        bytesOfO[prependLength ? 0 : bytesOfO.length - 1] = (byte) bytesOfX.length;

        return bytesOfO;
    }
}
