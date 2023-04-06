import java.util.Arrays;

public class ShaObject {

    /*public static class stInner {
        public byte[] b = new byte[200];
        public long[] q = new long[25];
    }
    public stInner st;*/

    public StateArray sa = new StateArray();
    public int pt;
    public int rsiz;
    public int mdlen;

// update the state with given number of rounds

    private static final long[] keccakFRNDC = {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static final int[] keccakFROTC = {
            1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 2, 14,
            27, 41, 56, 8, 25, 43, 62, 18, 39, 61, 20, 44
    };

    private static final int[] keccakFPILN = {
            10, 7, 11, 17, 18, 3, 5, 16, 8, 21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9, 6, 1
    };

    static void keccakF(final long[] state) {
        // state must be of length 25

        long[] bc = new long[5];

        for (int i = 0; i < 24; i++) {
            theta(state, bc);
            rhoAndPi(state, bc);
            chi(state, bc);
            iota(state, i);
        }
    }

    private static void theta(final long[] state, final long[] bc) {
        for (int i = 0; i < 5; i++)
            bc[i] = state[i] ^ state[i + 5] ^ state[i + 10] ^ state[i + 15] ^ state[i + 20];

        long t;
        for (int i = 0; i < 5; i++) {
            t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);

            for (int j = 0; j < 25; j += 5)
                state[i + j] ^= t;
        }
    }

    private static void rhoAndPi(final long[] state, final long[] bc) {
        long t = state[1];
        for (int i = 0, j; i < 24; i++) {
            j = keccakFPILN[i];
            bc[0] = state[j];
            state[j] = ROTL64(t, keccakFROTC[i]);

            t = bc[0];
        }
    }

    private static void chi(final long[] state, final long[] bc) {
//        for (int i = 0; i < 25; i += 5) {
//            for (int j = 0; j < 5; j++)
//                bc[j] = state[i + j];
//            for (int j = 0; j < 5; j++)
//                state[i + j] ^= ~bc[(i + 1) % 5] & bc[(i + 2) % 5];
//        }

        for (int j = 0; j < 25; j += 5) {
            for (int i = 0; i < 5; i++)
                bc[i] = state[j + i];
            for (int i = 0; i < 5; i++)
                state[j + i] ^= (~bc[(i + 1) % 5]) & bc[(i + 2) % 5];
        }
    }

    private static void iota(final long[] state, final int iteration) {
        state[0] ^= keccakFRNDC[iteration];
    }

    private static long ROTL64(final long x, final long y) {
        return (x << y) | (x >> (64 - y));
    }

    // TODO - Add SHAKE256
    // TODO - Determine meanings of variable names and give new descriptive names

    void shake256_init() {
        sha3_init(32);
    }

    void shake_update(byte[] data, int len) {
        sha3_update(data, len);
    }

    void sha3_init(int mdlen) {
        int i;

        for (i = 0; i < 25; i++)
            this.sa.set64(i,0);
        this.mdlen = mdlen;
        this.rsiz = 200 - 2 * mdlen;
        this.pt = 0;
    }

// update state with more data
    void sha3_update(byte[] data, int len) {
        int j;

        j = this.pt;
        for (int i = 0; i < len; i++) {
            this.sa.set8XOR(j++,data[i]);

            if (j >= this.rsiz) {
                keccakF(this.sa.getArray());
                j = 0;
            }
        }
        this.pt = j;
    }

// finalize and output a hash
    void sha3_final(byte[] md) {
        int i;

        this.sa.set8XOR(this.pt, (byte) 0x06);
        this.sa.set8XOR(this.rsiz-1, (byte) 0x80);
        keccakF(this.sa.getArray());

        for (i = 0; i < this.mdlen; i++) {
            md[i] = this.sa.get8(i);
        }
    }

// SHAKE128 and SHAKE256 extensible-output functionality
    void shake_xof() {
        this.sa.set8XOR(this.pt, (byte) 0x1F);
        this.sa.set8XOR(this.rsiz-1, (byte) 0x80);
        keccakF(this.sa.getArray());
        this.pt = 0;
    }

    void shake_out(byte[] out, int len) {
        int j;

        j = this.pt;
        for (int i = 0; i < len; i++) {
            if (j >= this.rsiz) {
                keccakF(this.sa.getArray());
                j = 0;
            }
            out[i] = this.sa.get8(j++);
        }
        this.pt = j;
    }

// compute a SHA-3 hash (md) of given byte length from "in"
//    byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen) {
//        sha3_ctx_t sha3o = new sha3_ctx_t();
//
//        sha3_init(mdlen);
//        sha3_update(in, inlen);
//        sha3_final(md);
//
//        return md;
//    }

    public static void main(String[] args) {
        String[] testhex = {
                // SHAKE256, message of length 0
                "AB0BAE316339894304E35877B0C28A9B1FD166C796B9CC258A064A8F57E27F2A",
                // SHAKE256, 1600-bit test pattern
                "6A1A9D7846436E4DCA5728B6F760EEF0CA92BF0BE5615E96959D767197A0BEEB"
        };

        ShaObject sha3 = new ShaObject();
        byte[] buf = new byte[32];
        //byte[] ref = new byte[32];

        for (int i = 0; i < 2; i++) {

            sha3.shake256_init();

            if (i >= 1) {                   // 1600-bit test pattern
                Arrays.fill(buf,0,20, (byte) 0b10100011);
                //memset(buf, 0xA3, 20);
                for (int j = 0; j < 200; j += 20)
                    sha3.shake_update(buf,20);
            }

            sha3.shake_xof();

            for (int j = 0; j < 512; j += 32)   // output. discard bytes 0..479
                sha3.shake_out(buf,32);


            UserInterface.printByteArrayAsHex(buf);
            boolean flip = true;
            for (int j = 0; j < testhex[i].length(); j++) {
                System.out.print(testhex[i].charAt(j));
                flip ^= true;
                if (flip) {
                    System.out.print(""+' '+' '); //the most efficient way to print "  "
                }
                if ((j+1)%32 == 0) {
                    System.out.print('\n');
                }
            }
            System.out.println("\nNOU\n");
            // compare to reference
//            test_readhex(ref, testhex[i], ref.length);
//            if (Arrays.compare(buf,ref) != 0) {
//                System.out.printf("[%d] SHAKE%d, len %d test FAILED.\n",
//                        i, i & 1 ? 256 : 128, i >= 2 ? 1600 : 0);
//            }
        }
    }

    // read a hex string, return byte length or -1 on error.
//    static int test_hexdigit(char ch)
//    {
//        if (ch >= '0' && ch <= '9')
//            return  ch - '0';
//        if (ch >= 'A' && ch <= 'F')
//            return  ch - 'A' + 10;
//        if (ch >= 'a' && ch <= 'f')
//            return  ch - 'a' + 10;
//        return -1;
//    }
//
//    static int test_readhex(byte[] buf, String str, int maxbytes)
//    {
//        int i, h, l;
//
//        for (i = 0; i < maxbytes; i++) {
//            h = test_hexdigit(str[2 * i]);
//            if (h < 0)
//                return i;
//            l = test_hexdigit(str[2 * i + 1]);
//            if (l < 0)
//                return i;
//            buf[i] = (h << 4) + l;
//        }
//
//        return i;
//    }
}
    