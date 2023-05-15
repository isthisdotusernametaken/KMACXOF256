import java.math.BigInteger;

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

    static BigInteger getRandK() {
        BigInteger res;
        do {
            res = new BigInteger(448, Util.RANDOM);

            if (res.signum() == -1)
                res = res.negate();
        } while (res.compareTo(r) >= 0);

        return res;
    }
}
