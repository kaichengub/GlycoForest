package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.collect.Sets;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;

public class ClusterEdgeTest {

    @Test
    public void testEstimate() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);

        addEdge(0.559, "B1", "B2", builder);

        addEdge(0.430, "A1", "B1", builder);
        addEdge(0.498, "A1", "B2", builder);
        addEdge(0.570, "A2", "B1", builder);
        addEdge(0.537, "A2", "B2", builder);

        final DenseSimilarityGraph<String> graph = builder.build();


        final UpdatableNormal prior = new UpdatableNormal(0.1, 0.1 * 0.1, 0.05 * 0.05, 0.0, 1.0);
        Cluster<String> cluster1 = new Cluster<>(Sets.newHashSet("A1", "A2"), graph, prior);
        Cluster<String> cluster2 = new Cluster<>(Sets.newHashSet("B1", "B2"), graph, prior);
        ClusterEdge<String, Cluster<String>> clusterEdge = new ClusterEdge<>(cluster1, cluster2, graph, new UpdatableNormal(0.1, 0.1 * 0.1, 0.05 * 0.05, 0.0, 1.0));
        
        Assert.assertEquals(0.4671999999999999, clusterEdge.getEstimatedMean(), 0.00001);
        Assert.assertEquals(0.05, clusterEdge.getEstimatedStandardDeviation(), 0.00001);
    }

    @Test
    public void testEstimateSingleEdge() throws Exception {

        final DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        addEdge(0.430, "A1", "B1", builder);
        final DenseSimilarityGraph<String> graph = builder.build();


        final UpdatableNormal vertexPrior = new UpdatableNormal(0.9, 0.1 * 0.1, 0.05 * 0.05, 0.0, 1.0);
        Cluster<String> cluster1 = new Cluster<>(Sets.newHashSet("A1"), graph, vertexPrior);
        Cluster<String> cluster2 = new Cluster<>(Sets.newHashSet("B1"), graph, vertexPrior);
        ClusterEdge<String, Cluster<String>> clusterEdge = new ClusterEdge<>(cluster1, cluster2, graph, new UpdatableNormal(0.1, 0.1 * 0.1, 0.05 * 0.05, 0.0, 1.0));

        Assert.assertEquals(0.36399999999999993, clusterEdge.getEstimatedMean(), 0.00001);
        Assert.assertEquals(0.05, clusterEdge.getEstimatedStandardDeviation(), 0.00001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEstimateOnEmptyInstance() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);

        addEdge(0.559, "B1", "B2", builder);

        addEdge(0.430, "A1", "B1", builder);
        addEdge(0.498, "A1", "B2", builder);
        addEdge(0.570, "A2", "B1", builder);
        addEdge(0.537, "A2", "B2", builder);

        final DenseSimilarityGraph<String> graph = builder.build();

        Cluster<String> cluster1 = new Cluster<>(Collections.<String>emptySet(), graph, mock(UpdatableNormal.class));
        Cluster<String> cluster2 = new Cluster<>(Collections.<String>emptySet(), graph, mock(UpdatableNormal.class));
        new ClusterEdge<>(cluster1, cluster2, graph, new UpdatableNormal(0.1, 0.01 * 0.01, 0.1 * 0.1, 0.0, 1.0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testResetInstances() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);

        addEdge(0.559, "B1", "B2", builder);

        SimEdge<String> edgeA1_B1 = addEdge(0.430, "A1", "B1", builder);
        SimEdge<String> edgeA1_B2 = addEdge(0.498, "A1", "B2", builder);
        SimEdge<String> edgeA2_B1 = addEdge(0.570, "A2", "B1", builder);
        SimEdge<String> edgeA2_B2 = addEdge(0.537, "A2", "B2", builder);

        final DenseSimilarityGraph<String> graph = builder.build();


        Cluster<String> cluster1 = new Cluster<>(Sets.newHashSet("A1", "A2"), graph, new UpdatableNormal(0.85, 1.0E-4, 0.01, 0.0, 1.0));
        Cluster<String> cluster2 = new Cluster<>(Sets.newHashSet("B1", "B2"), graph, new UpdatableNormal(0.85, 1.0E-4, 0.01, 0.0, 1.0));
        ClusterEdge<String, Cluster<String>> clusterEdge = new ClusterEdge<>(cluster1, cluster2, graph, new UpdatableNormal(0.1, 0.01 * 0.01, 0.1 * 0.1, 0.0, 1.0));

        final Set<SimEdge<String>> edgeSet = new HashSet<>();
        clusterEdge.forEachInstance(edgeSet::add);
        Assert.assertEquals(Sets.newHashSet(edgeA1_B1, edgeA1_B2, edgeA2_B1, edgeA2_B2), edgeSet);
    }

    private SimEdge<String> addEdge(double score, String node1, String node2, DenseSimilarityGraph.Builder<String> builder) {

        return builder.add(node1, node2, score);
    }
}