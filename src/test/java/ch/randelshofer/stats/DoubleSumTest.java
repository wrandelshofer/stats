package ch.randelshofer.stats;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleSumTest {
    @TestFactory
    public List<DynamicTest> testAccept() {
        return Arrays.asList(
                /* Example from: https://en.wikipedia.org/wiki/Kahan_summation_algorithm#Further_enhancements */
                DynamicTest.dynamicTest("1", () -> doTestAccept(
                        new double[]{1.0, 10e100, 1.0, -10e100}, 2)
                )
        );
    }

    public void doTestAccept(double[] input,
                             double expectedSum) {
        DoubleSum stats = new DoubleSum();
        for (double d : input) {
            stats.accept(d);
        }
        System.out.println(stats);
        double actualSum = stats.getSum();

        assertEquals(expectedSum, actualSum, "sum");
    }

    @TestFactory
    public List<DynamicTest> testCombine() {
        return Arrays.asList(
                /* Example from: https://en.wikipedia.org/wiki/Kahan_summation_algorithm#Further_enhancements */
                DynamicTest.dynamicTest("1", () -> doTestCombine(
                        new double[]{1.0, 10e100, 1.0, -10e100}, 2)
                )
        );
    }

    public void doTestCombine(double[] input,
                              double expectedSum) {
        for (int i = 0; i < input.length - 1; i++) {
            DoubleSum statsA = new DoubleSum();
            DoubleSum statsB = new DoubleSum();

            for (int j = 0; j <= i; j++) {
                statsA.accept(input[j]);
            }
            for (int j = i + 1; j < input.length; j++) {
                statsB.accept(input[j]);
            }
            statsA.combine(statsB);

            double actualSum = statsA.getSum();
            assertEquals(expectedSum, actualSum, "sum i=" + i);
        }
    }
}
