package org.expasy.mzjava.tools.glycoforest.wrcluster;

import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.*;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WRClusterTopology extends AbstractClusterTopology<WithinRunConsensus, Cluster<WithinRunConsensus>, WRClusterTopology> {

    public WRClusterTopology(SimilarityGraph<WithinRunConsensus> simGraph, UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(simGraph, nodePrior, edgePrior);
        buildGraph(simGraph);
    }

    public WRClusterTopology(Collection<Set<WithinRunConsensus>> clusters, SimilarityGraph<WithinRunConsensus> graph, UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(graph, nodePrior, edgePrior);
        buildGraph(clusters);
    }

    public WRClusterTopology(List<Cluster<WithinRunConsensus>> clusters, List<ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>>> clusterEdges, SimilarityGraph<WithinRunConsensus> simGraph,
                             UpdatableDistribution nodePrior, UpdatableDistribution edgePrior) {

        super(clusters, clusterEdges, simGraph, nodePrior, edgePrior);
    }

    @Override
    protected Cluster<WithinRunConsensus> newCluster(Set<WithinRunConsensus> vertices) {

        return new Cluster<>(vertices, getSimGraph(), getNodePrior());
    }

    @Override
    protected WRClusterTopology newClusterTopology(List<Cluster<WithinRunConsensus>> clusters, List<ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>>> clusterEdges) {

        return new WRClusterTopology(clusters, clusterEdges, getSimGraph(), getNodePrior(), getEdgePrior());
    }

    @Override
    public List<ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>>> getMergeableEdges(double minEdgeMean) {

        return edgeList.stream()
                .filter(clusterEdge -> clusterEdge.getSampleMean() >= minEdgeMean && canMerge(clusterEdge))
                .collect(Collectors.toList());
    }

    private boolean canMerge(ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>> clusterEdge) {

        final Set<UUID> v1RunIds = clusterEdge.getCluster1().getVerticeStream().map(WithinRunConsensus::getRunId).collect(Collectors.toSet());
        final Set<UUID> v2RunIds = clusterEdge.getCluster2().getVerticeStream().map(WithinRunConsensus::getRunId).collect(Collectors.toSet());

        v1RunIds.retainAll(v2RunIds);
        return v1RunIds.isEmpty();
    }
}
