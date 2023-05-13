// The following methods for the required cryptographic functionalities are
// adapted directly from the pseudocode given for these features in the
// assignment description.

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author Joshua Barbee
 */
public class Services {

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
        final var k = new BigInteger(448, Util.RANDOM).shiftLeft(2);
        final var W = V.publicMultiply(k);
        final var Z = Ed448GoldilocksPoint.G.publicMultiply(k);

        final byte[][] cAndT = cAndTOrMAndTPrimeAsymm(W.x, m, true);

        return new DHIESCryptogram(Z, cAndT[0], cAndT[1]);
    }

    static boolean decryptAsymm(final byte[][] mOut, final DHIESCryptogram cryptogram, final byte[] pw) {
        var s = calculateSFromPw(pw);
        var W = cryptogram.Z().publicMultiply(s);

        final byte[][] mAndTPrime = cAndTOrMAndTPrimeAsymm(W.x, cryptogram.c(), false);

        return checkDecryption(mOut, cryptogram.t(), mAndTPrime);
    }

    private static byte[][] cAndTOrMAndTPrimeAsymm(final BigInteger Wx, final byte[] mOrC, final boolean encrypting) {
        return cAndTOrMAndTPrime(
                Util.bigIntegerToBytes(Wx), mOrC, encrypting,
                "PK", "PKE", "PKA"
        );
    }

    private static byte[][] cAndTOrMAndTPrimeSymm(final byte[] z, final byte[] passphrase, final byte[] mOrC, final boolean encrypting) {
        return cAndTOrMAndTPrime(
                Util.cat(z, passphrase), mOrC, encrypting,
                "S", "SKE", "SKA"
        );
    }

    private static byte[][] cAndTOrMAndTPrime(final byte[] KMACKey, final byte[] mOrC, final boolean encrypting,
                                              final String keAndKaCust, final String mOrCCust, final String tOrTPrimeCust) {
        final byte[][] keAndKa = new byte[2][];
        Util.split(
                keAndKa,
                KMACXOF256.runKMACXOF256(KMACKey, new byte[0], 1024, keAndKaCust)
        );

        final byte[] cOrM = Util.xorByteArrays(
                KMACXOF256.runKMACXOF256(keAndKa[0], new byte[0], mOrC.length * 8, mOrCCust),
                mOrC
        );
        final byte[] tOrTPrime = KMACXOF256.runKMACXOF256(keAndKa[1], encrypting ? mOrC : cOrM, 512, tOrTPrimeCust);

        return new byte[][]{cOrM, tOrTPrime};
    }

    private static BigInteger calculateSFromPw(final byte[] pw) {
        return new BigInteger(
                KMACXOF256.runKMACXOF256(pw, new byte[0], 512, "SK")
        ).shiftLeft(2);
    }

    private static boolean checkDecryption(final byte[][] mOut, final byte[] t, final byte[][] mAndTPrime) {
        if (Arrays.equals(mAndTPrime[1], t)) {
            mOut[0] = mAndTPrime[0];
            return true;
        }
        return false;
    }
}
