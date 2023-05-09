import java.math.BigInteger;

public class Ed448GoldilocksPoint {

    private static final BigInteger d = BigInteger.valueOf(-39081L);

    final BigInteger x;
    final BigInteger y;

    Ed448GoldilocksPoint(final BigInteger x, final BigInteger y) {
        this.x = x;
        this.y = y;
    }

    Ed448GoldilocksPoint(final byte[] x, final byte[] y) {
        this(new BigInteger(x), new BigInteger(y));
    }

    Ed448GoldilocksPoint(final BigInteger x, final boolean positive) {
        this(x, ModularArithmetic.sqrt(x, positive));
    }

    // Neutral element
    Ed448GoldilocksPoint() {
        this(BigInteger.ZERO, BigInteger.ONE);
    }

    Ed448GoldilocksPoint negate() {
        return new Ed448GoldilocksPoint(x.negate(), y);
    }

    Ed448GoldilocksPoint add(final Ed448GoldilocksPoint addend) {
        final BigInteger dX1X2Y1Y2 = ModularArithmetic.mult(
                d, x, addend.x, y, addend.y
        );

        return new Ed448GoldilocksPoint(
                ModularArithmetic.div( // New x
                        // x1 y2 + y1 x2
                        ModularArithmetic.add(
                                ModularArithmetic.mult(x, addend.y),
                                ModularArithmetic.mult(y, addend.x)
                        ),
                        // 1 + d x1 x2 y1 y2
                        ModularArithmetic.add(BigInteger.ONE, dX1X2Y1Y2)
                ),
                ModularArithmetic.div( // New y
                        // y1 y2 - x1 x2
                        ModularArithmetic.sub(
                                ModularArithmetic.mult(y, addend.y),
                                ModularArithmetic.mult(x, addend.x)
                        ),
                        // 1 - d x1 x2 y1 y2
                        ModularArithmetic.sub(BigInteger.ONE, dX1X2Y1Y2)
                )
        );
    }

    Ed448GoldilocksPoint multiply(final BigInteger scalar) {
        var V = this;
        for (int i = scalar.bitLength() - 1; i >= 0; i--) {
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