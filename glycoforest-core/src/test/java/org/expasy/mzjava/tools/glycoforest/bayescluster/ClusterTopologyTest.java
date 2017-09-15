package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.collect.Lists;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.*;

@SuppressWarnings("unchecked")
public class ClusterTopologyTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("A1", "A2", 0.840);
        builder.add("A1", "A3", 0.798);
        builder.add("A2", "A3", 0.968);

        builder.add("B1", "B2", 0.559);
        builder.add("B1", "B3", 0.591);
        builder.add("B2", "B3", 0.498);

        builder.add("C1", "C2", 0.683);
        builder.add("C1", "C3", 0.725);
        builder.add("C2", "C3", 0.645);

        builder.add("A1", "B1", 0.430);
        builder.add("A1", "B2", 0.498);
        builder.add("A1", "B3", 0.405);
        builder.add("A2", "B1", 0.570);
        builder.add("A2", "B2", 0.537);
        builder.add("A2", "B3", 0.505);
        builder.add("A3", "B1", 0.495);
        builder.add("A3", "B2", 0.567);
        builder.add("A3", "B3", 0.467);

        builder.add("A1", "C1", 0.152);
        builder.add("A1", "C2", 0.184);
        builder.add("A1", "C3", 0.211);
        builder.add("A2", "C1", 0.271);
        builder.add("A2", "C2", 0.235);
        builder.add("A2", "C3", 0.189);
        builder.add("A3", "C1", 0.225);
        builder.add("A3", "C2", 0.148);
        builder.add("A3", "C3", 0.183);

        builder.add("B1", "C1", 0.398);
        builder.add("B1", "C2", 0.355);
        builder.add("B1", "C3", 0.394);
        builder.add("B2", "C1", 0.418);
        builder.add("B2", "C2", 0.377);
        builder.add("B2", "C3", 0.380);
        builder.add("B3", "C1", 0.372);
        builder.add("B3", "C2", 0.425);
        builder.add("B3", "C3", 0.373);

        final DenseSimilarityGraph<String> graph = builder.build();
        final double stdPriorMean = 0.05;
        final ArrayList<Set<String>> clusters = Lists.newArrayList(
                Sets.newSet("B1", "B2", "B3"),
                Sets.newSet("A1", "A2", "A3"),
                Sets.newSet("C1", "C2", "C3"));
        final ClusterTopology<String> clusterGraph = new ClusterTopology<>(clusters,
                graph,
                new UpdatableNormal(0.85, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0), new UpdatableNormal(0.2, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0)
        );

        Assert.assertEquals(3, clusterGraph.getClusterCount());
        Assert.assertEquals(54.917855152575484, clusterGraph.getLogLikelihood(), 0.0000001);
    }

    @Test
    public void testMerge() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("A1", "A2", 0.840);
        builder.add("A1", "A3", 0.798);
        builder.add("A2", "A3", 0.968);

        List<Set<String>> clusters = Arrays.asList(
                Sets.newSet("A1", "A2"),
                Sets.newSet("A3")
        );

        final double stdPriorMean = 0.05;
        final DenseSimilarityGraph<String> graph = builder.build();
        ClusterTopology<String> clusterGraph = new ClusterTopology<>(clusters, graph,
                new UpdatableNormal(0.85, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0), new UpdatableNormal(0.2, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0)
        );

        List<ClusterEdge<String, Cluster<String>>> edges = clusterGraph.getMergeableEdges(0.4);

        Assert.assertEquals(1, edges.size());

        ClusterEdge<String, Cluster<String>> edge = edges.get(0);
        ClusterTopology<String> combined = clusterGraph.combine(edge);

        final Collection<Set<String>> vertexSets = combined.createVertexSets();
        Assert.assertEquals(1, vertexSets.size());
    }

    @Test
    public void testCopyConstructor() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("A1", "A2", 0.840);
        builder.add("A1", "A3", 0.798);
        builder.add("A2", "A3", 0.968);

        List<Set<String>> clusters = Arrays.asList(
                Sets.newSet("A1", "A2", "A3")
        );

        final double stdPriorMean = 0.05;
        final DenseSimilarityGraph<String> graph = builder.build();
        ClusterTopology<String> clusterGraph = new ClusterTopology<>(clusters, graph,
                new UpdatableNormal(0.85, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0), new UpdatableNormal(0.2, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0)
        );

        final Collection<Set<String>> vertexSets = clusterGraph.createVertexSets();
        Assert.assertEquals(1, vertexSets.size());
        Assert.assertEquals(Arrays.asList(Sets.newSet("A1", "A2", "A3")), vertexSets);
    }

    @Test
    public void testSingletonGraph() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("A");

        final double stdPriorMean = 0.05;
        ClusterTopology<String> clusterGraph = new ClusterTopology<>(builder.build(),
                new UpdatableNormal(0.85, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0), new UpdatableNormal(0.2, stdPriorMean * stdPriorMean, 0.05 * 0.05, 0.0, 1.0)
        );

        Assert.assertEquals(1, clusterGraph.getClusterCount());
    }
}