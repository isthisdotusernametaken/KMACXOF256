import java.math.BigInteger;

/**
 * @author Joshua Barbee
 */
public class ModR {

    static final BigInteger r = BigInteger.TWO.pow(446)
                                .subtract(new BigInteger(
                                        "13818066809895115352007386748515426" +
                                        "880336692474882178609894547503885"
                                ));

    static BigInteger add(final BigInteger augend, final BigInteger addend) {
        return ModularArithmetic.add(r, augend, addend);
    }

    static BigInteger sub(final BigInteger minuend, final BigInteger subtrahend) {
        return ModularArithmetic.sub(r, minuend, subtrahend);
    }

    static BigInteger mult(final BigInteger... factors) {
         return ModularArithmetic.mult(r, factors);
    }

    static BigInteger getRandScalar() {
        BigInteger res;
        do {
            // In office hours, we were informed that only 448 (instead of 512,
            // as given in the services pseudocode in the assignment
            // description) random bits are necessary here to meet the required
            // security level
            res = new BigInteger(448, Util.RANDOM);

            if (res.signum() == -1)
                res = res.negate();
        } while (res.compareTo(r) >= 0);

        return res;
    }
}
