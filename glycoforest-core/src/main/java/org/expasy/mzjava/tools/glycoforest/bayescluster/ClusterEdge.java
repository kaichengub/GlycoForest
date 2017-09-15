package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.base.Optional;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ClusterEdge<V, C extends Cluster<V>> extends AbstractClusterObject<V> {

    private final C cluster1, cluster2;

    public ClusterEdge(C cluster1, C cluster2, SimilarityGraph<V> simGraph, UpdatableDistribution prior) {

        super(extractEdges(cluster1, cluster2, simGraph), prior, Math.min(cluster1.getEstimatedMean(), cluster2.getEstimatedMean()));

        checkNotNull(cluster1);
        checkNotNull(cluster2);
        checkArgument(!cluster1.isEmpty());
        checkArgument(!cluster2.isEmpty());
        checkNotNull(prior);

        this.cluster1 = cluster1;
        this.cluster2 = cluster2;
    }

    private static <V, C extends Cluster<V>> List<SimEdge<V>> extractEdges(C cluster1, C cluster2, SimilarityGraph<V> graph) {

        checkNotNull(cluster1);
        checkNotNull(cluster2);
        checkNotNull(graph);

        List<SimEdge<V>> edges = new ArrayList<>(cluster1.getVertexCount() * cluster2.getVertexCount());

        for (V vertex1 : cluster1.vertexIterable()) {

            for (V vertex2 : cluster2.vertexIterable()) {

                Optional<SimEdge<V>> edgeOpt = graph.findEdge(vertex1, vertex2);
                final SimEdge<V> edge = edgeOpt.isPresent() ? edgeOpt.get() : new SimEdge<>(vertex1, vertex2, 0.0);
                edges.add(edge);
            }
        }

        return edges;
    }

    public C getCluster1() {

        return cluster1;
    }

    public C getCluster2() {

        return cluster2;
    }

    public boolean contains(C cluster) {

        return cluster1.equals(cluster) || cluster2.equals(cluster);
    }

    @Override
    public String toString() {

        NumberFormat format = NumberFormat.getNumberInstance();
        return "ClusterEdge{"
                + format.format(getSampleMean()) + ", " + format.format(getSampleStandardDeviation()) + ", " + format.format(getEstimatedMean()) + ", " + format.format(getEstimatedStandardDeviation()) + "; " + edges.size()
                + "}";
    }
}

