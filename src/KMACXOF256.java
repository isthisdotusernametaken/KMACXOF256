import java.math.BigInteger;
import java.util.Arrays;

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
        //Required things to make tests happen.
        ShaObject sha = new ShaObject(true);
        byte[] d = {0, 1, 2, 3};
        String N = "";
        String S = "Email Signature";
        int L = 512;

        //Encoded bytepad of N and S strings.
        //TODO this is not 200 length per spec, but when i set it to correct length, wrong output
        byte[] temp = bytepad(Util.cat(encodeString(Util.ASCIIStringToBytes(N)),
                        encodeString(Util.ASCIIStringToBytes(S))),136);

        //makes temp 200 length
//        temp = Util.cat(temp,new byte[64]);
//        UserInterface.printByteArrayAsHex(temp);
//        System.out.println(temp.length);

        System.out.println("TEST 3:");

        //1. init
        sha.shake256_init();

        System.out.println("About to Absorb data NS:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //2. feed in bytepad NS
        sha.sha3_update(temp,temp.length); //correct data, wrong length, but correct length makes output bad

        System.out.println("After Update NS:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //3. feed bytepad: data
        //TODO this is not the correct data but produces correct output LOL
        sha.sha3_update(d,d.length); //data given: {0,1,2,3}, should be: {0,1,2,3,4,0,...,80,...,0} 200 length.

        System.out.println("After Update data:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //4. xof
        sha.shake_xof(); //just kinda put this here, and it seems to do good stuff.

        System.out.println("After XOF:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //5. outval
        byte[] res = new byte[L >>> 3];
        sha.shake_out(res, L >>> 3);

        System.out.println("Outval:");
        UserInterface.printByteArrayAsHex(res);

        /*
        spacer comment so it's easier to spot test 4.
        Below is not as commented as above, but it's all the same ideas, except with multiple data rounds.
         */

        System.out.println("\n\nTEST 4:");
        sha = new ShaObject(true);

        //1. init
        sha.shake256_init();

        System.out.println("About to Absorb data NS:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //2. feed in bytepad NS
        sha.sha3_update(temp,temp.length);

        System.out.println("After Update NS:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //3. feed bytepad: data: part 1
        //hardcoded data generation
        d = new byte[200];
        for (int i = 0; i < 136; i++) {
            d[i] = (byte) i;
        }
        sha.sha3_update(d,d.length);

        System.out.println("After Update data 1:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //4. feed bytepad: data: part 2
        //hardcoded data generation
        d = new byte[200];
        for (int i = 136; i < 200; i++) {
            d[i-136] = (byte) i;
        }
        d[64] = (byte) 4;
        d[135] = (byte) 128;
        sha.sha3_update(d,d.length);

        System.out.println("After Update data 2:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //5. xof
        sha.shake_xof();

        System.out.println("After XOF:");
        UserInterface.printByteArrayAsHex(sha.sa.array());

        //6. outval
        res = new byte[L >>> 3];
        sha.shake_out(res, L >>> 3);

        System.out.println("Outval:");
        UserInterface.printByteArrayAsHex(res);

        /*
        Old.
         */
//        byte[] res = cSHAKE256(d, 512, "", "Email Signature");
//        System.out.println("Outval is:");
//        UserInterface.printByteArrayAsHex(res);
//
//        System.out.println("\n\nTest Case #4:");
//        d = new byte[200];
//        for (int i = 0; i < 200; i++) {
//            d[i] = (byte) i;
//        }
//        res = cSHAKE256(d, 512, "", "Email Signature");
//        System.out.println("Outval is:");
//        UserInterface.printByteArrayAsHex(res);
    }

    static byte[] cSHAKE256(final byte[] X, final int L, final String N, final String S) {
        if (N.isEmpty() && S.isEmpty()) {
            return ShaObject.shake256(X, L, false);
        }
        //return KECCAK[512](bytepad(encode_string(N) || encode_string(S), 136) || X || 00, L).
        BigInteger bi = new BigInteger(Util.cat(
                bytepad(
                        Util.cat(
                                encodeString(Util.ASCIIStringToBytes(N)),
                                encodeString(Util.ASCIIStringToBytes(S))
                        ),
                        136
                ),
                X
        )).shiftLeft(2);

        //TODO debug
        System.out.println("N as bytes:");
        UserInterface.printByteArrayAsHex(Util.ASCIIStringToBytes(N));
        System.out.println("Encoded N:");
        UserInterface.printByteArrayAsHex(encodeString(Util.ASCIIStringToBytes(N)));
        System.out.println("Encoded S:");
        UserInterface.printByteArrayAsHex(encodeString(Util.ASCIIStringToBytes(S)));
        System.out.println("bytepad data:");
        UserInterface.printByteArrayAsHex(bytepad(Util.cat(encodeString(Util.ASCIIStringToBytes(N)),
                encodeString(Util.ASCIIStringToBytes(S))), 136));
        System.out.println("BI to bytes:");
        UserInterface.printByteArrayAsHex(Util.bigIntegerToBytes(bi));

        return ShaObject.shake256(Util.bigIntegerToBytes(bi), L, true);
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
