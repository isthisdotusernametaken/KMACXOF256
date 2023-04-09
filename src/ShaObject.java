import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

public class ShaObject {

    public ByteBuffer sa;
    public int pt;
    public int rsiz;
    public int mdlen;

    /**
     * Constructor so that the bytebuffer is built at correct size.
     */
    public ShaObject() {
        sa = ByteBuffer.allocate(Long.BYTES * 25);
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
        LongBuffer saAsLong = sa.asLongBuffer();
        for (i = 0; i < 25; i++)
            //this.sa.set64(i,0);
            saAsLong.put(i,0);
        this.mdlen = mdlen;
        this.rsiz = 200 - 2 * mdlen;
        this.pt = 0;
    }

// update state with more data
    void sha3_update(byte[] data, int len) {
        int j;

        j = this.pt;
        for (int i = 0; i < len; i++) {
            //this.sa.set8XOR(j++,data[i]);
            byte temp = (byte) (sa.get(i) ^ data[i]);
            this.sa.put(j++,temp);

            if (j >= this.rsiz) {
                //Keccak.keccakF(this.sa.getArray());
                Keccak.keccakF(sa.asLongBuffer());
                j = 0;
            }
        }
        this.pt = j;
    }

// finalize and output a hash
    void sha3_final(byte[] md) {
        int i;

        //this.sa.set8XOR(this.pt, (byte) 0x06);
        byte temp = (byte) (sa.get(this.pt) ^ (byte) 0x06);
        this.sa.put(this.pt,temp);

        //this.sa.set8XOR(this.rsiz-1, (byte) 0x80);
        temp = (byte) (sa.get(this.rsiz-1) ^ (byte) 0x80);
        this.sa.put(this.rsiz-1,temp);

        Keccak.keccakF(sa.asLongBuffer());

        for (i = 0; i < this.mdlen; i++) {
            //md[i] = this.sa.get8(i);
            md[i] = this.sa.get(i);
        }
    }

// SHAKE128 and SHAKE256 extensible-output functionality
    void shake_xof() {
        //this.sa.set8XOR(this.pt, (byte) 0x1F);
        byte temp = (byte) (sa.get(this.pt) ^ (byte) 0x1F);
        this.sa.put(this.pt,temp);

        //this.sa.set8XOR(this.rsiz-1, (byte) 0x80);
        temp = (byte) (sa.get(this.rsiz-1) ^ (byte) 0x80);
        this.sa.put(this.rsiz-1,temp);

        Keccak.keccakF(sa.asLongBuffer());

        //TODO debug
        for (int i = 0; i < sa.capacity(); i++) {
            System.out.print(String.format("%02X ", sa.get(i))+':');
        } System.out.print("\nXOF1\n");

        this.pt = 0;
    }

    void shake_out(byte[] out, int len) {
        int j;

        j = this.pt;
        for (int i = 0; i < len; i++) {
            if (j >= this.rsiz) {
                Keccak.keccakF(sa.asLongBuffer());
                j = 0;
            }
            //out[i] = this.sa.get8(j++);
            out[i] = this.sa.get(j++);
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

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X ", sha3.sa.get(j))+':');
            } System.out.print("\n0's complete\n");

            if (i >= 1) {                   // 1600-bit test pattern
                Arrays.fill(buf, 0, 20, (byte) 0b10100011);
                //memset(buf, 0xA3, 20);
                for (int j = 0; j < 200; j += 20)
                    sha3.shake_update(buf, 20);
            }

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X ", sha3.sa.get(j))+':');
            } System.out.print("\nAfter message added\n");

            sha3.shake_xof();

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X ", sha3.sa.get(j))+':');
            } System.out.print("\nAfter shakexof\n");

            for (int j = 0; j < 512; j += 32)   // output. discard bytes 0..479
                sha3.shake_out(buf,32);

            //TODO debug
            for (int j = 0; j < sha3.sa.capacity(); j++) {
                System.out.print(String.format("%02X", sha3.sa.get(j))+':');
            } System.out.print("\nAfter shakeout\n");

            //TODO debug
//            UserInterface.printByteArrayAsHex(buf);
//            boolean flip = true;
//            for (int j = 0; j < testhex[i].length(); j++) {
//                System.out.print(testhex[i].charAt(j));
//                flip ^= true;
//                if (flip) {
//                    System.out.print(""+' '+' '); //the most efficient way to print "  "
//                }
//                if ((j+1)%32 == 0) {
//                    System.out.print('\n');
//                }
//            }

            //TODO debug
            System.out.println("\nENDFOR\n");
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
    