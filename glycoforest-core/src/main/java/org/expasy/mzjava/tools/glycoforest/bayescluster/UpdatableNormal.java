package org.expasy.mzjava.tools.glycoforest.bayescluster;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;
import com.google.common.base.Preconditions;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.ms.cluster.SimEdge;

import java.util.Collection;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class UpdatableNormal implements UpdatableDistribution {

    private static final MersenneTwister mersenneTwister = new MersenneTwister();

    private final double priorMean;
    private final double varianceOfPriorMean;
    private final double priorPrecision;
    private final double knownStdev;
    private final double knownVariance;

    private final double minPosteriorMean;
    private final double maxPosteriorMean;

    private double posteriorMean;
    private double sampleMean;
    private double sampleStdev;

    private Normal normal;

    public UpdatableNormal(double priorMean, double varianceOfPriorMean, double knownVariance, double minPosteriorMean, double maxPosteriorMean) {

        if(Double.isNaN(priorMean))
            throw new IllegalArgumentException("prior mean has to be a number but was " + priorMean);
        if(Double.isInfinite(priorMean))
            throw new IllegalArgumentException("prior mean has to be finite but was " + priorMean);
        Preconditions.checkArgument(minPosteriorMean < maxPosteriorMean, "Min posterior mean has to be smaller than max posterior mean");
        Preconditions.checkArgument(minPosteriorMean <= priorMean && priorMean <= maxPosteriorMean, "UpdatableNormal mean has to be in the range minPosteriorMean <= priorMean <= maxPosteriorMean");

        this.priorMean = priorMean;
        this.varianceOfPriorMean = varianceOfPriorMean;
        this.priorPrecision = 1.0 / this.varianceOfPriorMean;
        this.knownStdev = Math.sqrt(knownVariance);
        this.knownVariance = knownVariance;
        this.minPosteriorMean = minPosteriorMean;
        this.maxPosteriorMean = maxPosteriorMean;

        this.posteriorMean = priorMean;
    }

    public UpdatableNormal(double priorMean, double varianceOfPriorMean, double knownVariance) {

        if(Double.isNaN(priorMean))
            throw new IllegalArgumentException("prior mean has to be a number but was " + priorMean);
        if(Double.isInfinite(priorMean))
            throw new IllegalArgumentException("prior mean has to be finite but was " + priorMean);

        this.priorMean = priorMean;
        this.varianceOfPriorMean = varianceOfPriorMean;
        this.priorPrecision = 1.0 / this.varianceOfPriorMean;
        this.knownStdev = Math.sqrt(knownVariance);
        this.knownVariance = knownVariance;
        this.minPosteriorMean = Double.MIN_VALUE;
        this.maxPosteriorMean = Double.MAX_VALUE;

        this.posteriorMean = priorMean;
    }

    public <V> void update(Collection<SimEdge<V>> instances){

        if (instances.isEmpty()) {

            posteriorMean = priorMean;
            sampleMean = 0;
            sampleStdev = 0;
            return;
        }

        SummaryStatistics statistics = new SummaryStatistics();
        for(SimEdge<V> edge : instances) {

            statistics.addValue(edge.getScore());
        }

        sampleMean = statistics.getMean();
        sampleStdev = statistics.getStandardDeviation();
        double n = statistics.getN();

        double posteriorVariance = 1 / (priorPrecision + n / knownVariance);
        posteriorMean = ((posteriorVariance / varianceOfPriorMean) * priorMean) + ((posteriorVariance / knownVariance) * n * sampleMean);
        if(posteriorMean > maxPosteriorMean)
            posteriorMean = maxPosteriorMean;
        else if(posteriorMean < minPosteriorMean)
            posteriorMean = minPosteriorMean;

        normal = new Normal(posteriorMean, knownStdev, mersenneTwister);
    }

    public double getSampleStandardDeviation() {

        return sampleStdev;
    }

    public double getSampleMean() {

        return sampleMean;
    }

    public double getPosteriorMean() {

        return posteriorMean;
    }

    public double getPosteriorStandardDeviation() {

        return knownStdev;
    }

    public double pdf(double score) {

        return normal.pdf(score);
    }
}
