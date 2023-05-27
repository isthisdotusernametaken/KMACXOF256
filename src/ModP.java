import java.math.BigInteger;

/**
 * @author Joshua Barbee
 */
public class ModP {

    static final BigInteger p = BigInteger.TWO.pow(448)
                                .subtract(BigInteger.TWO.pow(224))
                                .subtract(BigInteger.ONE);

    static BigInteger add(final BigInteger augend, final BigInteger addend) {
        return ModularArithmetic.add(p, augend, addend);
    }

    static BigInteger sub(final BigInteger minuend, final BigInteger subtrahend) {
        return ModularArithmetic.sub(p, minuend, subtrahend);
    }

    static BigInteger mult(final BigInteger... factors) {
         return ModularArithmetic.mult(p, factors);
    }

    static BigInteger div(final BigInteger dividend, final BigInteger divisor) {
        return ModularArithmetic.div(p, dividend, divisor);
    }

    static BigInteger sqrt(final BigInteger radicand, final boolean lsb) {
        return ModularArithmetic.sqrt(p, radicand, lsb);
    }
}
