import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author James Deal
 */
public class TestValidity {

    /**
     * Runs included testing functionality.
     */
    public static void runTests() {
        String res = testSHA() ? "Passed" : "Failed";
        System.out.println("SHA-3 Core Tests " + res);

        res = testcSHAKE() ? "Passed" : "Failed";
        System.out.println("cSHAKE Core Tests " + res);

        res = testKMACX() ? "Passed" : "Failed";
        System.out.println("KMACXOF256 Core Tests " + res);

        res = testPubG() ? "Passed" : "Failed";
        System.out.println("Public Multiply Elliptic Curve Tests " + res);

        res = testrandK() ? "Passed" : "Failed";
        System.out.println("Random Value Elliptic Curve Tests " + res);

        res = testSigning() ? "Passed" : "Failed";
        System.out.println("Signing Validation Tests " + res);
    }

    /**
     * Test suite for included Ed448 document signing.
     * These tests are adapted from values tested during office hour.
     * @return whether tests were passed.
     */
    private static boolean testSigning() {
        BigInteger s = new BigInteger("181709681073901722637330951972001133588410340171829515070372549795146003961539585716195755291692375963310293709091662304773755859649767");
        Ed448GoldilocksPoint V = Ed448GoldilocksPoint.G.publicMultiply(s);
        Ed448GoldilocksPoint Vcomp = new Ed448GoldilocksPoint(new BigInteger("111910020947240298691268336162300824746037131216209191799810358096412318142801044014797907773347279881352710842324719097290172466450981"),
                new BigInteger("303065640976498061814411258004932719509123967921223459369488294905178621661014494003510485620704199511147463779612337517846176798606966"));
        if (!V.equals(Vcomp)) {
            System.out.println("V does not equal stored expected value.");
            return false;
        }

        BigInteger k = new BigInteger("181709681073901722637330951972001133588410340171829515070372549795146003961539585716195755291692375963310293709091662304773755859649771");
        Ed448GoldilocksPoint U = Ed448GoldilocksPoint.G.publicMultiply(k);
        Ed448GoldilocksPoint Ucomp = new Ed448GoldilocksPoint(new BigInteger("404380029028889598305090303264184820370701159517231199528356945527365709676372694331698319757588071399739144817588205208086872260580233"),
                new BigInteger("275896460466705704734935549735944857172840076155469986901454984644766316915006013707938105197446415437527868162799018964936077024972356"));
        if (!U.equals(Ucomp)) {
            System.out.println("U does not equal stored expected value.");
            return false;
        }

        BigInteger h = new BigInteger("181709681073901722637330951972001133588410340171829515070372549795146003961539585716195755291692375963310293709091662304773755859649773");
        BigInteger z = k.subtract(h.multiply(s)).mod(ModR.r);
        BigInteger zComp = new BigInteger("181709681073901722637330951972001133588410340171829515070372549795146003961539585716195755291692375963310293709091662304773755859649699");
        if (!z.equals(zComp)) {
            System.out.println("z does not equal stored expected value.");
            return false;
        }

        Ed448GoldilocksPoint zg = Ed448GoldilocksPoint.G.publicMultiply(z);
        Ed448GoldilocksPoint ZGcomp = new Ed448GoldilocksPoint(new BigInteger("345894552852087263977237504695496465160464389249526159648594302148096535488074423113569071955810152000328012051065483274989607585737598"),
                new BigInteger("454462844747667412945175092625655210772183720085230519727190280879775444374115702642054728064935049574668331824260439712755646156916252"));
        if (!zg.equals(ZGcomp)) {
            System.out.println("zg does not equal stored expected value.");
            return false;
        }

        Ed448GoldilocksPoint hv = V.publicMultiply(h);
        Ed448GoldilocksPoint HVcomp = new Ed448GoldilocksPoint(new BigInteger("138673777668098078958523464308330120015358842185385264673450630678467333639774002047545931872429733011941889972579872832692149385574261"),
                new BigInteger("706268205075764833997762442771271321638924372213634149932866211190160677974967523276890474214539730357726552291657851001494570124193482"));
        if (!hv.equals(HVcomp)) {
            System.out.println("hv does not equal stored expected value.");
            return false;
        }


        Ed448GoldilocksPoint W = zg.add(hv);
        Ed448GoldilocksPoint Wcomp = new Ed448GoldilocksPoint(new BigInteger("404380029028889598305090303264184820370701159517231199528356945527365709676372694331698319757588071399739144817588205208086872260580233"),
                new BigInteger("275896460466705704734935549735944857172840076155469986901454984644766316915006013707938105197446415437527868162799018964936077024972356"));
        if (!W.equals(Wcomp)) {
            System.out.println("W does not equal stored expected value.");
            return false;
        }

        return true;
    }

    /**
     * Test suite for included Ed448 Goldilocks Point implementation with Random numbers.
     * These tests are adapted from provided project documentation.
     * @return whether tests were passed.
     */
    private static boolean testrandK() {
        for (int i = 0; i < 10; i++) {
            //k * G = (k mod r) * G
            BigInteger k = ModR.getRandK();

            Ed448GoldilocksPoint kG = Ed448GoldilocksPoint.G.publicMultiply(k);
            Ed448GoldilocksPoint kmrG = Ed448GoldilocksPoint.G.publicMultiply(k);

            if (!kG.equals(kmrG)) {
                System.out.println("kG != kmrG");
                return false;
            }

            //(k + 1) * G = (k * G) + G
            Ed448GoldilocksPoint k1G = Ed448GoldilocksPoint.G.publicMultiply(k.add(new BigInteger("1")));
            Ed448GoldilocksPoint kGpG = Ed448GoldilocksPoint.G.publicMultiply(k).add(Ed448GoldilocksPoint.G);
            if (!k1G.equals(kGpG)) {
                System.out.println("k1G != kGpG");
                return false;
            }

            //(k + t) * G = (k * G) + (t * G)
            BigInteger t = new BigInteger(448, Util.RANDOM);
            k1G = Ed448GoldilocksPoint.G.publicMultiply(k.add(t));
            kGpG = Ed448GoldilocksPoint.G.publicMultiply(k).add(Ed448GoldilocksPoint.G.publicMultiply(t));
            if (!k1G.equals(kGpG)) {
                System.out.println("(k + t) * G != (k * G) + (t * G)");
                return false;
            }

            //k * (t * G) = t * (k * G) = (k * t mod r) * G
            Ed448GoldilocksPoint ktG = Ed448GoldilocksPoint.G.publicMultiply(t).publicMultiply(k);
            Ed448GoldilocksPoint tkG = Ed448GoldilocksPoint.G.publicMultiply(k).publicMultiply(t);
            BigInteger ktmr = ModR.mult(k, t);
            Ed448GoldilocksPoint ktmrG = Ed448GoldilocksPoint.G.publicMultiply(ktmr);
            if (!ktG.equals(tkG)) {
                System.out.println("k * (t * G) != t * (k * G)");
                return false;
            }
            if (!tkG.equals(ktmrG)) {
                System.out.println("t * (k * G) != (k * t mod r) * G");
                return false;
            }
        }

        return true;
    }

    /**
     * Test suite for included Ed448 Goldilocks Point implementation.
     * These tests are adapted from provided project documentation.
     * @return whether tests were passed.
     */
    private static boolean testPubG() {
        //0G = O
        Ed448GoldilocksPoint localG = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger("0"));

        if (!localG.equals(Ed448GoldilocksPoint.O)) {
            System.out.println("0 != 0");
            return false;
        }

        //1G = G
        localG = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger("1"));

        if (!localG.equals(Ed448GoldilocksPoint.G)) {
            System.out.println("1 != 1");
            return false;
        }

        //G - G = O
        Ed448GoldilocksPoint negG = Ed448GoldilocksPoint.G.negate();
        localG = localG.add(negG);

        if (!localG.equals(Ed448GoldilocksPoint.O)) {
            System.out.println("-x is equal to y-g-the square root of bacon");
            return false;
        }

        //2G = G + G
        Ed448GoldilocksPoint plusG = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger("2"));
        Ed448GoldilocksPoint multG = Ed448GoldilocksPoint.G.add(Ed448GoldilocksPoint.G);

        if (!plusG.equals(multG)) {
            System.out.println("2 != 2");
            return false;
        }

        //4G = 2(2G)
        Ed448GoldilocksPoint fourG = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger("4"));
        Ed448GoldilocksPoint twoG = Ed448GoldilocksPoint.G.publicMultiply(new BigInteger("2"))
                                                            .publicMultiply(new BigInteger("2"));

        if (!twoG.equals(fourG)) {
            System.out.println("4 != 2*2");
            return false;
        }

        //4G != O
        if (fourG.equals(Ed448GoldilocksPoint.O)) {
            System.out.println("4 == O");
            return false;
        }

        //rG = O
        localG = Ed448GoldilocksPoint.G.publicMultiply(ModR.r);
        if (!localG.equals(Ed448GoldilocksPoint.O)) {
            System.out.println("rG != O");
            return false;
        }

        return true;
    }

    /**
     * Test suite for included SHA3 implementation.
     * These tests are adapted from <a href="https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c">Markku-Juhani Saarinen’s tiny_sha3</a>
     * @return whether tests were passed.
     */
    private static boolean testSHA() {

        byte[] firstCompare = {(byte) 0xBA, (byte) 0xAE, (byte) 0x0F, (byte) 0x5F, (byte) 0xB1, (byte) 0x36,
                (byte) 0x9D, (byte) 0xB7, (byte) 0x8F, (byte) 0x3A, (byte) 0xC4, (byte) 0x5F, (byte) 0x8C,
                (byte) 0x4A, (byte) 0xC5, (byte) 0x67, (byte) 0x1D, (byte) 0x85, (byte) 0x73, (byte) 0x5C,
                (byte) 0xDD, (byte) 0xDB, (byte) 0x09, (byte) 0xD2, (byte) 0xB1, (byte) 0xE3, (byte) 0x4A,
                (byte) 0x1F, (byte) 0xC0, (byte) 0x66, (byte) 0xFF, (byte) 0x4A, (byte) 0x16, (byte) 0x2C,
                (byte) 0xB2, (byte) 0x63, (byte) 0xD6, (byte) 0x54, (byte) 0x12, (byte) 0x74, (byte) 0xAE,
                (byte) 0x2F, (byte) 0xCC, (byte) 0x86, (byte) 0x5F, (byte) 0x61, (byte) 0x8A, (byte) 0xBE,
                (byte) 0x27, (byte) 0xC1, (byte) 0x24, (byte) 0xCD, (byte) 0x8B, (byte) 0x07, (byte) 0x4C,
                (byte) 0xCD, (byte) 0x51, (byte) 0x63, (byte) 0x01, (byte) 0xB9, (byte) 0x18, (byte) 0x75,
                (byte) 0x82, (byte) 0x4D, (byte) 0x09, (byte) 0x95, (byte) 0x8F, (byte) 0x34, (byte) 0x1E,
                (byte) 0xF2, (byte) 0x74, (byte) 0xBD, (byte) 0xAB, (byte) 0x0B, (byte) 0xAE, (byte) 0x31,
                (byte) 0x63, (byte) 0x39, (byte) 0x89, (byte) 0x43, (byte) 0x04, (byte) 0xE3, (byte) 0x58,
                (byte) 0x77, (byte) 0xB0, (byte) 0xC2, (byte) 0x8A, (byte) 0x9B, (byte) 0x1F, (byte) 0xD1,
                (byte) 0x66, (byte) 0xC7, (byte) 0x96, (byte) 0xB9, (byte) 0xCC, (byte) 0x25, (byte) 0x8A,
                (byte) 0x06, (byte) 0x4A, (byte) 0x8F, (byte) 0x57, (byte) 0xE2, (byte) 0x7F, (byte) 0x2A,
                (byte) 0x5B, (byte) 0x8D, (byte) 0x54, (byte) 0x8A, (byte) 0x72, (byte) 0x8C, (byte) 0x94,
                (byte) 0x44, (byte) 0xEC, (byte) 0xB8, (byte) 0x79, (byte) 0xAD, (byte) 0xC1, (byte) 0x9D,
                (byte) 0xE0, (byte) 0xC1, (byte) 0xB8, (byte) 0x58, (byte) 0x7D, (byte) 0xE3, (byte) 0xE7,
                (byte) 0x3E, (byte) 0x15, (byte) 0xD3, (byte) 0xCE, (byte) 0x2D, (byte) 0xB7, (byte) 0xC9,
                (byte) 0xFA, (byte) 0x7B, (byte) 0x58, (byte) 0xFF, (byte) 0xF7, (byte) 0x62, (byte) 0xC3,
                (byte) 0x82, (byte) 0x83, (byte) 0x58, (byte) 0x67, (byte) 0x7D, (byte) 0xB2, (byte) 0x4B,
                (byte) 0x75, (byte) 0x9B, (byte) 0x41, (byte) 0x1C, (byte) 0x3A, (byte) 0x73, (byte) 0xD4,
                (byte) 0x78, (byte) 0x06, (byte) 0xA3, (byte) 0x79, (byte) 0x88, (byte) 0xBE, (byte) 0x2A,
                (byte) 0xF4, (byte) 0xC7, (byte) 0xD0, (byte) 0x99, (byte) 0x46, (byte) 0x91, (byte) 0x92,
                (byte) 0x20, (byte) 0x29, (byte) 0xA3, (byte) 0xE2, (byte) 0x2E, (byte) 0xA3, (byte) 0x66,
                (byte) 0x2F, (byte) 0xEF, (byte) 0xB4, (byte) 0x4C, (byte) 0x99, (byte) 0xD9, (byte) 0xC9,
                (byte) 0x87, (byte) 0xD5, (byte) 0x3D, (byte) 0x6F, (byte) 0xB2, (byte) 0x9C, (byte) 0x1B,
                (byte) 0x16, (byte) 0xCE, (byte) 0x5C, (byte) 0x63, (byte) 0x04, (byte) 0x4E, (byte) 0xB7,
                (byte) 0xE1, (byte) 0x99, (byte) 0xB0, (byte) 0xBE, (byte) 0xFD};

        byte[] secondCompare = {(byte) 0x79, (byte) 0x12, (byte) 0x44, (byte) 0xD3, (byte) 0x07, (byte) 0x72,
                (byte) 0x65, (byte) 0x05, (byte) 0xC3, (byte) 0xAD, (byte) 0x4B, (byte) 0x26, (byte) 0xB6,
                (byte) 0x82, (byte) 0x23, (byte) 0x77, (byte) 0x25, (byte) 0x7A, (byte) 0xA1, (byte) 0x52,
                (byte) 0x03, (byte) 0x75, (byte) 0x60, (byte) 0xA7, (byte) 0x39, (byte) 0x71, (byte) 0x4A,
                (byte) 0x3C, (byte) 0xA7, (byte) 0x9B, (byte) 0xD6, (byte) 0x05, (byte) 0x54, (byte) 0x7C,
                (byte) 0x9B, (byte) 0x78, (byte) 0xDD, (byte) 0x1F, (byte) 0x59, (byte) 0x6F, (byte) 0x2D,
                (byte) 0x4F, (byte) 0x17, (byte) 0x91, (byte) 0xBC, (byte) 0x68, (byte) 0x9A, (byte) 0x0E,
                (byte) 0x9B, (byte) 0x79, (byte) 0x9A, (byte) 0x37, (byte) 0x33, (byte) 0x9C, (byte) 0x04,
                (byte) 0x27, (byte) 0x57, (byte) 0x33, (byte) 0x74, (byte) 0x01, (byte) 0x43, (byte) 0xEF,
                (byte) 0x5D, (byte) 0x2B, (byte) 0x58, (byte) 0xB9, (byte) 0x6A, (byte) 0x36, (byte) 0x3D,
                (byte) 0x4E, (byte) 0x08, (byte) 0x07, (byte) 0x6A, (byte) 0x1A, (byte) 0x9D, (byte) 0x78,
                (byte) 0x46, (byte) 0x43, (byte) 0x6E, (byte) 0x4D, (byte) 0xCA, (byte) 0x57, (byte) 0x28,
                (byte) 0xB6, (byte) 0xF7, (byte) 0x60, (byte) 0xEE, (byte) 0xF0, (byte) 0xCA, (byte) 0x92,
                (byte) 0xBF, (byte) 0x0B, (byte) 0xE5, (byte) 0x61, (byte) 0x5E, (byte) 0x96, (byte) 0x95,
                (byte) 0x9D, (byte) 0x76, (byte) 0x71, (byte) 0x97, (byte) 0xA0, (byte) 0xBE, (byte) 0xEB,
                (byte) 0x4C, (byte) 0x01, (byte) 0x7C, (byte) 0xC8, (byte) 0x55, (byte) 0x01, (byte) 0xA2,
                (byte) 0x35, (byte) 0x0F, (byte) 0xF4, (byte) 0xBB, (byte) 0x54, (byte) 0x2F, (byte) 0xA7,
                (byte) 0xE9, (byte) 0x63, (byte) 0x34, (byte) 0xBF, (byte) 0x31, (byte) 0x4E, (byte) 0x08,
                (byte) 0x70, (byte) 0x48, (byte) 0x1E, (byte) 0xD9, (byte) 0x7D, (byte) 0xD4, (byte) 0xA7,
                (byte) 0xE0, (byte) 0xCC, (byte) 0x39, (byte) 0x82, (byte) 0x2C, (byte) 0x92, (byte) 0x88,
                (byte) 0x46, (byte) 0x50, (byte) 0x4C, (byte) 0x82, (byte) 0xBB, (byte) 0x61, (byte) 0x1B,
                (byte) 0x46, (byte) 0x61, (byte) 0xD3, (byte) 0x9A, (byte) 0x23, (byte) 0x90, (byte) 0x4B,
                (byte) 0xF7, (byte) 0xAC, (byte) 0xB8, (byte) 0xC9, (byte) 0x2A, (byte) 0x68, (byte) 0x8D,
                (byte) 0x63, (byte) 0x7B, (byte) 0x88, (byte) 0x7F, (byte) 0x82, (byte) 0xE2, (byte) 0x78,
                (byte) 0xAE, (byte) 0xFB, (byte) 0xC5, (byte) 0xBD, (byte) 0xBA, (byte) 0x94, (byte) 0x4D,
                (byte) 0x79, (byte) 0x2E, (byte) 0x31, (byte) 0x46, (byte) 0xE4, (byte) 0xB7, (byte) 0x00,
                (byte) 0xA5, (byte) 0x23, (byte) 0xC6, (byte) 0x34, (byte) 0xC0, (byte) 0x2E, (byte) 0x46,
                (byte) 0x5A, (byte) 0x05, (byte) 0x4A, (byte) 0x76, (byte) 0xA5, (byte) 0x54, (byte) 0x37,
                (byte) 0x98, (byte) 0xB4, (byte) 0x4D, (byte) 0x91, (byte) 0x81};

        ShaObject sha3 = new ShaObject(false);
        byte[] buf = new byte[32];

        for (int i = 0; i < 2; i++) {

            sha3.shake256_init();

            if (i == 1) { // 1600-bit test pattern
                Arrays.fill(buf, 0, 20, (byte) 0b10100011);
                for (int j = 0; j < 200; j += 20)
                    sha3.shake_update(buf, 20);
            }

            sha3.shake_xof();

            for (int j = 0; j < 512; j += 32) {
                sha3.shake_out(buf, 32);
            }


            if (i == 0) {
                for (int j = 0; j < sha3.sa.capacity(); j++) {
                    if (sha3.sa.get(j) != firstCompare[j]) {
                        return false;
                    }
                }
            } else {
                for (int j = 0; j < sha3.sa.capacity(); j++) {
                    if (sha3.sa.get(j) != secondCompare[j]) {
                        return false;
                    }
                }
            }

        }

        return true;
    }

    /**
     * Test suite for included cSHAKE256 functionality.
     * These tests are adapted from <a href="https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/cSHAKE_samples.pdf">NIST's Test Vectors</a>
     * @return whether the tests were passed.
     */
    private static boolean testcSHAKE() {

        byte[] corOne = {(byte) 0xD0, (byte) 0x08, (byte) 0x82, (byte) 0x8E, (byte) 0x2B, (byte) 0x80, (byte) 0xAC,
                (byte) 0x9D, (byte) 0x22, (byte) 0x18, (byte) 0xFF, (byte) 0xEE, (byte) 0x1D, (byte) 0x07, (byte) 0x0C,
                (byte) 0x48, (byte) 0xB8, (byte) 0xE4, (byte) 0xC8, (byte) 0x7B, (byte) 0xFF, (byte) 0x32, (byte) 0xC9,
                (byte) 0x69, (byte) 0x9D, (byte) 0x5B, (byte) 0x68, (byte) 0x96, (byte) 0xEE, (byte) 0xE0, (byte) 0xED,
                (byte) 0xD1, (byte) 0x64, (byte) 0x02, (byte) 0x0E, (byte) 0x2B, (byte) 0xE0, (byte) 0x56, (byte) 0x08,
                (byte) 0x58, (byte) 0xD9, (byte) 0xC0, (byte) 0x0C, (byte) 0x03, (byte) 0x7E, (byte) 0x34, (byte) 0xA9,
                (byte) 0x69, (byte) 0x37, (byte) 0xC5, (byte) 0x61, (byte) 0xA7, (byte) 0x4C, (byte) 0x41, (byte) 0x2B,
                (byte) 0xB4, (byte) 0xC7, (byte) 0x46, (byte) 0x46, (byte) 0x95, (byte) 0x27, (byte) 0x28, (byte) 0x1C,
                (byte) 0x8C};

        byte[] corTwo = {(byte) 0x07, (byte) 0xDC, (byte) 0x27, (byte) 0xB1, (byte) 0x1E, (byte) 0x51, (byte) 0xFB,
                (byte) 0xAC, (byte) 0x75, (byte) 0xBC, (byte) 0x7B, (byte) 0x3C, (byte) 0x1D, (byte) 0x98, (byte) 0x3E,
                (byte) 0x8B, (byte) 0x4B, (byte) 0x85, (byte) 0xFB, (byte) 0x1D, (byte) 0xEF, (byte) 0xAF, (byte) 0x21,
                (byte) 0x89, (byte) 0x12, (byte) 0xAC, (byte) 0x86, (byte) 0x43, (byte) 0x02, (byte) 0x73, (byte) 0x09,
                (byte) 0x17, (byte) 0x27, (byte) 0xF4, (byte) 0x2B, (byte) 0x17, (byte) 0xED, (byte) 0x1D, (byte) 0xF6,
                (byte) 0x3E, (byte) 0x8E, (byte) 0xC1, (byte) 0x18, (byte) 0xF0, (byte) 0x4B, (byte) 0x23, (byte) 0x63,
                (byte) 0x3C, (byte) 0x1D, (byte) 0xFB, (byte) 0x15, (byte) 0x74, (byte) 0xC8, (byte) 0xFB, (byte) 0x55,
                (byte) 0xCB, (byte) 0x45, (byte) 0xDA, (byte) 0x8E, (byte) 0x25, (byte) 0xAF, (byte) 0xB0, (byte) 0x92,
                (byte) 0xBB};

        String N = "";
        String S = "Email Signature";
        int L = 512;

        byte[] dOne = {0, 1, 2, 3};

        byte[] dTwo = new byte[200];
        for (int i = 0; i < 200; i++) {
            dTwo[i] = (byte) i;
        }

        return Arrays.equals(KMACXOF256.cSHAKE256(dOne, L, N, S), corOne)
                && Arrays.equals(KMACXOF256.cSHAKE256(dTwo, L, N, S), corTwo);
    }

    /**
     * Test suite for included KMACXOF256 functionality.
     * These tests are adapted from <a href="https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/KMACXOF_samples.pdf">NIST's Test Vectors</a>
     * @return whether the tests were passed.
     */
    private static boolean testKMACX() {

        byte[] corFour = {(byte) 0x17, (byte) 0x55, (byte) 0x13, (byte) 0x3F, (byte) 0x15, (byte) 0x34, (byte) 0x75,
                (byte) 0x2A, (byte) 0xAD, (byte) 0x07, (byte) 0x48, (byte) 0xF2, (byte) 0xC7, (byte) 0x06, (byte) 0xFB,
                (byte) 0x5C, (byte) 0x78, (byte) 0x45, (byte) 0x12, (byte) 0xCA, (byte) 0xB8, (byte) 0x35, (byte) 0xCD,
                (byte) 0x15, (byte) 0x67, (byte) 0x6B, (byte) 0x16, (byte) 0xC0, (byte) 0xC6, (byte) 0x64, (byte) 0x7F,
                (byte) 0xA9, (byte) 0x6F, (byte) 0xAA, (byte) 0x7A, (byte) 0xF6, (byte) 0x34, (byte) 0xA0, (byte) 0xBF,
                (byte) 0x8F, (byte) 0xF6, (byte) 0xDF, (byte) 0x39, (byte) 0x37, (byte) 0x4F, (byte) 0xA0, (byte) 0x0F,
                (byte) 0xAD, (byte) 0x9A, (byte) 0x39, (byte) 0xE3, (byte) 0x22, (byte) 0xA7, (byte) 0xC9, (byte) 0x20,
                (byte) 0x65, (byte) 0xA6, (byte) 0x4E, (byte) 0xB1, (byte) 0xFB, (byte) 0x08, (byte) 0x01, (byte) 0xEB,
                (byte) 0x2B};

        byte[] corFive = {(byte) 0xFF, (byte) 0x7B, (byte) 0x17, (byte) 0x1F, (byte) 0x1E, (byte) 0x8A, (byte) 0x2B,
                (byte) 0x24, (byte) 0x68, (byte) 0x3E, (byte) 0xED, (byte) 0x37, (byte) 0x83, (byte) 0x0E, (byte) 0xE7,
                (byte) 0x97, (byte) 0x53, (byte) 0x8B, (byte) 0xA8, (byte) 0xDC, (byte) 0x56, (byte) 0x3F, (byte) 0x6D,
                (byte) 0xA1, (byte) 0xE6, (byte) 0x67, (byte) 0x39, (byte) 0x1A, (byte) 0x75, (byte) 0xED, (byte) 0xC0,
                (byte) 0x2C, (byte) 0xA6, (byte) 0x33, (byte) 0x07, (byte) 0x9F, (byte) 0x81, (byte) 0xCE, (byte) 0x12,
                (byte) 0xA2, (byte) 0x5F, (byte) 0x45, (byte) 0x61, (byte) 0x5E, (byte) 0xC8, (byte) 0x99, (byte) 0x72,
                (byte) 0x03, (byte) 0x1D, (byte) 0x18, (byte) 0x33, (byte) 0x73, (byte) 0x31, (byte) 0xD2, (byte) 0x4C,
                (byte) 0xEB, (byte) 0x8F, (byte) 0x8C, (byte) 0xA8, (byte) 0xE6, (byte) 0xA1, (byte) 0x9F, (byte) 0xD9,
                (byte) 0x8B};

        byte[] corSix = {(byte) 0xD5, (byte) 0xBE, (byte) 0x73, (byte) 0x1C, (byte) 0x95, (byte) 0x4E, (byte) 0xD7,
                (byte) 0x73, (byte) 0x28, (byte) 0x46, (byte) 0xBB, (byte) 0x59, (byte) 0xDB, (byte) 0xE3, (byte) 0xA8,
                (byte) 0xE3, (byte) 0x0F, (byte) 0x83, (byte) 0xE7, (byte) 0x7A, (byte) 0x4B, (byte) 0xFF, (byte) 0x44,
                (byte) 0x59, (byte) 0xF2, (byte) 0xF1, (byte) 0xC2, (byte) 0xB4, (byte) 0xEC, (byte) 0xEB, (byte) 0xB8,
                (byte) 0xCE, (byte) 0x67, (byte) 0xBA, (byte) 0x01, (byte) 0xC6, (byte) 0x2E, (byte) 0x8A, (byte) 0xB8,
                (byte) 0x57, (byte) 0x8D, (byte) 0x2D, (byte) 0x49, (byte) 0x9B, (byte) 0xD1, (byte) 0xBB, (byte) 0x27,
                (byte) 0x67, (byte) 0x68, (byte) 0x78, (byte) 0x11, (byte) 0x90, (byte) 0x02, (byte) 0x0A, (byte) 0x30,
                (byte) 0x6A, (byte) 0x97, (byte) 0xDE, (byte) 0x28, (byte) 0x1D, (byte) 0xCC, (byte) 0x30, (byte) 0x30,
                (byte) 0x5D};

        int L = 512;
        String s = "My Tagged Application";
        byte[] x = {0, 1, 2, 3};

        byte[] d = new byte[200];
        for (int i = 0; i < 200; i++) {
            d[i] = (byte) i;
        }

        byte[] k = new byte[32];
        for (int i = 0; i < 32; i++) {
            k[i] = (byte) (i + 64);
        }

        return Arrays.equals(KMACXOF256.runKMACXOF256(k, x, L, s), corFour)
                && Arrays.equals(KMACXOF256.runKMACXOF256(k, d, L, ""), corFive)
                && Arrays.equals(KMACXOF256.runKMACXOF256(k, d, L, s), corSix);
    }
}
