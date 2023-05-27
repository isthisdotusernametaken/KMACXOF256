import java.math.BigInteger;

/**
 * State and arithmatic for Ed448 Goldilocks Points.
 * @author Joshua Barbee
 */
public class Ed448GoldilocksPoint {

    private static final BigInteger d = BigInteger.valueOf(-39081L);
    private static final BigInteger negativeD = BigInteger.valueOf(39081L);

    static final Ed448GoldilocksPoint G = new Ed448GoldilocksPoint(BigInteger.valueOf(8L), false);
    static final Ed448GoldilocksPoint O = new Ed448GoldilocksPoint();

    /** X value of Ed point. */
    final BigInteger x;
    /** Y value of Ed point. */
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

    /**
     * Calculation of Y Ed448 point, logic constructed from provided documentation.
     * @param x x coordinate of point.
     * @param lsb least significant bit.
     * @return y coordinate of point.
     */
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

    /**
     * Addition of two Ed448 points, logic constructed from provided documentation.
     * @param addend second Ed448 point to add to this point.
     * @return sum of points.
     */
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

    // To avoid information leakage, this multiplication method should be used
    // only with public parameters if the provided security services are to be
    // practically used. A multiplication algorithm without this leakage issue
    // is not included in this (toy) implementation.
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

    boolean equals(Ed448GoldilocksPoint other) {
        return x.equals(other.x) && y.equals(other.y);
    }

    byte[][] toBytes() {
        return new byte[][]{x.toByteArray(), y.toByteArray()};
    }

}