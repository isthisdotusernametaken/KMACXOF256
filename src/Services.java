// The following methods for the required cryptographic functionalities are
// adapted directly from the pseudocode given for these features in the
// assignment description.

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author Joshua Barbee
 * @author James Deal
 */
public class Services {

    /** The value 4. This is sourced from the natural world where 4 is an excellent number. */
    private static final BigInteger FOUR = BigInteger.valueOf(4);

    static byte[] cryptographicHash(final byte[] data) {
        return KMACXOF256.runKMACXOF256(new byte[0], data, 512, "D");
    }

    static byte[] authenticationTag(final byte[] data, final byte[] pw) {
        return KMACXOF256.runKMACXOF256(pw, data, 512, "T");
    }

    static SymmetricCryptogram encryptSymm(final byte[] m, final byte[] pw) {
        final byte[] z = new byte[64];
        Util.RANDOM.nextBytes(z);

        final byte[][] cAndT = cAndTOrMAndTPrimeSymm(z, pw, m, true);

        return new SymmetricCryptogram(z, cAndT[0], cAndT[1]);
    }

    static boolean decryptSymm(final byte[][] mOut, final SymmetricCryptogram cryptogram, final byte[] pw) {
        final byte[][] mAndTPrime = cAndTOrMAndTPrimeSymm(cryptogram.z(), pw, cryptogram.c(), false);

        return checkDecryption(mOut, cryptogram.t(), mAndTPrime);
    }

    static EllipticKeyPair generateKeyPair(final byte[] pw) {
        final var s = calculateSFromPw(pw);
        final var V = Ed448GoldilocksPoint.G.publicMultiply(s); // USE PRIVATE MULTIPLY IF AVAILABLE

        return new EllipticKeyPair(Util.bigIntegerToBytes(s), V);
    }

    static DHIESCryptogram encryptAsymm(final byte[] m, final Ed448GoldilocksPoint V) {
        final var k = ModR.mult(new BigInteger(448, Util.RANDOM), FOUR);
        final var W = V.publicMultiply(k);
        final var Z = Ed448GoldilocksPoint.G.publicMultiply(k);

        final byte[][] cAndT = cAndTOrMAndTPrimeAsymm(W.x, m, true);

        return new DHIESCryptogram(Z, cAndT[0], cAndT[1]);
    }

    static boolean decryptAsymm(final byte[][] mOut, final DHIESCryptogram cryptogram, final byte[] pw) {
        final var s = calculateSFromPw(pw);
        final var W = cryptogram.Z().publicMultiply(s);

        final byte[][] mAndTPrime = cAndTOrMAndTPrimeAsymm(W.x, cryptogram.c(), false);

        return checkDecryption(mOut, cryptogram.t(), mAndTPrime);
    }

    /**
     * Sign a source using Dhies/Schnorr.
     * @param m Contents of the source to be signed as a byte array.
     * @param pw Passpharse to use when signing as byte array.
     * @return Schnorr signature of file.
     */
    static SchnorrSignature signFile(final byte[] m, final byte[] pw) {
        BigInteger s = calculateSFromPw(pw);
        BigInteger k = ModR.mult(new BigInteger(KMACXOF256.runKMACXOF256(s.toByteArray(), m, 512, "N")), FOUR);
        Ed448GoldilocksPoint U = Ed448GoldilocksPoint.G.publicMultiply(k);
        BigInteger h = new BigInteger(KMACXOF256.runKMACXOF256(U.x.toByteArray(), m, 512, "T")).mod(ModR.r);
        BigInteger z = ModR.sub(k, ModR.mult(h, s));
        return new SchnorrSignature(h.toByteArray(), z.toByteArray());
    }

    /**
     * Verify signature against given file.
     * @param hz Given verified signature.
     * @param V Public key.
     * @param m Source data.
     * @return If signature is valid against source data.
     */
    static boolean verifySignature(final SchnorrSignature hz, final Ed448GoldilocksPoint V, final byte[] m) {
        Ed448GoldilocksPoint U = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger(hz.z()))
                .add(V.publicMultiply(new BigInteger(hz.h()))); //the modR is here in spirit
        byte[] res = new BigInteger(KMACXOF256.runKMACXOF256(U.x.toByteArray(), m, 512, "T")).mod(ModR.r).toByteArray();
        return Arrays.equals(res, hz.h());
    }

    private static byte[][] cAndTOrMAndTPrimeAsymm(final BigInteger Wx, final byte[] mOrC, final boolean encrypting) {
        return cAndTOrMAndTPrime(
                Util.bigIntegerToBytes(Wx), mOrC, encrypting,
                "PK", "PKE", "PKA",
                false
        );
    }

    private static byte[][] cAndTOrMAndTPrimeSymm(final byte[] z, final byte[] passphrase, final byte[] mOrC, final boolean encrypting) {
        return cAndTOrMAndTPrime(
                Util.cat(z, passphrase), mOrC, encrypting,
                "S", "SKE", "SKA",
                true
        );
    }

    private static byte[][] cAndTOrMAndTPrime(final byte[] KMACKey, final byte[] mOrC, final boolean encrypting,
                                              final String keAndKaCust, final String mOrCCust, final String tOrTPrimeCust,
                                              final boolean keLeft) {
        final byte[][] keAndKa = new byte[2][];
        Util.split(
                keAndKa,
                KMACXOF256.runKMACXOF256(KMACKey, new byte[0], 1024, keAndKaCust)
        );

        final byte[] cOrM = Util.xorByteArrays(
                KMACXOF256.runKMACXOF256(keAndKa[keLeft ? 0 : 1], new byte[0], mOrC.length * 8, mOrCCust),
                mOrC
        );
        final byte[] tOrTPrime = KMACXOF256.runKMACXOF256(keAndKa[keLeft ? 1 : 0], encrypting ? mOrC : cOrM, 512, tOrTPrimeCust);

        return new byte[][]{cOrM, tOrTPrime};
    }

    private static BigInteger calculateSFromPw(final byte[] pw) {
        return ModR.mult(
                new BigInteger(
                KMACXOF256.runKMACXOF256(pw, new byte[0], 512, "SK")
                ),
                FOUR
        );
    }

    private static boolean checkDecryption(final byte[][] mOut, final byte[] t, final byte[][] mAndTPrime) {
        if (Arrays.equals(mAndTPrime[1], t)) {
            mOut[0] = mAndTPrime[0];
            return true;
        }
        return false;
    }
}
