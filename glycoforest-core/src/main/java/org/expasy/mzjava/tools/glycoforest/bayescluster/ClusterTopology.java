package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ClusterTopology<V> extends AbstractClusterTopology<V, Cluster<V>, ClusterTopology<V>> {

    public ClusterTopology(SimilarityGraph<V> graph, UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(graph, nodePrior, edgePrior);
        buildGraph(graph);
    }

    public ClusterTopology(Collection<Set<V>> clusters, SimilarityGraph<V> graph, UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(graph, nodePrior, edgePrior);
        buildGraph(clusters);
    }

    public ClusterTopology(List<Cluster<V>> clusters, List<ClusterEdge<V, Cluster<V>>> clusterEdges, SimilarityGraph<V> graph, UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(clusters, clusterEdges, graph, nodePrior, edgePrior);
    }

    @Override
    protected Cluster<V> newCluster(Set<V> vertices) {

        return new Cluster<>(vertices, getSimGraph(), getNodePrior());
    }

    @Override
    protected ClusterTopology<V> newClusterTopology(List<Cluster<V>> clusters, List<ClusterEdge<V, Cluster<V>>> clusterEdges) {

        return new ClusterTopology<>(clusters, clusterEdges, getSimGraph(), getNodePrior(), getEdgePrior());
    }
}

