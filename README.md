# stats
Provides classes for calculating statistics from Java streams.

##DoubleSum

Computes the sum of doubles with the Neumaier compensation algorithm.

Usage with a double stream:

    DoubleSum stats = doubleStream.collect(DoubleSum::new,
                                           DoubleSum::accept,
                                           DoubleSum::combine);

    double sum = stats.getSum();

##VarianceStatistics

This collector computes sample variance and population variance in
addition to the values computed by `java.util.DoubleSummaryStatistics`.

Usage with a double stream:

    VarianceStatistics stats = doubleStream.collect(VarianceStatistics::new,
                                                    VarianceStatistics::accept,
                                                    VarianceStatistics::combine);
    int    count  = stats.getCount();
    double sum    = stats.getSum();
    double min    = stats.getMin();
    double avg    = stats.getAverage();
    double max    = stats.getMax();
    double sstdev = stats.getSampleStandardDeviation();
    double pstdev = stats.getPopulationStandardDeviation();

##Stats

Class with static utility functions.

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
    public static double confidenceT(double alpha, double stdev, long size);

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
    public static double confidenceNorm(double alpha, double stdev, long size);

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
    public static double confidence(double alpha, double stdev, long size);