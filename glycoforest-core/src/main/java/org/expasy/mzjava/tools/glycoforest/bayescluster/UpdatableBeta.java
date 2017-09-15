package org.expasy.mzjava.tools.glycoforest.bayescluster;

import cern.jet.random.Beta;
import cern.jet.random.engine.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;
import org.expasy.mzjava.core.ms.cluster.SimEdge;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class UpdatableBeta implements UpdatableDistribution {

    private static final MersenneTwister mersenneTwister = new MersenneTwister();

    public enum Type {VERTEX, EDGE}

    private final double priorMean;
    private final int priorN;
    private final Type type;

    private final Beta betaDist = new Beta(1, 1, mersenneTwister);
    private double alpha, beta;
    private double sampleMean = 0, sampleStdev = 0;

    public UpdatableBeta(double priorMean, int priorN, Type type) {

        checkArgument(priorN > 0);

        switch (type) {

            case VERTEX:

                checkArgument(priorMean >= 0.5 && priorMean <= 1);
                break;
            case EDGE:

                checkArgument(priorMean >= 0 && priorMean <= 0.5);
                break;

            default:

                throw new IllegalArgumentException("type cannot be " + type);
        }

        this.priorMean = priorMean;
        this.priorN = priorN;
        this.type = type;

        estimateBeta(priorMean);
    }

    private void estimateBeta(double mean) {

        switch (type) {

            case VERTEX:

                if(mean < 0.5)
                    mean = 0.5;

                alpha = mean / (-mean + 1);
                beta = 1;
                break;
            case EDGE:

                if(mean > 0.5)
                    mean = 0.5;

                alpha = 1;
                beta = (-mean + 1) / mean;
                break;
            default:

                throw new IllegalStateException("Can't estimate for " + type);
        }

        betaDist.setState(alpha, beta);
    }

    @Override
    public <V> void update(Collection<SimEdge<V>> instances) {

        if (instances.isEmpty()) {

            sampleMean = 0;
            sampleStdev = 0;
            return;
        }


        final SummaryStatistics stats = new SummaryStatistics();
        for (SimEdge<V> edge : instances) {

            stats.addValue(edge.getScore());
        }

        sampleMean = stats.getMean();
        sampleStdev = stats.getStandardDeviation();
        estimateBeta(((priorMean * priorN) + stats.getSum()) / (priorN + stats.getN()));
    }

    @Override
    public double pdf(double score) {

        final double pdf = betaDist.pdf(score);

        if(pdf == 0)
            return 0.00000000000001;
        else
            return pdf;
    }

    @Override
    public double getSampleMean() {

        return sampleMean;
    }

    @Override
    public double getSampleStandardDeviation() {

        return sampleStdev;
    }

    /**
     * <pre>
     * mean =  a
     *       -----
     *       a + b
     * </pre>
     * from http://www.wolframalpha.com/input/?i=beta+distribution
     *
     * @return the posterior mean
     */
    @Override
    public double getPosteriorMean() {

        return alpha / (alpha + beta);
    }

    /**
     * <pre>
     * var =          ab
     *       ---------------------
     *       (a + b)^2 (a + b + 1)
     * </pre>
     * from http://www.wolframalpha.com/input/?i=beta+distribution
     *
     * @return the posterior standard deviation
     */
    @Override
    public double getPosteriorStandardDeviation() {

        final double aPlusB = alpha + beta;
        return FastMath.sqrt((alpha * beta) / ((aPlusB * aPlusB) * (aPlusB + 1)));
    }
}
