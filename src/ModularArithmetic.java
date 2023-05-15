import java.math.BigInteger;

public class ModularArithmetic {

    static final BigInteger r = BigInteger.TWO.pow(446)
                                .subtract(new BigInteger(
                                        "13818066809895115352007386748515426" +
                                        "880336692474882178609894547503885"
                                ));
    static final BigInteger p = BigInteger.TWO.pow(448)
                                        .subtract(BigInteger.TWO.pow(224))
                                        .subtract(BigInteger.ONE);

    static BigInteger add(final BigInteger augend, final BigInteger addend) {
        return augend.add(addend).mod(p);
    }

    static BigInteger sub(final BigInteger minuend, final BigInteger subtrahend) {
        return minuend.subtract(subtrahend).mod(p);
    }

    static BigInteger mult(final BigInteger... factors) {
         BigInteger acc = factors[0];
         for (int i = 1; i < factors.length; i++)
             acc = acc.multiply(factors[i]).mod(p);

         return acc;
    }

    static BigInteger div(final BigInteger dividend, final BigInteger divisor) {
        return dividend.multiply(divisor.modInverse(p)).mod(p);
    }

    static BigInteger sqrt(final BigInteger radicand, final boolean lsb) {
        if (radicand.signum() == 0)
            return BigInteger.ZERO;

        BigInteger res = radicand.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (res.testBit(0) != lsb)
            res = p.subtract(res);

        return (res.multiply(res).subtract(radicand).mod(p).signum() == 0) ? res : null;
    }

    static BigInteger getRandK() {
        BigInteger res;
        do {
            res = new BigInteger(448,Util.RANDOM);

            if (res.signum() == -1)
                res = res.negate();
        } while (res.compareTo(r) >= 0);

        return res;
    }
}
