import java.security.SecureRandom;

public class Services {

    private static final SecureRandom RANDOM = new SecureRandom();

    static byte[] cryptographicHash(final byte[] data) {
        return KMACXOF256.runKMACXOF256(Util.ASCIIStringToBytes(""), data, 512, "D");
    }

    static byte[] authenticationTag(final byte[] data, final byte[] passphrase) {
        return KMACXOF256.runKMACXOF256(passphrase, data, 512, "T");
    }

    static SymmetricCryptogram encrypt(final byte[] data, final byte[] passphrase) {
        final byte[] z = new byte[64];
        RANDOM.nextBytes(z);

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

        final byte[] c = Util.xorByteArrays(
                KMACXOF256.runKMACXOF256(keAndKa[0], new byte[0], data.length, "SKE"),
                data
        );

        final byte[] t = KMACXOF256.runKMACXOF256(keAndKa[1], data, 512, "SKA");

        return new SymmetricCryptogram(z, c, t);
    }
}
