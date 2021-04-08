/*
 * @(#)Stats.java
 * Copyright © 2021 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.stats;

import java.util.Arrays;

import static java.lang.Math.sqrt;

/**
 * Provides utility methods for statistics calculations.
 */
public class Stats {
    private Stats() {
        // prevent instantiation
    }

    /**
     * Quantiles of the normal distribution function for
     * expected value = 0 and variance = 1.
     * <p>
     * The values below have been computed with NORMINV(p,0,1) on OpenOffice.
     * <p>
     * References
     * <ul>
     * <li><a href="https://de.wikipedia.org/wiki/Standardnormalverteilungstabelle">wikipedia</a></li>
     * </ul>
     * </p>
     */
    private final static double[][] z1 = {
            // The first row contains the percentage "p".
            {0.9, 0.95, 0.96, 0.975, 0.98, 0.99, 0.995, 0.999, 0.9995},
            {1.2815515655, 1.644853627, 1.7506860713, 1.9599639845, 2.0537489106, 2.326347874, 2.5758293035, 3.0902323062, 3.2905267315},
    };

    /**
     * Quantiles of the Student's t1 function.
     * <p>
     * The values below have been computed with TINV((1-p)*2,n) on OpenOffice.
     * <p>
     * References
     * <ul>
     * <li><a href="https://de.wikipedia.org/wiki/Quantiltabelle">wikipedia</a></li>
     * </ul>
     * </p>
     */
    private final static double[][] t1 = {
            // The first row contains the percentage "p".
            // The first column contains the sample size "n".
            {0, 0.9, 0.95, 0.96, 0.975, 0.98, 0.9875, 0.99, 0.995, 0.999, 0.9995},
            {1, 3.0776835372, 6.3137515147, 7.9158150883, 12.7062047362, 15.8945448439, 25.4516995794, 31.8205159538, 63.6567411629, 318.3088389856, 636.6192487688},
            {2, 1.8856180832, 2.9199855804, 3.3197640478, 4.3026527297, 4.8487322139, 6.2053468166, 6.9645567343, 9.9248432009, 22.3271247701, 31.5990545764},
            {3, 1.6377443537, 2.3533634348, 2.6054268231, 3.1824463053, 3.4819087603, 4.1765348461, 4.5407028586, 5.8409093097, 10.2145318524, 12.9239786367},
            {4, 1.5332062741, 2.1318467863, 2.3328725605, 2.7764451052, 2.9985278732, 3.4954059325, 3.746947388, 4.6040948713, 7.1731822198, 8.6103015814},
            {5, 1.4758840488, 2.0150483733, 2.1909582572, 2.5705818356, 2.7565085219, 3.1633814497, 3.3649299989, 4.0321429836, 5.8934295314, 6.8688266259},
            {6, 1.4397557473, 1.9431802805, 2.1043061225, 2.4469118511, 2.6122418471, 2.9686866842, 3.1426684033, 3.7074280213, 5.2076262387, 5.9588161788},
            {7, 1.4149239277, 1.8945786051, 2.0460111085, 2.3646242516, 2.5167524241, 2.8412442486, 2.9979515669, 3.4994832974, 4.7852896286, 5.4078825209},
            {8, 1.3968153097, 1.8595480375, 2.0041515415, 2.3060041352, 2.4489849896, 2.7515235961, 2.8964594477, 3.3553873313, 4.5007909337, 5.0413054334},
            {9, 1.3830287384, 1.8331129327, 1.9726526521, 2.2621571628, 2.3984409849, 2.6850108468, 2.821437925, 3.2498355416, 4.2968056627, 4.7809125859},
            {10, 1.3721836411, 1.8124611228, 1.9480994689, 2.228138852, 2.3593146237, 2.6337669157, 2.7637694581, 3.1692726726, 4.143700494, 4.5868938587},
            {11, 1.363430318, 1.7958848187, 1.9284268351, 2.2009851601, 2.3281398335, 2.5930926825, 2.7180791838, 3.1058065155, 4.0247010376, 4.4369793382},
            {12, 1.356217334, 1.7822875556, 1.9123133245, 2.1788128297, 2.3027216838, 2.5600329594, 2.6809979931, 3.0545395894, 3.9296332646, 4.3177912836},
            {13, 1.3501712888, 1.770933396, 1.8988744747, 2.1603686565, 2.2816035637, 2.5326378147, 2.6503088379, 3.0122758387, 3.8519823912, 4.2208317277},
            {14, 1.3450303745, 1.7613101358, 1.8874961446, 2.1447866879, 2.263781279, 2.5095694115, 2.6244940676, 2.9768427344, 3.7873902375, 4.1404541127},
            {15, 1.3406056079, 1.7530503557, 1.877738665, 2.1314495456, 2.2485402916, 2.4898797035, 2.602480295, 2.9467128835, 3.7328344253, 4.0727651959},
            {16, 1.3367571673, 1.7458836763, 1.8692790406, 2.1199052992, 2.2353584305, 2.4728783225, 2.5834871853, 2.9207816224, 3.6861547927, 4.0149963272},
            {17, 1.3333793897, 1.7396067261, 1.861874684, 2.1098155778, 2.2238453075, 2.4580507204, 2.5669339837, 2.8982305197, 3.6457673801, 3.9651262721},
            {18, 1.3303909436, 1.7340636066, 1.8553398773, 2.1009220402, 2.2137032516, 2.4450056165, 2.5523796302, 2.8784404727, 3.6104848848, 3.9216458251},
            {19, 1.327728209, 1.7291328115, 1.8495300396, 2.0930240544, 2.2047013507, 2.4334402114, 2.5394831906, 2.8609346065, 3.579400149, 3.8834058526},
            {20, 1.325340707, 1.7247182429, 1.8443309416, 2.0859634473, 2.1966577456, 2.4231165399, 2.5279770027, 2.8453397098, 3.5518083432, 3.8495162749},
            {21, 1.3231878739, 1.7207429028, 1.8396511415, 2.0796138447, 2.1894272705, 2.4138450166, 2.517648016, 2.831359558, 3.5271536689, 3.8192771643},
            {22, 1.3212367416, 1.7171443744, 1.8354165757, 2.0738730679, 2.1828926499, 2.4054727463, 2.5083245529, 2.8187560606, 3.5049920311, 3.7921306717},
            {23, 1.3194602398, 1.7138715277, 1.8315666194, 2.0686576104, 2.1769581113, 2.3978750647, 2.4998667395, 2.8073356838, 3.4849643749, 3.7676268043},
            {24, 1.3178359337, 1.7108820799, 1.828051172, 2.0638985616, 2.171544676, 2.3909493151, 2.4921594732, 2.7969395048, 3.466777298, 3.7453986193},
            {25, 1.3163450727, 1.7081407613, 1.824828469, 2.0595385528, 2.1665866345, 2.3846102008, 2.4851071754, 2.7874358137, 3.450188727, 3.7251439497},
            {26, 1.3149718643, 1.7056179198, 1.8218634184, 2.0555294386, 2.1620288734, 2.3787862662, 2.4786298236, 2.7787145333, 3.4349971816, 3.7066117435},
            {27, 1.3137029128, 1.7032884457, 1.8191263196, 2.0518305165, 2.1578248239, 2.3734172009, 2.472659912, 2.7706829571, 3.4210336212, 3.6895917135},
            {28, 1.3125267816, 1.7011309343, 1.8165918667, 2.0484071418, 2.1539348677, 2.3684517492, 2.467140098, 2.7632624555, 3.4081551784, 3.6739064007},
            {29, 1.3114336473, 1.6991270265, 1.8142383647, 2.0452296421, 2.1503250878, 2.3638460732, 2.4620213602, 2.7563859037, 3.3962402884, 3.6594050195},
            {30, 1.3104150254, 1.6972608866, 1.8120471079, 2.0422724563, 2.1469662791, 2.3595624587, 2.4572615424, 2.7499956536, 3.3851848668, 3.645958635},
            {35, 1.306211802, 1.6895724578, 1.8030237268, 2.0301079283, 2.1331566252, 2.3419692993, 2.4377225471, 2.7238055892, 3.3400452021, 3.5911467758},
            {40, 1.3030770526, 1.6838510133, 1.7963135933, 2.0210753903, 2.1229098196, 2.3289347676, 2.4232567793, 2.7044592674, 3.3068777141, 3.5509657609},
            {50, 1.2987136942, 1.6759050252, 1.7870012357, 2.0085591121, 2.1087212819, 2.3109139356, 2.4032719167, 2.6777932709, 3.2614090558, 3.4960128818},
            {60, 1.2958210935, 1.6706488649, 1.7808455585, 2.000297822, 2.0993628446, 2.299045582, 2.3901194726, 2.6602830289, 3.231709126, 3.4602004692},
            {70, 1.2937628979, 1.6669144791, 1.7764741818, 1.9944371118, 2.0927270053, 2.2906386286, 2.3808074823, 2.6479046238, 3.210789061, 3.4350145214},
            {80, 1.2922235831, 1.6641245786, 1.7732095349, 1.9900634213, 2.0877765978, 2.2843716179, 2.373868273, 2.6386905963, 3.1952576903, 3.4163374585},
            {90, 1.2910288987, 1.661961084, 1.7706785588, 1.9866745407, 2.0839418837, 2.2795197772, 2.3684974762, 2.6315651656, 3.1832708141, 3.4019353069},
            {100, 1.2900747613, 1.6602343261, 1.7686589233, 1.9839715185, 2.0808839013, 2.275652413, 2.3642173662, 2.6258905214, 3.1737394937, 3.3904913112},
            {150, 1.2872209136, 1.6550755002, 1.762627337, 1.9759053309, 2.071761868, 2.264125081, 2.3514645818, 2.6090025659, 3.145452532, 3.3565689817},
            {200, 1.285798794, 1.6525081009, 1.7596268401, 1.9718962236, 2.0672298802, 2.2584031839, 2.3451370823, 2.6006344362, 3.1314798142, 3.3398354063},
            {250, 1.2849470554, 1.6509714898, 1.7578314146, 1.9694983934, 2.0645199227, 2.2549833231, 2.3413561184, 2.5956376305, 3.123150229, 3.3298672057},
            {300, 1.2843798676, 1.6499486739, 1.7566364893, 1.9679030113, 2.06271712, 2.2527089278, 2.3388419238, 2.5923164108, 3.1176195538, 3.323251513},
            {400, 1.2836715996, 1.6486719415, 1.7551451065, 1.9659123432, 2.0604679212, 2.2498721172, 2.3357064133, 2.58817608, 3.1107312738, 3.3150152234},
            {500, 1.2832470207, 1.6479068539, 1.7542514877, 1.9647198375, 2.0591206932, 2.2481733216, 2.3338289554, 2.5856978351, 3.1066116243, 3.3100911515},
            {600, 1.2829641272, 1.6473971918, 1.7536562458, 1.963925622, 2.0582234944, 2.2470421597, 2.3325789171, 2.5840481469, 3.1038707237, 3.3068157878},
            {800, 1.282610689, 1.6467605594, 1.7529127597, 1.9629337387, 2.0571030669, 2.2456297448, 2.3310181671, 2.5819888161, 3.1004508077, 3.3027298317},
            {1000, 1.2823987215, 1.6463788173, 1.7524669698, 1.9623390808, 2.0564313811, 2.244783115, 2.3300826748, 2.5807546981, 3.0984021639, 3.3002826484},
    };

    /**
     * Evaluates the inverse of the cumulative normal distribution function
     * of {@code expected value = 0, variance = 1} for the given confidence level.
     *
     * @param p the confidence level in [0, 1].
     * @return the value that yields the percentage {@code p}.
     */
    private static double z1(double p) {
        int col = Arrays.binarySearch(z1[0], p);
        if (col <= 0) {
            throw new AssertionError("Cannot compute z1 for confidence level: " + p);
        }
        return z1[1][col];
    }

    /**
     * Evaluates the inverse of the cumulative Student's t-distribution function
     * for the given confidence level and degrees of freedom.
     *
     * @param p the confidence level in [0, 1].
     * @param n the degrees of freedom
     * @return the value that yields the percentage {@code p}.
     */
    private static double t1(double p, double n) {
        int col = Arrays.binarySearch(t1[0], p);
        if (col <= 0) {
            throw new AssertionError("Cannot compute t1 for confidence level: " + p);
        }

        int row = binarySearch(t1, n);
        if (row == 0) {
            return Double.POSITIVE_INFINITY;
        } else if (row > 0) {
            return t1[row][col];
        } else {
            row = -1 - row;
            if (row == t1.length) {
                return t1[row - 1][col];
            }
            double n0 = t1[row - 1][0];
            double n1 = t1[row][0];
            double t = (n - n0) / (n1 - n0);
            return t1[row][col] * (1 - t) + t1[row + 1][col] * t;
        }

    }

    /**
     * Same as Arrays.binarySearch but with double[][] type.
     */
    private static int binarySearch(double[][] a,
                                    double key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            double[] midVal = a[mid];
            int cmp = Double.compare(midVal[0], key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    /**
     * Returns the confidence value for a population mean using a
     * Student's t distribution.
     * <p>
     * This distribution should be used if the standard deviation of the
     * population is not known. For large sample sizes {@code size ≥ 30}
     * it converges to the normal distribution, see {@link #confidenceNorm}.
     *
     * @param alpha the significance level.
     *              The confidence level equals {@code 1 - alpha}.
     *              An alpha of 0.05 indicates a 95 percent confidence.
     *              Supported values: 0.05, 0.02, 0.01, 0.001.
     * @param stdev the standard deviation of the sample
     * @param size  the sample size
     * @return the value {@code c} for constructing the confidence interval
     * {@code [ mean - c , mean + c ] }.
     */
    public static double confidenceT(double alpha, double stdev, long size) {
        return t1(1.0 - alpha / 2, size - 1) * stdev / sqrt(size);
    }

    /**
     * Returns the confidence value for a population mean using a
     * Normal distribution.
     * <p>
     * This distribution should be used if the standard deviation of
     * the population is known. If it is unknown, then it should
     * be used for large sample sizes {@code size ≥ 30} only.
     * For smaller sample sizes with unknown standard deviation of the population
     * use {@link #confidenceT}.
     *
     * @param alpha the significance level.
     *              The confidence level equals {@code 1 - alpha}.
     *              An alpha of 0.05 indicates a 95 percent confidence.
     *              Supported values: 0.05, 0.02, 0.01, 0.001.
     * @param stdev the standard deviation of the population
     * @param size  the sample size
     * @return the value {@code c} for constructing the confidence interval
     * {@code [ mean - c , mean + c ] }.
     */
    public static double confidenceNorm(double alpha, double stdev, long size) {
        return z1(1.0 - alpha / 2) * stdev / sqrt(size);
    }

    /**
     * Returns the confidence value for a population mean using
     * the Student's t distribution for small samples and the
     * Normal distribution for large samples.
     * <p>
     * For sample sizes &lt; 30 the Student's t distribution is used.
     * For sample sizes ≥ 30 the Normal distribution is used.
     * <p>
     * The confidence interval can be constructed by
     * subtracting and adding the returned value {@code c} from the mean
     * value: {@code [mean - c, mean + c]}.
     * <p>
     *
     * @param alpha the significance level.
     *              The confidence level equals {@code 1 - alpha}.
     *              An alpha of 0.05 indicates a 95 percent confidence level.
     *              Supported values: 0.05, 0.02, 0.01, 0.001.
     * @param stdev the sample standard deviation
     * @param size  the sample size
     * @return the confidence value {@code c}
     */
    public static double confidence(double alpha, double stdev, long size) {
        if (size >= 30) {
            return Stats.confidenceNorm(alpha, stdev, size);
        } else {
            return Stats.confidenceT(alpha, stdev, size);
        }
    }
}
