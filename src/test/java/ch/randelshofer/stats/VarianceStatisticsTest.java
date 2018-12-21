package ch.randelshofer.stats;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.DoubleStream;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VarianceStatisticsTest {
    @TestFactory
    public List<DynamicTest> testConfidenceInterval() {
        return List.of(
                DynamicTest.dynamicTest("10", () -> doConfidenceInterval(
                        new double[]{
                                200.0, 171.0, 176.0, 194.0, 148.0, 203.0, 182.0, 186.0, 176.0, 161.0}
                        , 179.7, 266.21000000000276,
                        295.78888888889276                    ,12.303075868006456)
                ),
                DynamicTest.dynamicTest("30", () -> doConfidenceInterval(
                        new double[]{
                                200.0, 171.0, 176.0, 194.0, 148.0, 203.0, 182.0, 186.0, 176.0, 161.0,
                                159.0, 198.0, 167.0, 164.0, 186.0, 150.0, 190.0, 171.0, 190.0, 145.0,
                                164.0, 186.0, 175.0, 202.0, 199.0, 161.0, 150.0, 209.0, 164.0, 149.0}
                        , 175.86666666666667, 342.1155555555524,
                        353.91264367815717                    ,6.731866622656419)
                )
        );
    }

    public void doConfidenceInterval(double[] samples,
                                     double mean,
                                     double varPop,
                                     double var,
                                     double confidence) {
        VarianceStatistics stats = DoubleStream.of(samples).collect(VarianceStatistics::new, VarianceStatistics::accept, VarianceStatistics::combine);
        System.out.println(stats);
        double actualMean = stats.getAverage();
        double actualVariance=stats.getSampleVariance();
        double actualPopVariance=stats.getPopulationVariance();
        double actualStdev=stats.getSampleStandardDeviation();
        double actualPopStdev=stats.getPopulationStandardDeviation();
        double actualConfidence=Stats.confidence(0.05,actualStdev,stats.getCount());

        assertEquals(mean,actualMean,"mean");
        assertEquals(varPop,actualPopVariance,"variance of population");
        assertEquals(var,actualVariance,"variance");
        assertEquals(sqrt(varPop),actualPopStdev,"stdev of population");
        assertEquals(sqrt(var),actualStdev,"stdev");
        assertEquals(confidence,actualConfidence,"confidence");
    }

}