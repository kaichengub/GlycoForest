package org.expasy.mzjava.tools.glycoforest.rtcluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.AbstractBayesClusterBuilder;
import org.expasy.mzjava.tools.glycoforest.bayescluster.ClusterEdge;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableDistribution;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

/**
 * Bayes cluster builder that constrains the clusters that can be merged using the retention time.
 * So that only clusters that are adjacent in retention time are merged.
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RTBayesClusterBuilder<V> extends AbstractBayesClusterBuilder<V, RTCluster<V>, RTClusterTopology<V>> {

    private final ToDoubleFunction<V> retentionTimeFunction;
    private final ToDoubleFunction<V> ticFunction;
    private final ToIntFunction<V> scanNumberFunction;

    public RTBayesClusterBuilder(UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior, double edgeJoinThreshold,
                                 ToDoubleFunction<V> retentionTimeFunction,
                                 ToDoubleFunction<V> ticFunction,
                                 ToIntFunction<V> scanNumberFunction) {

        super(clusterPrior, edgePrior, edgeJoinThreshold);
        this.retentionTimeFunction = retentionTimeFunction;
        this.ticFunction = ticFunction;
        this.scanNumberFunction = scanNumberFunction;
    }

    @Override
    protected RTClusterTopology<V> newClusterTopology(Collection<Set<V>> startingClusters, SimilarityGraph<V> graph, UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior) {

        return new RTClusterTopology<>(startingClusters, graph, clusterPrior, edgePrior, retentionTimeFunction, ticFunction, scanNumberFunction);
    }

    @Override
    protected RTClusterTopology<V> newClusterTopology(SimilarityGraph<V> graph, UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior) {

        return new RTClusterTopology<>(graph, clusterPrior, edgePrior, retentionTimeFunction, ticFunction, scanNumberFunction);
    }

    protected RTClusterTopology<V> runCluster(RTClusterTopology<V> clusterTopology) {

        clusterTopology = bayesCluster(clusterTopology);
        clusterTopology = mergeSingletons(clusterTopology);
//        clusterTopology = mergeAdjacent(clusterTopology);
//        clusterTopology = bayesCluster(clusterTopology);

        return clusterTopology;
    }

    private RTClusterTopology<V> mergeSingletons(RTClusterTopology<V> clusterTopology) {

        Optional<ClusterEdge<V, RTCluster<V>>> first;
        do {

            first = clusterTopology.getAdjacentEdgeStream()
                    .filter(edge -> edge.getCluster1().getVertexCount() <= 1 || edge.getCluster2().getVertexCount() <= 1)
                    .filter(edge -> getScanNumberDelta(edge) <= 10)     //todo add as parameter
                    .sorted((e1, e2) -> Double.compare(e2.getSampleMean(), e1.getSampleMean()))
                    .findFirst();
            if (first.isPresent())
                clusterTopology = clusterTopology.combine(first.get());
        } while (first.isPresent());
        return clusterTopology;
    }

    private RTClusterTopology<V> mergeAdjacent(RTClusterTopology<V> clusterTopology) {

        Optional<ClusterEdge<V, RTCluster<V>>> first;
        do {

            first = clusterTopology.getAdjacentEdgeStream()
                    .filter(edge -> getScanNumberDelta(edge) <= 10)     //todo add as parameter
//                    .filter(edge -> edge.getSampleMean() >= 0.95 || edge.getSampleMean() >= Math.max(edge.getCluster1().getSampleMean(), edge.getCluster2().getSampleMean()))
                    .filter(edge -> edge.getSampleMean() >= Math.min(edge.getCluster1().getSampleMean(), edge.getCluster2().getSampleMean()))
                    .sorted((e1, e2) -> Double.compare(e2.getSampleMean(), e1.getSampleMean()))
                    .findFirst();
            if(first.isPresent())
                clusterTopology = clusterTopology.combine(first.get());
        } while (first.isPresent());
        return clusterTopology;
    }

    private RTClusterTopology<V> bayesCluster(RTClusterTopology<V> clusterTopology) {

        double bic = clusterTopology.calcBIC();
        while (clusterTopology.getClusterCount() > 1) {

            final List<ClusterEdge<V, RTCluster<V>>> edgeList = clusterTopology.getMergeableEdges(edgeJoinThreshold);
//            Collections.sort(edgeList, (e1, e2) -> Double.compare(getTIC(e2), getTIC(e1)));
            Collections.sort(edgeList, (e1, e2) -> Double.compare(e2.getSampleMean(), e1.getSampleMean()));
            boolean changed = false;
            for (ClusterEdge<V, RTCluster<V>> edge : edgeList) {

                final RTClusterTopology<V> newClusterTopology = clusterTopology.combine(edge);
                final double newBIC = newClusterTopology.calcBIC();
                if (newBIC > bic) {

//                    System.out.println("Joined\n\t" +
//                            edge +
//                            "\n\t" + edge.getCluster1() +
//                            "\n\t" + edge.getCluster2() +
//                            "\n\tbic " + bic + " -> " + newBIC + "\n");                      //sout
                    bic = newBIC;
                    clusterTopology = newClusterTopology;
                    changed = true;
                    break;
                }
            }

            if (!changed)
                break;
        }
        return clusterTopology;
    }

    private double getTIC(ClusterEdge<V, RTCluster<V>> edge) {

        RTCluster<V> clusterA = edge.getCluster1();
        RTCluster<V> clusterB = edge.getCluster2();

        if (clusterA.getMeanRt() < clusterB.getMeanRt()) {

            return clusterA.getTicLastNode() + clusterB.getTicFirstNode();
        } else {

            return clusterB.getTicLastNode() + clusterA.getTicFirstNode();
        }
    }

    private int getScanNumberDelta(ClusterEdge<V, RTCluster<V>> edge) {

        RTCluster<V> clusterA = edge.getCluster1();
        RTCluster<V> clusterB = edge.getCluster2();

        final int delta;
        if (clusterA.getMeanScanNumber() < clusterB.getMeanScanNumber()) {

            delta = clusterB.getScanNumberFirstNode() - clusterA.getScanNumberLastNode();
        } else {

            delta = clusterA.getScanNumberFirstNode() - clusterB.getScanNumberLastNode();
        }

        return delta;
    }
}