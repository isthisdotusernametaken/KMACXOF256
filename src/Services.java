// The following methods for the required cryptographic functionalities are
// adapted directly from the pseudocode given for these features in the
// assignment description.

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author Joshua Barbee
 */
public class Services {

    private static final SecureRandom RANDOM = new SecureRandom();

    static byte[] cryptographicHash(final byte[] data) {
        return KMACXOF256.runKMACXOF256(Util.ASCIIStringToBytes(""), data, 512, "D");
    }

    static byte[] authenticationTag(final byte[] data, final byte[] pw) {
        return KMACXOF256.runKMACXOF256(pw, data, 512, "T");
    }

    static SymmetricCryptogram encrypt(final byte[] m, final byte[] pw) {
        final byte[] z = new byte[64];
        RANDOM.nextBytes(z);

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
