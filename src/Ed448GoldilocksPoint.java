import java.math.BigInteger;

public class Ed448GoldilocksPoint {

    private static final BigInteger d = BigInteger.valueOf(-39081L);
    private static final BigInteger negativeD = BigInteger.valueOf(39081L);

    static final Ed448GoldilocksPoint G = new Ed448GoldilocksPoint(BigInteger.valueOf(8L), false);
    static final Ed448GoldilocksPoint O = new Ed448GoldilocksPoint();

    final BigInteger x;
    final BigInteger y;

    Ed448GoldilocksPoint(final BigInteger x, final BigInteger y) {
        this.x = x;
        this.y = y;
    }

    Ed448GoldilocksPoint(final byte[] x, final byte[] y) {
        this(new BigInteger(x), new BigInteger(y));
    }

    Ed448GoldilocksPoint(final BigInteger x, final boolean lsb) {
        this(x, calculateY(x, lsb));
    }

    // Neutral element
    private Ed448GoldilocksPoint() {
        this(BigInteger.ZERO, BigInteger.ONE);
    }

    private static BigInteger calculateY(final BigInteger x, final boolean lsb) {
        final BigInteger xSquared = ModP.mult(x, x);

        return ModP.sqrt(
                ModP.div(
                        ModP.sub(BigInteger.ONE, xSquared),
                        ModP.add(
                                BigInteger.ONE,
                                ModP.mult(negativeD, xSquared)
                        )
                ),
                lsb
        );
    }

    Ed448GoldilocksPoint negate() {
        return new Ed448GoldilocksPoint(x.negate(), y);
    }

    Ed448GoldilocksPoint add(final Ed448GoldilocksPoint addend) {
        final BigInteger dX1X2Y1Y2 = ModP.mult(
                d, x, addend.x, y, addend.y
        );

        return new Ed448GoldilocksPoint(
                ModP.div( // New x
                        // x1 y2 + y1 x2
                        ModP.add(
                                ModP.mult(x, addend.y),
                                ModP.mult(y, addend.x)
                        ),
                        // 1 + d x1 x2 y1 y2
                        ModP.add(BigInteger.ONE, dX1X2Y1Y2)
                ),
                ModP.div( // New y
                        // y1 y2 - x1 x2
                        ModP.sub(
                                ModP.mult(y, addend.y),
                                ModP.mult(x, addend.x)
                        ),
                        // 1 - d x1 x2 y1 y2
                        ModP.sub(BigInteger.ONE, dX1X2Y1Y2)
                )
        );
    }

    Ed448GoldilocksPoint publicMultiply(final BigInteger scalar) {
        if (scalar.equals(BigInteger.ZERO))
            return O;

        Ed448GoldilocksPoint V = this;
        for (int i = scalar.bitLength() - 2; i >= 0; i--) {
            V = V.add(V);
            if (scalar.testBit(i))
                V = V.add(this);
        }

        return V;
    }

    // Montgomery point multiplication algorithm
//    Ed448GoldilocksPoint privateMultiply(final BigInteger scalar) {
//        Ed448GoldilocksPoint[] R = {this, this.add(this)};
//        boolean swap = false;
//
//        for (int i = scalar.bitLength() - 2; i >= 0; i--) {
//            // R0, R1 = condswap(R0, R1, swap ⊕ si)
//            condSwap(R, swap != scalar.testBit(i));
//
//            // R0, R1 = 2R0, R0 + R1
//            R[1] = R[0].add(R[1]);
//            R[0] = R[0].add(R[0]);
//
//            // swap = si
//            swap = scalar.testBit(i);
//        }
//
//        // R0, R1 = condswap(R0, R1, swap)
//        condSwap(R, swap);
//
//        return R[0];
//    }

    boolean equals(Ed448GoldilocksPoint other) {
        return x.equals(other.x) && y.equals(other.y);
    }

    byte[][] toBytes() {
        return new byte[][]{x.toByteArray(), y.toByteArray()};
    }

//    private void condSwap(final Ed448GoldilocksPoint[] R, final boolean swap) {
//        if (swap) {
//            var temp = R[0];
//            R[0] = R[1];
//            R[1] = temp;
//        }
//
//        // Definitely still insecure, since BigInteger uses array sizes based
//        // on the magnitude of the numbers (minimal 2's comp representation)
////        BigInteger[] diff = {
////                R[0].x.xor(R[1].x).and(BigInteger.ZERO), // or replace with constant for BigInteger.ONE.negate()
////                R[0].y.xor(R[1].y).and(BigInteger.ZERO)
////        };
//    }
}