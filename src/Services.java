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

    static SymmetricCryptogram encrypt(final byte[] m, final byte[] pw) {
        final byte[] z = new byte[64];
        Util.RANDOM.nextBytes(z);

        final byte[][] keAndKa = calculateKeAndKa(z, pw);
        final byte[] c = calculateCOrM(keAndKa[0], m);
        final byte[] t = calculateT(keAndKa[1], m);

        return new SymmetricCryptogram(z, c, t);
    }

    static boolean decrypt(final byte[][] mOut, final SymmetricCryptogram cryptogram, final byte[] pw) {
        final byte[][] keAndKa = calculateKeAndKa(cryptogram.z(), pw);
        final byte[] m = calculateCOrM(keAndKa[0], cryptogram.c());
        final byte[] tPrime = calculateT(keAndKa[1], m);

        if (Arrays.equals(tPrime, cryptogram.t())) {
            mOut[0] = m;
            return true;
        }
        return false;
    }

//    static EllipticKeyPair generateKeyPair(final byte[] pw) {
//        var s = new BigInteger(
//                KMACXOF256.runKMACXOF256(pw, new byte[0], 512, "SK")
//        ).shiftLeft(2);
//        var V = Ed448GoldilocksPoint.G.privateMultiply(s);
//
//        return new EllipticKeyPair(Util.bigIntegerToBytes(s), V);
//    }
//
//    static DHIESCryptogram encryptDHIES(final byte[] m, final Ed448GoldilocksPoint V) {
//        final byte[] kBytes = new byte[56];
//        Util.RANDOM.nextBytes(kBytes);
//        var k = new BigInteger(kBytes);
//
//        var W =
//
//        final byte[][] keAndKa = calculateKeAndKa(z, pw);
//        final byte[] c = calculateCOrM(keAndKa[0], m);
//        final byte[] t = calculateT(keAndKa[1], m);
//
//        return new SymmetricCryptogram(z, c, t);
//    }

    private static byte[][] calculateKeAndKa(final byte[] z, final byte[] passphrase) {
        final byte[][] keAndKa = new byte[2][];
        Util.split(
                keAndKa,
                KMACXOF256.runKMACXOF256(
                        Util.cat(z, passphrase),
                        new byte[0],
                        1024,
                        "S"
                )
        );

        return keAndKa;
    }

    private static byte[] calculateCOrM(final byte[] ke, final byte[] mOrC) {
        return Util.xorByteArrays(
                KMACXOF256.runKMACXOF256(ke, new byte[0], mOrC.length * 8, "SKE"),
                mOrC
        );
    }

    private static byte[] calculateT(final byte[] ka, final byte[] m) {
        return KMACXOF256.runKMACXOF256(ka, m, 512, "SKA");
    }
}
