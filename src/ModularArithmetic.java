import java.math.BigInteger;

public class ModularArithmetic {

    static BigInteger add(final BigInteger mod, final BigInteger augend, final BigInteger addend) {
        return augend.add(addend).mod(mod);
    }

    static BigInteger sub(final BigInteger mod, final BigInteger minuend, final BigInteger subtrahend) {
        return minuend.subtract(subtrahend).mod(mod);
    }

    static BigInteger mult(final BigInteger mod, final BigInteger... factors) {
        BigInteger acc = factors[0];
        for (int i = 1; i < factors.length; i++)
            acc = acc.multiply(factors[i]).mod(mod);

        return acc;
    }

    static BigInteger div(final BigInteger mod, final BigInteger dividend, final BigInteger divisor) {
        return dividend.multiply(divisor.modInverse(mod)).mod(mod);
    }

    // Taken directly from part 2 assignment description
    static BigInteger sqrt(final BigInteger mod, final BigInteger radicand, final boolean lsb) {
        if (radicand.signum() == 0)
            return BigInteger.ZERO;

        BigInteger res = radicand.modPow(mod.shiftRight(2).add(BigInteger.ONE), mod);
        if (res.testBit(0) != lsb)
            res = mod.subtract(res);

        return (res.multiply(res).subtract(radicand).mod(mod).signum() == 0) ? res : null;
    }
}
