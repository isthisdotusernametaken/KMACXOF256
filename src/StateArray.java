public class StateArray {

    private static final long KEEP_LAST_BYTE = 0x0000_0000_0000_00FFL;

    private static final long ZERO_BYTE_7 = 0xFFFF_FFFF_FFFF_FF00L;
    private static final long ZERO_BYTE_6 = 0xFFFF_FFFF_FFFF_00FFL;
    private static final long ZERO_BYTE_5 = 0xFFFF_FFFF_FF00_FFFFL;
    private static final long ZERO_BYTE_4 = 0xFFFF_FFFF_00FF_FFFFL;
    private static final long ZERO_BYTE_3 = 0xFFFF_FF00_FFFF_FFFFL;
    private static final long ZERO_BYTE_2 = 0xFFFF_00FF_FFFF_FFFFL;
    private static final long ZERO_BYTE_1 = 0xFF00_FFFF_FFFF_FFFFL;
    private static final long ZERO_BYTE_0 = 0x00FF_FFFF_FFFF_FFFFL;

    private final long[] state = new long[25];

    byte get8(final int index8) {
        int index64 = index8 / 8;

        return switch (index8 % 8) {
            case 7 -> (byte) (state[index64] & KEEP_LAST_BYTE);
            case 6 -> (byte) ((state[index64] >>> 8) & KEEP_LAST_BYTE);
            case 5 -> (byte) ((state[index64] >>> 16) & KEEP_LAST_BYTE);
            case 4 -> (byte) ((state[index64] >>> 24) & KEEP_LAST_BYTE);
            case 3 -> (byte) ((state[index64] >>> 32) & KEEP_LAST_BYTE);
            case 2 -> (byte) ((state[index64] >>> 40) & KEEP_LAST_BYTE);
            case 1 -> (byte) ((state[index64] >>> 48) & KEEP_LAST_BYTE);
            default -> (byte) ((state[index64] >>> 56) & KEEP_LAST_BYTE); // Always 0 in this case
        };
    }

    void set8(final int index8, final byte value) {
        int index64 = index8 / 8;

        long valueAsLong = ((long) value) & KEEP_LAST_BYTE;
        switch (index8 % 8) {
            case 7 -> {
                state[index64] &= ZERO_BYTE_7;
                state[index64] |= valueAsLong;
            }
            case 6 -> {
                state[index64] &= ZERO_BYTE_6;
                state[index64] |= (valueAsLong << 8);
            }
            case 5 -> {
                state[index64] &= ZERO_BYTE_5;
                state[index64] |= (valueAsLong << 16);
            }
            case 4 -> {
                state[index64] &= ZERO_BYTE_4;
                state[index64] |= (valueAsLong << 24);
            }
            case 3 -> {
                state[index64] &= ZERO_BYTE_3;
                state[index64] |= (valueAsLong << 32);
            }
            case 2 -> {
                state[index64] &= ZERO_BYTE_2;
                state[index64] |= (valueAsLong << 40);
            }
            case 1 -> {
                state[index64] &= ZERO_BYTE_1;
                state[index64] |= (valueAsLong << 48);
            }
            default -> { // Always 0 in this case
                state[index64] &= ZERO_BYTE_0;
                state[index64] |= (valueAsLong << 56);
            }
        }
    }

    void set8XOR(final int index8, final byte value) {
        set8(index8, (byte) (get8(index8) ^ value));
    }

    long get64(final int index64) {
        return state[index64];
    }

    void set64(final int index64, final long value) {
        state[index64] = value;
    }

    long[] getArray() {
        return state;
    }
}
