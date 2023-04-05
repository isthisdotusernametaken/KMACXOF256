public class Keccak {

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
}
