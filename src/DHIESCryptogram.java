/**
 * @author Joshua Barbee
 */
public record DHIESCryptogram(Ed448GoldilocksPoint Z, byte[] c, byte[] t) {}
