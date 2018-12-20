/* @(#)SampleStatistics.java
 * Copyright (c) 2018 Werner Randelshofer. MIT License.
 */
package ch.randelshofer.stats;

import java.util.DoubleSummaryStatistics;

import static java.lang.Math.sqrt;

/**
 * Computes statistics for samples taken from a population of independent
 * random variables.
 * <p>
 * Usage with a double stream:
 * <pre>
 * SampleStatistics stats = doubleStream.collect(SampleStatistics::new,
 *                                               SampleStatistics::accept,
 *                                               SampleStatistics::combine);
 * </pre>
 *
 * <p>
 * References:
 * <ul>
 * <li>Andy Georges, Dries Buytaert, Lieven Eeckhout. (2007).<br>
 * Statistically Rigorous Java Performance Evaluation.<br>
 * Department of Electronics and Information Systems, Ghent University, Belgium.<br>
 * <a href="https://dri.es/files/oopsla07-georges.pdf">link</a>.</li>
 * <li>https://stackoverflow.com/questions/36263352/java-streams-standard-deviation</li>
 * <li>Student t-Distribution.<br>
 * Wikipedia.<br>
 * <a href="https://de.wikipedia.org/wiki/Studentsche_t-Verteilung">link</a></li>
 * <li>Algorithms for calculating variance.<br>
 * Wikipedia.
 * <a href="https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Computing_shifted_data">link</a>
 * </li>
 * </ul>
 * </p>
 */
public class SampleStatistics extends DoubleSummaryStatistics {
    /** We use e DoubleSummaryStatistics here, because it can sum
     * doubles with compensation.
     */
    private DoubleSum sumOfSquare = new DoubleSum();

    /**
     * Adds a value to the sample.
     *
     * @param value a new value
     */
    @Override
    public void accept(double value) {
        super.accept(value);
        sumOfSquare.accept(value*value);
    }

    /**
     * Combines the state of another {@code SampleStatistics} into this one.
     *
     * @param other another {@code SampleStatistics}
     * @return this
     */
    public SampleStatistics combine(SampleStatistics other) {
        super.combine(other);
        sumOfSquare.combine(other.sumOfSquare);
        return this;
    }

    /**
     * Returns the sum of square of the sample.
     *
     * @return the sum of square
     */
    public double getSumOfSquare() {
        return sumOfSquare.getSum();
    }

    /**
     * Returns the (unbiased) variance {@code s^2} of the sample.
     *
     * @return the variance of the sample
     */
    public double getVariance() {
        double avg = getAverage();
        long n = getCount();
        return n > 0 ? (getSumOfSquare() - avg * avg * n) / (n - 1) : 0.0d;
    }

    /**
     * Returns the standard deviation {@code stdev} of the sample.
     *
     * @return the standard deviation of the sample
     */
    public double getStandardDeviation() {
        return sqrt(getVariance());
    }

    /**
     * Returns the variance {@code s^2} of the population.
     * <p>
     * Use this method only if the entire population has been sampled.
     *
     * @return the variance of the population
     */
    public double getPopulationVariance() {
        double avg = getAverage();
        long n = getCount();
        return n > 0 ? (getSumOfSquare() / n) - avg * avg : 0.0d;
    }

    /**
     * Returns the standard deviation {@code stdev} of the population.
     * <p>
     * Use this method only if the entire population has been sampled.
     *
     * @return the standard deviation of the population
     */
    public double getPopulationStandardDeviation() {
        return sqrt(getPopulationVariance());
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
     * @return the confidence value {@code c}
     */
    public double getConfidence(double alpha) {
        long n = getCount();
        double stdev = getStandardDeviation();

        if (n >= 30) {
            return Stats.confidenceNorm(alpha, stdev, n);
        } else {
            return Stats.confidenceT(alpha, stdev, n);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%f, min=%f, avg=%f, max=%f, stdev=%f, conf95%%=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax(),
                getStandardDeviation(),
                getConfidence(0.05)
        );
    }
}
