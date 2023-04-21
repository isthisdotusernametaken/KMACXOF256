import java.nio.ByteBuffer;
import java.nio.LongBuffer;

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
            this.sa.put(j++, temp); // generate data in buffer to provide to keccakf

            if (j >= this.rsiz) {
                Keccak.keccakF(sa.asLongBuffer()); // when buffer full, send to keccakf to hash (xor'd data)
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
}
