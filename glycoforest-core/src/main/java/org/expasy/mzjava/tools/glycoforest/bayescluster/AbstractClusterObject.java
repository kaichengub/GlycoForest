package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.apache.commons.math3.util.FastMath;
import org.expasy.mzjava.core.ms.cluster.SimEdge;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class AbstractClusterObject<V> {

    protected final Collection<SimEdge<V>> edges;

    private double estimatedStdev;
    private double estimatedMean;

    private final double sampleMean;
    private final double sampleStdev;
    private final double sumOfLogLikelihoods;

    protected AbstractClusterObject(Collection<SimEdge<V>> edges, UpdatableDistribution prior, double maxMean) {

        checkNotNull(edges);
        checkNotNull(prior);

        this.edges = edges;
        prior.update(edges);

        sampleMean = prior.getSampleMean();
        sampleStdev = prior.getSampleStandardDeviation();
        estimatedMean = Math.min(prior.getPosteriorMean(), maxMean);
        estimatedStdev = prior.getPosteriorStandardDeviation();

        double sumOfLogs = 0;
        for (SimEdge<V> edge : edges) {

            double likelihood = prior.pdf(edge.getScore());

            if(likelihood < 0) {

                throw new IllegalStateException("likelihood is < 0, was " + likelihood);
            } else if(!Double.isFinite(likelihood)) {

                throw new IllegalStateException("likelihood is not finite, was " + likelihood);
            }

            sumOfLogs += FastMath.log(likelihood);

            if(!Double.isFinite(sumOfLogs))
                throw new IllegalStateException("sum of logs is not finite, was " + sumOfLogs);
        }
        sumOfLogLikelihoods = sumOfLogs;
    }

    public double getSumOfLogLikelihoods() {

        return sumOfLogLikelihoods;
    }

    public double getEstimatedMean() {

        return estimatedMean;
    }

    public double getEstimatedStandardDeviation() {

        return estimatedStdev;
    }

    public Stream<SimEdge<V>> edgeStream(){

        return edges.stream();
    }

    public void forEachInstance(Consumer<SimEdge<V>> consumer) {

        edges.forEach(consumer::accept);
    }

    public double getSampleMean() {

        return sampleMean;
    }

    public double getSampleStandardDeviation() {

        return sampleStdev;
    }

    public int getEdgeCount() {

        return edges.size();
    }
}
