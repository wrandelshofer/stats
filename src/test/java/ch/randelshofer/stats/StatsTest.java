package ch.randelshofer.stats;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsTest {
    public void doConfidenceNorm(double alpha, double stdev, int size, double expected) {
        double actual = Stats.confidenceNorm(alpha, stdev, size);
        System.out.println("confidenceNorm(" + alpha + "," + stdev + "," + size + "):" + actual);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> testConfidenceNorm() {
        return List.of(
                DynamicTest.dynamicTest("5%,1,5", () -> doConfidenceNorm(0.05, 1, 5, 0.8765225405586689)),
                DynamicTest.dynamicTest("5%,1,10", () -> doConfidenceNorm(0.05, 1, 10, 0.6197950322918954)),
                DynamicTest.dynamicTest("5%,1,30", () -> doConfidenceNorm(0.05, 1, 30, 0.3578388287361186)),
                DynamicTest.dynamicTest("5%,1,50", () -> doConfidenceNorm(0.05, 1, 50, 0.27718076486427107)),
                DynamicTest.dynamicTest("5%,3,5", () -> doConfidenceNorm(0.05, 3, 5, 2.6295676216760064)),
                DynamicTest.dynamicTest("2%,7,163", () -> doConfidenceNorm(0.02, 7, 163, 1.2754953978324377)),
                DynamicTest.dynamicTest("1%,7,163", () -> doConfidenceNorm(0.01, 7, 163, 1.4122816535461036)),
                DynamicTest.dynamicTest("0.1%,7,163", () -> doConfidenceNorm(0.001, 7, 163, 1.8041376138884646))
        );
    }


    public void doConfidenceT(double alpha, double stdev, int size, double expected) {
        double actual = Stats.confidenceT(alpha, stdev, size);
        System.out.println("confidenceT(" + alpha + "," + stdev + "," + size + "):" + actual);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> testConfidenceT() {
        return List.of(
                DynamicTest.dynamicTest("5%,1,5", () -> doConfidenceT(0.05, 1, 5, 1.241663998204751)),
                DynamicTest.dynamicTest("5%,1,10", () -> doConfidenceT(0.05, 1, 10, 0.7153569059712322)),
                DynamicTest.dynamicTest("5%,1,30", () -> doConfidenceT(0.05, 1, 30, 0.37340613675212914)),
                DynamicTest.dynamicTest("5%,1,50", () -> doConfidenceT(0.05, 1, 50, 0.28300166315079744)),
                DynamicTest.dynamicTest("5%,3,5", () -> doConfidenceT(0.05, 3, 5, 3.7249919946142525)),
                DynamicTest.dynamicTest("2%,7,163", () -> doConfidenceT(0.02, 7, 163, 1.2852996599024225)),
                DynamicTest.dynamicTest("1%,7,163", () -> doConfidenceT(0.01, 7, 163, 1.4252243506793107)),
                DynamicTest.dynamicTest("0.1%,7,163", () -> doConfidenceT(0.001, 7, 163, 1.8298609951126712))
        );
    }


}