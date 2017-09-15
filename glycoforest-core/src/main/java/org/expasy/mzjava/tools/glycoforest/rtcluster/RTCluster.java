package org.expasy.mzjava.tools.glycoforest.rtcluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.Cluster;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableDistribution;

import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RTCluster<V> extends Cluster<V> implements Comparable<RTCluster<V>> {

    private final double minRt;
    private final double maxRt;
    private final double meanRt;

    private final double ticFirstNode, ticLastNode;
    private final int scanNumberFirstNode, scanNumberLastNode;

    public RTCluster(Set<V> vertices, SimilarityGraph<V> simGraph, UpdatableDistribution prior, ToDoubleFunction<V> retentionTimeFunction, ToDoubleFunction<V> ticFunction, ToIntFunction<V> scanNumberFunction) {

        super(vertices, simGraph, prior);

        double currMinRt = Double.MAX_VALUE;
        double currMaxRt = 0;
        V first = null;
        V last = null;
        for (V vertex : vertices) {

            double rt = retentionTimeFunction.applyAsDouble(vertex);

            if(rt < currMinRt) {

                currMinRt = rt;
                first = vertex;
            }
            if(rt > currMaxRt) {

                currMaxRt = rt;
                last = vertex;
            }
        }
        minRt = currMinRt;
        maxRt = currMaxRt;
        meanRt = (maxRt + minRt)/2.0;
        ticFirstNode = ticFunction.applyAsDouble(first);
        ticLastNode = ticFunction.applyAsDouble(last);
        scanNumberFirstNode = scanNumberFunction.applyAsInt(first);
        scanNumberLastNode = scanNumberFunction.applyAsInt(last);
    }

    @Override
    public int compareTo(RTCluster<V> that) {

        return Double.compare(meanRt, that.meanRt);
    }

    public double getTicFirstNode() {

        return ticFirstNode;
    }

    public double getTicLastNode() {

        return ticLastNode;
    }

    public double getMeanRt() {

        return meanRt;
    }

    public int getScanNumberFirstNode() {

        return scanNumberFirstNode;
    }

    public int getScanNumberLastNode() {

        return scanNumberLastNode;
    }

    public int getMeanScanNumber() {

        return (int)Math.round((scanNumberFirstNode + scanNumberLastNode)/2.0);
    }

    @Override
    public String toString() {

        return "RTCluster{" +
                "scan interval = " + scanNumberFirstNode +
                " - " + scanNumberLastNode +
                ", super = " + super.toString() +
                '}';
    }
}
