public class Services {
    static byte[] cryptographicHash(final byte[] data) {
        return KMACXOF256.runKMACXOF256(Util.ASCIIStringToBytes(""), data, 512, "D");
    }

    static byte[] authenticationTag(final byte[] data, final byte[] passphrase) {
        return KMACXOF256.runKMACXOF256(passphrase, data, 512, "T");
    }


}
