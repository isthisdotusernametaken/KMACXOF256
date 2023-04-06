public class TinySha {

// sha3.c
// 19-Nov-11  Markku-Juhani O. Saarinen <mjos@iki.fi>

// Revised 07-Aug-15 to match with official release of FIPS PUB 202 "SHA3"
// Revised 03-Sep-15 for portability + OpenSSL - style API

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
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
    };

    private static final int[] keccakFPILN = {
            10, 7,  11, 17, 18, 3, 5,  16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
    };

    // TODO - Add SHAKE256

    // TODO - Determine meanings of variable names and give new descriptive names
    static void keccakF(final long[] state) {
        // state must be of length 24

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
            bc[i] = state[i] ^ state[i + 5] ^ state[i + 15] ^ state[i + 20];

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
        for (int i = 0; i < 25; i += 5) {
            for (int j = 0; j < 5; j++)
                bc[i] = state[i + j];
            for (int j = 0; j < 5; j++)
                state[i + j] ^= ~bc[(i + 1) % 5] & bc[(i + 2) % 5];
        }
    }

    private static void iota(final long[] state, final int iteration) {
        state[0] ^= keccakFRNDC[iteration];
    }

    private static long ROTL64(final long x, final long y) {
        return (x << y) | (x >> (64 - y));
    }

// Initialize the context for SHA3

    int sha3_init(sha3_ctx_t c, int mdlen)
    {
        int i;

        for (i = 0; i < 25; i++)
            c.st.q[i] = 0;
        c.mdlen = mdlen;
        c.rsiz = 200 - 2 * mdlen;
        c.pt = 0;

        return 1;
    }

// update state with more data

    int sha3_update(sha3_ctx_t c, byte[] data, int len)
    {
        int j;

        j = c.pt;
        for (int i = 0; i < len; i++) {
            c.st.b[j++] ^= data[i];
            if (j >= c.rsiz) {
                keccakF(c.st.q);
                j = 0;
            }
        }
        c.pt = j;

        return 1;
    }

// finalize and output a hash

    int sha3_final(byte[] md, sha3_ctx_t c)
    {
        int i;

        c.st.b[c.pt] ^= 0x06;
        c.st.b[c.rsiz - 1] ^= 0x80;
        keccakF(c.st.q);

        for (i = 0; i < c.mdlen; i++) {
            md[i] = c.st.b[i];
        }

        return 1;
    }

// compute a SHA-3 hash (md) of given byte length from "in"

    byte[] sha3(byte[] in, int inlen, byte[] md, int mdlen)
    {
        sha3_ctx_t sha3o = new sha3_ctx_t();

        sha3_init(sha3o, mdlen);
        sha3_update(sha3o, in, inlen);
        sha3_final(md, sha3o);

        return md;
    }

// SHAKE128 and SHAKE256 extensible-output functionality

    void shake_xof(sha3_ctx_t c)
    {
        c.st.b[c.pt] ^= 0x1F;
        c.st.b[c.rsiz - 1] ^= 0x80;
        keccakF(c.st.q);
        c.pt = 0;
    }

    void shake_out(sha3_ctx_t c, byte[] out, int len)
    {
        int j;

        j = c.pt;
        for (int i = 0; i < len; i++) {
            if (j >= c.rsiz) {
                keccakF(c.st.q);
                j = 0;
            }
            out[i] = c.st.b[j++];
        }
        c.pt = j;
    }

    public class  sha3_ctx_t {
        public class stInner {
            public byte[] b = new byte[200];
            public long[] q = new long[25];
        }
        public stInner st;
        public int pt;
        public int rsiz;
        public int mdlen;
    }

}
