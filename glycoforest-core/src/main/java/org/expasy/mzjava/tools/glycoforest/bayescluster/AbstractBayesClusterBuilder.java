package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class AbstractBayesClusterBuilder<V, C extends Cluster<V>, CT extends AbstractClusterTopology<V, C, CT>> implements ClusterBuilder<V> {

    protected final UpdatableDistribution clusterPrior;
    protected final UpdatableDistribution edgePrior;
    protected final double edgeJoinThreshold;

    public AbstractBayesClusterBuilder(UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior, double edgeJoinThreshold) {

        this.edgePrior = edgePrior;
        this.clusterPrior = clusterPrior;
        this.edgeJoinThreshold = edgeJoinThreshold;
    }

    public Collection<Set<V>> cluster(SimilarityGraph<V> graph) {

        CT clusterTopology = newClusterTopology(graph, clusterPrior, edgePrior);

        if (graph.getVertexCount() > 1 && graph.getEdgeCount() > 0) {

            clusterTopology = runCluster(clusterTopology);
        }
        return clusterTopology.createVertexSets();
    }

    public Collection<Set<V>> cluster(SimilarityGraph<V> graph, Collection<Set<V>> startingClusters) {

        CT clusterTopology = newClusterTopology(startingClusters, graph, clusterPrior, edgePrior);

        if (graph.getVertexCount() > 1) {

            clusterTopology = runCluster(clusterTopology);
        }
        return clusterTopology.createVertexSets();
    }

    protected abstract CT newClusterTopology(Collection<Set<V>> startingClusters, SimilarityGraph<V> graph,
                                             UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior);

    protected abstract CT newClusterTopology(SimilarityGraph<V> graph,
                                             UpdatableDistribution clusterPrior, UpdatableDistribution edgePrior);

    protected CT runCluster(CT clusterTopology) {

        double bic = clusterTopology.calcBIC();
        while (clusterTopology.getClusterCount() > 1) {


            List<ClusterEdge<V, C>> edgeList = clusterTopology.getMergeableEdges(edgeJoinThreshold);
            Collections.sort(edgeList, (e1, e2) -> Double.compare(e2.getSampleMean(), e1.getSampleMean()));
            boolean changed = false;
            for (ClusterEdge<V, C> edge : edgeList) {

                final CT newClusterTopology = clusterTopology.combine(edge);
                final double newBIC = newClusterTopology.calcBIC();
                if (newBIC > bic) {

                    bic = newBIC;
                    clusterTopology = newClusterTopology;
                    changed = true;
                    break;
                }
            }

            if(!changed)
                break;
        }

        return clusterTopology;
    }
}
