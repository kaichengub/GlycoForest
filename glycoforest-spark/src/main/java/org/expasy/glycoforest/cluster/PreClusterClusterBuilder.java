package org.expasy.glycoforest.cluster;

import org.expasy.mzjava.core.ms.cluster.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Cluster builder that pre clusters using k-means clustering if the number of nodes to cluster is larger
 * than kMeansThreshold.
 * <p>
 * Before the pre clustering is performed all edges that are not accepted by the edge predicate are removed while
 * copying the graph. One use case of this is to remove edges between spectra that are separated by to much time
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PreClusterClusterBuilder<V> implements ClusterBuilder<V> {

    private final int kMeansThreshold;
    private final Predicate<SimEdge<V>> edgePredicate;
    private final KMeansPlusPlusClusterBuilder<V> kMeansClusterBuilder;
    private final ClusterBuilder<V> clusterBuilder2;

    public PreClusterClusterBuilder(int kMeansThreshold, Predicate<SimEdge<V>> edgePredicate, ClusterBuilder<V> clusterBuilder2, int numKMeansClusters) {

        this.kMeansThreshold = kMeansThreshold;
        this.edgePredicate = edgePredicate;
        this.kMeansClusterBuilder = new KMeansPlusPlusClusterBuilder<>(numKMeansClusters);
        this.clusterBuilder2 = clusterBuilder2;
    }

    @Override
    public Collection<Set<V>> cluster(SimilarityGraph<V> graph) {

        final Collection<Set<V>> clusters;
        if (graph.getVertexCount() > kMeansThreshold) {

            final SimilarityGraph<V> copy = copy(graph);
            Collection<Set<V>> intermediates = kMeansClusterBuilder.cluster(copy);
            clusters = clusterBuilder2.cluster(graph, intermediates);
        } else {

            clusters = clusterBuilder2.cluster(graph);
        }

        return clusters;
    }

    private SimilarityGraph<V> copy(SimilarityGraph<V> graph) {

        final DenseSimilarityGraph.Builder<V> builder = new DenseSimilarityGraph.Builder<>();

        graph.forEachVertex(builder::add);
        graph.forEachEdge(edge -> {

            if (edgePredicate.test(edge))
                builder.add(edge);
        });

        return builder.build();
    }

    @Override
    public Collection<Set<V>> cluster(SimilarityGraph<V> graph, Collection<Set<V>> startingClusters) {

        throw new UnsupportedOperationException();
    }
}
