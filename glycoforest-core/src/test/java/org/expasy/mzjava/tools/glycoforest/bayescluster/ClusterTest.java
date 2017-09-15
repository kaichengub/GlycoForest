package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.collect.Sets;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ClusterTest {

    @Test
    public void testEstimate() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);
        addEdge(0.798, "A1", "A3", builder);
        addEdge(0.968, "A2", "A3", builder);

        addEdge(0.559, "B1", "B2", builder);
        addEdge(0.591, "B1", "B3", builder);
        addEdge(0.498, "B2", "B3", builder);

        addEdge(0.430, "A1", "B1", builder);
        addEdge(0.498, "A1", "B2", builder);
        addEdge(0.405, "A1", "B3", builder);
        addEdge(0.570, "A2", "B1", builder);
        addEdge(0.537, "A2", "B2", builder);
        addEdge(0.505, "A2", "B3", builder);
        addEdge(0.495, "A3", "B1", builder);
        addEdge(0.567, "A3", "B2", builder);
        addEdge(0.467, "A3", "B3", builder);
        final DenseSimilarityGraph<String> graph = builder.build();

        Cluster<String> vertex = new Cluster<>(Sets.newHashSet("A1"), graph, new UpdatableNormal(0.85, 0.01, 1.0E-4, 0.0, 1.0));

        //Test with no edges
        Assert.assertEquals(0.85, vertex.getEstimatedMean(), 0.000000001);
        Assert.assertEquals(0.01, vertex.getEstimatedStandardDeviation(), 0.000000001);

        //Test with one edge
        vertex = new Cluster<>(Sets.newHashSet("A1", "A2"), graph, new UpdatableNormal(0.85, 0.01, 1.0E-4, 0.0, 1.0));
        Assert.assertEquals(0.8400990099009901, vertex.getEstimatedMean(), 0.000000001);
        Assert.assertEquals(0.01, vertex.getEstimatedStandardDeviation(), 0.000000001);

        //Test with three edges
        vertex = new Cluster<>(Sets.newHashSet("A1", "A2", "A3"), graph, new UpdatableNormal(0.85, 0.01, 1.0E-4, 0.0, 1.0));

        Assert.assertEquals(0.8686046511627907, vertex.getEstimatedMean(), 0.000000001);
        Assert.assertEquals(0.01, vertex.getEstimatedStandardDeviation(), 0.000000001);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInstances() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        SimEdge<String> edgeA1_A2 = addEdge(0.840, "A1", "A2", builder);
        SimEdge<String> edgeA1_A3 = addEdge(0.798, "A1", "A3", builder);
        SimEdge<String> edgeA2_A3 = addEdge(0.968, "A2", "A3", builder);

        addEdge(0.559, "B1", "B2", builder);
        addEdge(0.591, "B1", "B3", builder);
        addEdge(0.498, "B2", "B3", builder);

        addEdge(0.430, "A1", "B1", builder);
        addEdge(0.498, "A1", "B2", builder);
        addEdge(0.405, "A1", "B3", builder);
        addEdge(0.570, "A2", "B1", builder);
        addEdge(0.537, "A2", "B2", builder);
        addEdge(0.505, "A2", "B3", builder);
        addEdge(0.495, "A3", "B1", builder);
        addEdge(0.567, "A3", "B2", builder);
        addEdge(0.467, "A3", "B3", builder);
        final DenseSimilarityGraph<String> graph = builder.build();

        Cluster<String> vertex = new Cluster<>(Sets.newHashSet("A1"), graph, new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0));

        final Set<SimEdge<String>> edges = new HashSet<>();
        vertex.forEachInstance(edges::add);
        Assert.assertEquals(Collections.<SimEdge<String>>emptySet(), edges);

        vertex = new Cluster<>(Sets.newHashSet("A1", "A2"), graph, new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0));
        edges.clear();
        vertex.forEachInstance(edges::add);
        Assert.assertEquals(Sets.newHashSet(edgeA1_A2), edges);

        vertex = new Cluster<>(Sets.newHashSet("A1", "A2", "A3"), graph, new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0));
        edges.clear();
        vertex.forEachInstance(edges::add);
        Assert.assertEquals(Sets.newHashSet(edgeA1_A2, edgeA1_A3, edgeA2_A3), edges);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForeEachVertex() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);
        addEdge(0.798, "A1", "A3", builder);
        addEdge(0.968, "A2", "A3", builder);

        Cluster<String> vertex = new Cluster<>(Sets.newHashSet("A1", "A2", "A3"), builder.build(), new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0));

        Consumer<String> consumer = mock(Consumer.class);
        vertex.forEachVertex(consumer);
        verify(consumer).accept("A1");
        verify(consumer).accept("A2");
        verify(consumer).accept("A3");
    }

    @Test
    public void testVertexIterable() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        addEdge(0.840, "A1", "A2", builder);
        addEdge(0.798, "A1", "A3", builder);
        addEdge(0.968, "A2", "A3", builder);

        Cluster<String> cluster = new Cluster<>(Sets.newHashSet("A1", "A2", "A3"), builder.build(), new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0));

        Assert.assertEquals(Sets.newHashSet("A1", "A2", "A3"), Sets.newHashSet(cluster.vertexIterable()));
    }

    private SimEdge<String> addEdge(double score, String node1, String node2, DenseSimilarityGraph.Builder<String> builder) {

        return builder.add(node1, node2, score);
    }
}