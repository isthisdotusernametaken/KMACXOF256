import java.math.BigInteger;

public class ModularArithmetic {

    private static final BigInteger p = BigInteger.TWO.pow(448)
            .subtract(BigInteger.TWO.pow(224))
            .subtract(BigInteger.ONE);

    static BigInteger add(final BigInteger augend, final BigInteger addend) {
        return augend.add(addend).mod(p);
    }

    static BigInteger sub(final BigInteger minuend, final BigInteger subtrahend) {
        return minuend.subtract(subtrahend).mod(p);
    }

    static BigInteger mult(final BigInteger ... factors) {
         BigInteger acc = factors[0];
         for (int i = 1; i < factors.length; i++)
             acc = acc.multiply(factors[i]).mod(p);

         return acc;
    }

    static BigInteger div(final BigInteger dividend, final BigInteger divisor) {
        return dividend.multiply(divisor.modInverse(p)).mod(p);
    }

    static BigInteger sqrt(final BigInteger radicand, final boolean positive) {
        return BigInteger.ONE; // Placeholder
    }
}
