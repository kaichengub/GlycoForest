package org.expasy.mzjava.tools.glycoforest.bayescluster;

import com.google.common.collect.Sets;
import org.expasy.mzjava.core.ms.cluster.ClusterBuilder;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class BaseClusterBuilderTest {

    @Test
    public abstract void testTwoClusters();

    public void doTestTwoClusters(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("A1", "A2", 0.9);
        builder.add("A1", "B1", 0.2);
        builder.add("A2", "B1", 0.3);


        List<Set<String>> clusters = new ArrayList<>(clusterBuilder.cluster(builder.build()));
        Collections.sort(clusters, new Comparator<Set<String>>() {
            @Override
            public int compare(Set<String> c1, Set<String> c2) {

                return Double.compare(c1.size(), c2.size());
            }
        });

        Assert.assertEquals(2, clusters.size());

        Assert.assertEquals(Sets.newHashSet("B1"), clusters.get(0));
        Assert.assertEquals(Sets.newHashSet("A1", "A2"), clusters.get(1));
    }

    @Test
    public abstract void testThreeClusters();

    /**
     * mean     stdev
     * ---------------------
     * A-A    0.869    0.089
     * A-B    0.497    0.057
     * A-C    0.200    0.040
     * <p>
     * B-B    0.649    0.047
     * B-C    0.388    0.023
     * <p>
     * C-C    0.684    0.040
     */
    public void doTestThreeClusters(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("A1", "A2", 0.840);
        builder.add("A1", "A3", 0.798);
        builder.add("A2", "A3", 0.968);

        builder.add("B1", "B2", 0.659);
        builder.add("B1", "B3", 0.691);
        builder.add("B2", "B3", 0.598);

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

        List<Set<String>> clusters = new ArrayList<>(clusterBuilder.cluster(builder.build()));

        Assert.assertEquals(clusters.toString(), 3, clusters.size());
        Collections.sort(clusters, new Comparator<Set<String>>() {
            @Override
            public int compare(Set<String> c1, Set<String> c2) {

                return c1.iterator().next().compareTo(c2.iterator().next());
            }
        });
        Assert.assertEquals(Sets.newHashSet("A1", "A2", "A3"), clusters.get(0));
        Assert.assertEquals(Sets.newHashSet("B1", "B2", "B3"), clusters.get(1));
        Assert.assertEquals(Sets.newHashSet("C1", "C2", "C3"), clusters.get(2));
    }

    @Test
    public abstract void testThreeClustersWithLink();

    /**
     * mean     stdev
     * ---------------------
     * A-A    0.869    0.089
     * A-B    0.497    0.057
     * A-C    0.200    0.040
     * <p>
     * B-B    0.549    0.047
     * B-C    0.388    0.023
     * <p>
     * C-C    0.684    0.040
     *
     * Same as three clusters except that edge from A1 to C1 is 0.999
     */
    public void doTestThreeClustersWithLink(ClusterBuilder<String> clusterBuilder) {

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

        builder.add("A1", "C1", 0.999);
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

        List<Set<String>> clusters = new ArrayList<>(clusterBuilder.cluster(builder.build()));
        Assert.assertEquals(clusters.toString(), 3, clusters.size());
        Collections.sort(clusters, new Comparator<Set<String>>() {
            @Override
            public int compare(Set<String> c1, Set<String> c2) {

                return c1.iterator().next().compareTo(c2.iterator().next());
            }
        });
        Assert.assertEquals(Sets.newHashSet("A1", "A2", "A3"), clusters.get(0));
        Assert.assertEquals(Sets.newHashSet("B1", "B2", "B3"), clusters.get(1));
        Assert.assertEquals(Sets.newHashSet("C1", "C2", "C3"), clusters.get(2));
    }

    @Test
    public abstract void testTwoSmallClusters();

    public void doTestTwoSmallClusters(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        List<String> cluster1 = EdgeListGenerator.makeNodes(4, EdgeListGenerator.newStringVertexFunction("A"));
        List<String> cluster2 = EdgeListGenerator.makeNodes(3, EdgeListGenerator.newStringVertexFunction("B"));

        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9);
        EdgeListGenerator.addWithinEdges(cluster2, builder, 0.9);
        EdgeListGenerator.addBetweenEdges(cluster1, cluster2, builder, 0.6);


        Collection<Set<String>> results = clusterBuilder.cluster(builder.build());

        Assert.assertEquals(2, results.size());

        for (Set<String> cluster : results) {

            if (cluster.size() == 4) {

                Assert.assertEquals(Sets.newHashSet(cluster1), cluster);
            } else {

                Assert.assertEquals(Sets.newHashSet(cluster2), cluster);
            }
        }
    }

    @Test
    public abstract void testSmallLinked();

    /**
     * <pre>
     *   A1--0.9-B1-----+
     *   |  \     |     |
     *  0.9 0.9  0.1    |
     *   |      \ |     |
     *   A2--0.9-A3     |
     *    \             |
     *     \-----------0.1
     * </pre>
     */
    public void doTestSmallLinked(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("A1", "B1", 0.9);
        builder.add("A2", "B1", 0.1);
        builder.add("A3", "B1", 0.1);

        builder.add("A1", "A2", 0.93);
        builder.add("A1", "A3", 0.92);
        builder.add("A2", "A3", 0.91);


        List<Set<String>> results = new ArrayList<>(clusterBuilder.cluster(builder.build()));
        Assert.assertEquals(2, results.size());

        Collections.sort(results, new Comparator<Set<String>>() {
            @Override
            public int compare(Set<String> c1, Set<String> c2) {

                return Double.compare(c1.size(), c2.size());
            }
        });

        Assert.assertEquals(Sets.newHashSet("B1"), results.get(0));
        Assert.assertEquals(Sets.newHashSet("A1", "A2", "A3"), results.get(1));
    }

    @Test
    public abstract void testOneLargeCluster();

    public void doTestOneLargeCluster(ClusterBuilder<String> clusterBuilder) {

        List<String> cluster1 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("A"));
        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9, 0.05);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(1, clusters.size());
    }

    @Test
    public abstract void testOneSingletonCluster();

    public void doTestOneSingletonCluster(ClusterBuilder<String> clusterBuilder) {

        String node = "A";
        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add(node);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(1, clusters.size());
        Assert.assertEquals(Sets.newHashSet(node), clusters.iterator().next());
    }

    @Test
    public abstract void testTwoEqualClusters();

    public void doTestTwoEqualClusters(ClusterBuilder<String> clusterBuilder) {

        List<String> cluster1 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("A"));
        List<String> cluster2 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("B"));

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9, 0.05);
        EdgeListGenerator.addWithinEdges(cluster2, builder, 0.9, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster1, cluster2, builder, 0.5, 0.05);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(2, clusters.size());

        int contaminants = 0;
        for (Set<String> currCluster : clusters) {

            int matchTo1 = 0;
            int matchTo2 = 0;
            for (String node : currCluster) {

                if (cluster1.contains(node)) {
                    matchTo1 += 1;
                } else if (cluster2.contains(node)) {
                    matchTo2 += 1;
                }
            }

            contaminants += Math.min(matchTo1, matchTo2);
        }

        Assert.assertEquals(true, contaminants <= 1);
    }

    @Test
    public abstract void testThreeEqualClusters();

    public void doTestThreeEqualClusters(ClusterBuilder<String> clusterBuilder) {

        List<String> cluster1 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("A"));
        List<String> cluster2 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("B"));
        List<String> cluster3 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("C"));

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9, 0.05);
        EdgeListGenerator.addWithinEdges(cluster2, builder, 0.85, 0.05);
        EdgeListGenerator.addWithinEdges(cluster3, builder, 0.8, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster1, cluster2, builder, 0.5, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster2, cluster3, builder, 0.4, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster3, cluster1, builder, 0.2, 0.05);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(clusters.toString(), 3, clusters.size());

        int contaminants = 0;
        for (Set<String> currCluster : clusters) {

            int matchTo1 = 0;
            int matchTo2 = 0;
            for (String node : currCluster) {

                if (cluster1.contains(node)) {
                    matchTo1 += 1;
                } else if (cluster2.contains(node)) {
                    matchTo2 += 1;
                }
            }

            contaminants += Math.min(matchTo1, matchTo2);
        }

        Assert.assertEquals(true, contaminants < 1);
    }

    @Test
    public abstract void testThreeEqualClusters2();

    public void doTestThreeEqualClusters2(ClusterBuilder<String> clusterBuilder) {

        List<String> cluster1 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("A"));
        List<String> cluster2 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("B"));
        List<String> cluster3 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("C"));

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9, 0.05);
        EdgeListGenerator.addWithinEdges(cluster2, builder, 0.7, 0.05);
        EdgeListGenerator.addWithinEdges(cluster3, builder, 0.6, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster1, cluster2, builder, 0.5, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster2, cluster3, builder, 0.4, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster3, cluster1, builder, 0.2, 0.05);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(clusters.toString(), 3, clusters.size());

        int contaminants = 0;
        for (Set<String> currCluster : clusters) {

            int matchTo1 = 0;
            int matchTo2 = 0;
            for (String node : currCluster) {

                if (cluster1.contains(node)) {
                    matchTo1 += 1;
                } else if (cluster2.contains(node)) {
                    matchTo2 += 1;
                }
            }

            contaminants += Math.min(matchTo1, matchTo2);
        }

        Assert.assertEquals("Have " + contaminants + " contaminants", true, contaminants < 1);
    }

    @Test
    public abstract void testTwoClose();

    /**
     * Graph:
     * <pre>
     *     A1-A2--B1--B2
     *     |\ /|\ | /
     *     |/ \| \|/
     *     A3-A4  B3
     * </pre>
     */
    public void doTestTwoClose(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("a1", "a2", 0.9);
        builder.add("a1", "a3", 0.9);
        builder.add("a1", "a4", 0.9);
        builder.add("a2", "a3", 0.9);
        builder.add("a2", "a4", 0.9);
        builder.add("a3", "a4", 0.9);

        builder.add("b1", "b2", 0.9);
        builder.add("b1", "b3", 0.9);
        builder.add("b2", "b3", 0.9);

        builder.add("a1", "b1", 0.0);
        builder.add("a1", "b2", 0.0);
        builder.add("a1", "b3", 0.0);

        builder.add("a2", "b1", 0.8);
        builder.add("a2", "b2", 0.0);
        builder.add("a2", "b3", 0.8);

        builder.add("a3", "b1", 0.0);
        builder.add("a3", "b2", 0.0);
        builder.add("a3", "b3", 0.0);

        builder.add("a4", "b1", 0.0);
        builder.add("a4", "b2", 0.0);
        builder.add("a4", "b3", 0.0);

        Collection<Set<String>> results = clusterBuilder.cluster(builder.build());

        Assert.assertEquals(results.toString(), 2, results.size());

        for (Set<String> cluster : results) {

            if (cluster.size() == 4) {

                Assert.assertEquals(Sets.newHashSet("a1", "a2", "a3", "a4"), cluster);
            } else {

                Assert.assertEquals(Sets.newHashSet("b1", "b2", "b3"), cluster);
            }
        }
    }

    /**
     * <pre>
     *           B1 _
     *         /  |  \
     *        /  0.1  \
     *       /    |    \
     *     0.9   A1    0.9
     *     /    /   \    \
     *    /  0.1    0.1   \
     *   /   /        \   |
     *   | /           \  |
     *  B2-----0.9-------B3
     *
     * </pre>
     */
    @Test
    public abstract void testSplitSmall();

    /**
     * <pre>
     *           B1 _
     *         /  |  \
     *        /  0.1  \
     *       /    |    \
     *     0.9   A1    0.9
     *     /    /   \    \
     *    /  0.1    0.1   \
     *   /   /        \   |
     *   | /           \  |
     *  B2-----0.9-------B3
     *
     * </pre>
     */
    public void doTestSplitSmall(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("a1", "b1", 0.1);
        builder.add("a1", "b2", 0.0);
        builder.add("a1", "b3", 0.0);

        builder.add("b1", "b2", 0.9);
        builder.add("b1", "b3", 0.9);
        builder.add("b2", "b3", 0.9);


        Collection<Set<String>> results = clusterBuilder.cluster(builder.build());

        Assert.assertEquals(2, results.size());
    }

    /**
     *  <pre>
     *          A1-\
     *       /  |   \
     *    0.8  0.8  |
     *    /     |   |
     *  B1-0.90-B2  |
     *    \     /   |
     *   0.9  0.9   |
     *     \  /     0.8
     *      B3-----/
     * </pre>
     */
    @Test
    public abstract void testKeepSmall();

    /**
     *  <pre>
     *          A1-\
     *       /  |   \
     *    0.8  0.8  |
     *    /     |   |
     *  B1-0.90-B2  |
     *    \     /   |
     *   0.9  0.9   |
     *     \  /     0.8
     *      B3-----/
     * </pre>
     */
    public void doTestKeepSmall(ClusterBuilder<String> clusterBuilder) {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        builder.add("a1", "b1", 0.8);
        builder.add("a1", "b2", 0.8);
        builder.add("a1", "b3", 0.8);

        builder.add("b1", "b2", 0.9);
        builder.add("b1", "b3", 0.9);
        builder.add("b2", "b3", 0.9);

        Collection<Set<String>> results = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(results.toString(), 1, results.size());
    }

    @Test
    public abstract void testTwoUnequalClusters();

    public void doTestTwoUnequalClusters(ClusterBuilder<String> clusterBuilder) {
        List<String> cluster1 = EdgeListGenerator.makeNodes(10, EdgeListGenerator.newStringVertexFunction("A"));
        List<String> cluster2 = EdgeListGenerator.makeNodes(1, EdgeListGenerator.newStringVertexFunction("B"));

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        EdgeListGenerator.addWithinEdges(cluster1, builder, 0.9, 0.05);
        EdgeListGenerator.addWithinEdges(cluster2, builder, 0.9, 0.05);
        EdgeListGenerator.addBetweenEdges(cluster1, cluster2, builder, 0.5, 0.05);

        Collection<Set<String>> clusters = clusterBuilder.cluster(builder.build());
        Assert.assertEquals(2, clusters.size());

        int contaminants = 0;
        for (Set<String> currCluster : clusters) {

            int matchTo1 = 0;
            int matchTo2 = 0;
            for (String node : currCluster) {

                if (cluster1.contains(node)) {
                    matchTo1 += 1;
                } else if (cluster2.contains(node)) {
                    matchTo2 += 1;
                }
            }

            contaminants += Math.min(matchTo1, matchTo2);
        }

        Assert.assertEquals(true, contaminants <= 1);
    }
}
