package org.expasy.mzjava.tools.glycoforest.rtcluster;

import gnu.trove.map.hash.TObjectIntHashMap;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.AbstractClusterTopology;
import org.expasy.mzjava.tools.glycoforest.bayescluster.ClusterEdge;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableDistribution;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RTClusterTopology<V> extends AbstractClusterTopology<V, RTCluster<V>, RTClusterTopology<V>> {

    private final ToDoubleFunction<V> retentionTimeFunction;
    private final ToDoubleFunction<V> ticFunction;
    private final ToIntFunction<V> scanNumberFunction;

    private final List<ClusterEdge<V, RTCluster<V>>> adjacentEdges = new ArrayList<>();

    public RTClusterTopology(SimilarityGraph<V> graph,
                             UpdatableDistribution nodePrior, UpdatableDistribution edgePrior,
                             ToDoubleFunction<V> retentionTimeFunction,
                             ToDoubleFunction<V> ticFunction,
                             ToIntFunction<V> scanNumberFunction) {

        super(graph, nodePrior, edgePrior);

        this.retentionTimeFunction = retentionTimeFunction;
        this.ticFunction = ticFunction;
        this.scanNumberFunction = scanNumberFunction;
        buildGraph(graph);
        collectAdjacentEdges(adjacentEdges);
    }

    public RTClusterTopology(Collection<Set<V>> clusters, SimilarityGraph<V> graph,
                             UpdatableDistribution nodePrior, UpdatableDistribution edgePrior,
                             ToDoubleFunction<V> retentionTimeFunction,
                             ToDoubleFunction<V> ticFunction,
                             ToIntFunction<V> scanNumberFunction) {

        super(graph, nodePrior, edgePrior);

        this.retentionTimeFunction = retentionTimeFunction;
        this.ticFunction = ticFunction;
        this.scanNumberFunction = scanNumberFunction;
        buildGraph(clusters);
        collectAdjacentEdges(adjacentEdges);
    }

    public RTClusterTopology(List<RTCluster<V>> clusters, List<ClusterEdge<V, RTCluster<V>>> clusterEdges, SimilarityGraph<V> graph,
                             UpdatableDistribution nodePrior, UpdatableDistribution edgePrior,
                             ToDoubleFunction<V> retentionTimeFunction,
                             ToDoubleFunction<V> ticFunction,
                             ToIntFunction<V> scanNumberFunction) {

        super(clusters, clusterEdges, graph, nodePrior, edgePrior);

        this.retentionTimeFunction = retentionTimeFunction;
        this.ticFunction = ticFunction;
        this.scanNumberFunction = scanNumberFunction;
        collectAdjacentEdges(adjacentEdges);
    }

    private void collectAdjacentEdges(List<ClusterEdge<V, RTCluster<V>>> edges){

        Collections.sort(clusters, RTCluster::compareTo);
        TObjectIntHashMap<RTCluster> clusterRank = new TObjectIntHashMap<>(clusters.size());
        for (int i = 0; i < clusters.size(); i++) {

            clusterRank.put(clusters.get(i), i);
        }

        for(ClusterEdge<V, RTCluster<V>> edge : edgeList) {

            int rank1 = clusterRank.get(edge.getCluster1());
            int rank2 = clusterRank.get(edge.getCluster2());

            if(Math.abs(rank1 - rank2) == 1)
                edges.add(edge);
        }
    }

    @Override
    protected RTCluster<V> newCluster(Set<V> vertices) {

        return new RTCluster<>(vertices, getSimGraph(), getNodePrior(), retentionTimeFunction, ticFunction, scanNumberFunction);
    }

    @Override
    protected RTClusterTopology<V> newClusterTopology(List<RTCluster<V>> clusters, List<ClusterEdge<V, RTCluster<V>>> clusterEdges) {

        return new RTClusterTopology<>(clusters, clusterEdges, getSimGraph(), getNodePrior(), getEdgePrior(), retentionTimeFunction, ticFunction, scanNumberFunction);
    }

    @Override
    public List<ClusterEdge<V, RTCluster<V>>> getMergeableEdges(double minEdgeMean) {

        return adjacentEdges.stream().filter(clusterEdge -> clusterEdge.getSampleMean() >= minEdgeMean).collect(Collectors.toList());
    }

    public Stream<ClusterEdge<V, RTCluster<V>>> getAdjacentEdgeStream(){

        return adjacentEdges.stream();
    }

    public Stream<ClusterEdge<V, RTCluster<V>>> getEdgeStream(){

        return edgeList.stream();
    }
}

