// This class' methods and other content for the Keccak-f function are directly
// adapted from Markku-Juhani Saarinen’s C implementation:
// https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c

import java.nio.LongBuffer;

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

    static void keccakF(final LongBuffer state) {
        // state must be of length 25
        long[] bc = new long[5];
        reverseByteOrderInLong(state);

        for (int i = 0; i < 24; i++) {
            theta(state, bc);
            rhoAndPi(state, bc);
            chi(state, bc);
            iota(state, i);
        }
        reverseByteOrderInLong(state);
    }

    private static void reverseByteOrderInLong(final LongBuffer state) {
        for (int i = 0; i < state.capacity(); i++) {
            state.put(
                    i,
                    ((state.get(i) & 0x0000_0000_0000_00FFL) << 56) |
                    ((state.get(i) & 0x0000_0000_0000_FF00L) << 40) |
                    ((state.get(i) & 0x0000_0000_00FF_0000L) << 24) |
                    ((state.get(i) & 0x0000_0000_FF00_0000L) << 8) |
                    ((state.get(i) & 0x0000_00FF_0000_0000L) >>> 8) |
                    ((state.get(i) & 0x0000_FF00_0000_0000L) >>> 24) |
                    ((state.get(i) & 0x00FF_0000_0000_0000L) >>> 40) |
                    ((state.get(i) & 0xFF00_0000_0000_0000L) >>> 56)
            );
        }
    }

    private static void theta(final LongBuffer state, final long[] bc) {
        for (int i = 0; i < 5; i++)
            bc[i] = state.get(i) ^ state.get(i + 5) ^ state.get(i + 10) ^ state.get(i + 15) ^ state.get(i + 20);

        long t;
        for (int i = 0; i < 5; i++) {
            t = bc[(i + 4) % 5] ^ ROTL64(bc[(i + 1) % 5], 1);

            for (int j = 0; j < 25; j += 5)
                state.put(i + j, state.get(i + j) ^ t);
        }
    }

    private static void rhoAndPi(final LongBuffer state, final long[] bc) {
        long t = state.get(1);
        for (int i = 0, j; i < 24; i++) {
            j = keccakFPILN[i];
            bc[0] = state.get(j);
            state.put(j, ROTL64(t, keccakFROTC[i]));

            t = bc[0];
        }
    }

    private static void chi(final LongBuffer state, final long[] bc) {
        for (int i = 0; i < 25; i += 5) {
            for (int j = 0; j < 5; j++)
                bc[j] = state.get(i + j);
            for (int j = 0; j < 5; j++)
                state.put(i + j, state.get(i + j) ^ (~bc[(j + 1) % 5] & bc[(j + 2) % 5]));
        }
    }

    private static void iota(final LongBuffer state, final int iteration) {
        state.put(0, state.get(0) ^ keccakFRNDC[iteration]);
    }

    private static long ROTL64(final long x, final long y) {
        return (x << y) | (x >>> (64 - y));
    }
}
