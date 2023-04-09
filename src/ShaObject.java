import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class ShaObject {

    /** Current state. */
    public ByteBuffer sa;
    public int pt;
    public int rsiz;
    public int mdlen;

    /** If implementation is cSHAKE. */
    public boolean cShake;

    /**
     * Constructor so that the bytebuffer is built at correct size.
     * @param cS if this object is cSHAKE or SHAKE
     */
    public ShaObject(final boolean cS) {
        this.cShake = cS;
        sa = ByteBuffer.allocate(Long.BYTES * 25);
    }

    void shake256_init() {
        sha3_init(32);
    }

    void shake_update(final byte[] data, final int len) {
        sha3_update(data, len);
    }

    void sha3_init(final int mdl) {
        LongBuffer saAsLong = sa.asLongBuffer();
        for (int i = 0; i < 25; i++) {
            saAsLong.put(i, 0);
        }
        this.mdlen = mdl;
        this.rsiz = 200 - 2 * mdl;
        this.pt = 0;
    }

// update state with more data
    void sha3_update(final byte[] data, final int len) {
        int j;
        j = this.pt;

        for (int i = 0; i < len; i++) {
            byte temp = (byte) (sa.get(j) ^ data[i]);
            this.sa.put(j++, temp);

            if (j >= this.rsiz) {
                Keccak.keccakF(sa.asLongBuffer());
                j = 0;
            }
        }
        this.pt = j;
    }

// finalize and output a hash
    void sha3_final(byte[] md) {
        byte temp = (byte) (sa.get(this.pt) ^ (byte) 0x06);
        this.sa.put(this.pt, temp);

        temp = (byte) (sa.get(this.rsiz - 1) ^ (byte) 0x80);
        this.sa.put(this.rsiz - 1, temp);

        Keccak.keccakF(sa.asLongBuffer());

        for (int i = 0; i < this.mdlen; i++) {
            //md[i] = this.sa.get8(i);
            md[i] = this.sa.get(i);
        }
    }

// SHAKE128 and SHAKE256 extensible-output functionality.
    void shake_xof() {
        byte temp = (byte) (sa.get(this.pt) ^ (byte) (this.cShake ? 0x04 : 0x1F));
        this.sa.put(this.pt, temp);

        temp = (byte) (sa.get(this.rsiz - 1) ^ (byte) 0x80);
        this.sa.put(this.rsiz - 1, temp);

        Keccak.keccakF(sa.asLongBuffer());

        this.pt = 0;
    }

    void shake_out(final byte[] out, final int len) {
        int j;

        j = this.pt;
        for (int i = 0; i < len; i++) {
            if (j >= this.rsiz) {
                Keccak.keccakF(sa.asLongBuffer());
                j = 0;
            }
            out[i] = this.sa.get(j++);
        }
        this.pt = j;
    }

    /**
     * Method that returns shake256 stuff
     * @param x input bitstring
     * @param l lenth of output
     * @param cShake if implementation is cSHAKE or SHAKE
     * @return result of SHAKE256
     */
    public static byte[] shake256(final byte[] x, final int l, final boolean cShake) {
        ShaObject sha3 = new ShaObject(cShake);

        sha3.shake256_init();
        System.out.println("About to Absorb data:");
        UserInterface.printByteArrayAsHex(sha3.sa.array());
        System.out.println("Data to be absorbed:");
        UserInterface.printByteArrayAsHex(x);

        sha3.sha3_update(x, x.length);
        System.out.println("After sha3_update:");
        UserInterface.printByteArrayAsHex(sha3.sa.array());

        sha3.shake_xof();
        System.out.println("After sha3_xof:");
        UserInterface.printByteArrayAsHex(sha3.sa.array());

        byte[] res = new byte[l >>> 3];
        sha3.shake_out(res, l >>> 3);

        return res;
    }

    public static void main(String[] args) {
        ShaObject sha3 = new ShaObject(false);
        byte[] buf = new byte[32];

        for (int i = 0; i < 2; i++) {

            sha3.shake256_init();

            if (i == 1) { // 1600-bit test pattern
                Arrays.fill(buf, 0, 20, (byte) 0b10100011);
                for (int j = 0; j < 200; j += 20)
                    sha3.shake_update(buf, 20);
            }

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X", sha3.sa.get(j))+':');
            } System.out.print("\nAfter message added\n");

            sha3.shake_xof();

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X", sha3.sa.get(j))+':');
            } System.out.print("\nAfter shakexof\n");

            for (int j = 0; j < 512; j += 32)   // output. discard bytes 0..479
                sha3.shake_out(buf,32);

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X", sha3.sa.get(j))+':');
            } System.out.print("\nAfter shakeout\n");

            //TODO debug
            System.out.println("\nENDFOR\n");
        }
    }

}
