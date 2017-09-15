package org.expasy.mzjava.tools.glycoforest.wrcluster;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.tools.glycoforest.bayescluster.Cluster;
import org.expasy.mzjava.tools.glycoforest.bayescluster.ClusterEdge;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableNormal;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class WRClusterTopologyTest {

    @Test
    public void testGetMergeableEdges() throws Exception {

        WithinRunConsensus cons1 = mockConsensus("a1000000-0000-0000-0000-000000000000", 60, 70, "c1000000-0000-0000-0000-000000000000");
        WithinRunConsensus cons2 = mockConsensus("a2000000-0000-0000-0000-000000000000", 70 + 60, 80 + 60, "c1000000-0000-0000-0000-000000000000");
        WithinRunConsensus cons3 = mockConsensus("b1000000-0000-0000-0000-000000000000", 250, 260, "c1000000-0000-0000-0000-000000000000");

        DenseSimilarityGraph.Builder<WithinRunConsensus> builder = new DenseSimilarityGraph.Builder<>();
        builder.add(cons1, cons2, 1);
        builder.add(cons1, cons3, 1);
        builder.add(cons2, cons3, 1);

        SimilarityGraph<WithinRunConsensus> graph = builder.build();

        WRClusterTopology topology = new WRClusterTopology(graph, new UpdatableNormal(0.85, 0.0025, 0.0025, 0.0, 1.0), new UpdatableNormal(0.2, 0.0025, 0.0025, 0.0, 1.0)
        );

        List<ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>>> edges = topology.getMergeableEdges(0);
        Assert.assertEquals(0, edges.size());
    }

    @Test
    public void testGetMergeableEdges2() throws Exception {

        WithinRunConsensus cons1 = mockConsensus("a1000000-0000-0000-0000-000000000000", 60, 70, "c1000000-0000-0000-0000-000000000000");
        WithinRunConsensus cons2 = mockConsensus("a2000000-0000-0000-0000-000000000000", 130, 140, "c1000000-0000-0000-0000-000000000000");
        WithinRunConsensus cons3 = mockConsensus("b1000000-0000-0000-0000-000000000000", 250, 260, "c2000000-0000-0000-0000-000000000000");

        DenseSimilarityGraph.Builder<WithinRunConsensus> builder = new DenseSimilarityGraph.Builder<>();
        builder.add(cons2, cons1, 1);
        builder.add(cons3, cons2, 1);
        builder.add(cons3, cons1, 1);

        SimilarityGraph<WithinRunConsensus> graph = builder.build();

        WRClusterTopology topology = new WRClusterTopology(graph, new UpdatableNormal(0.85, 0.0025, 0.0025, 0.0, 1.0), new UpdatableNormal(0.2, 0.0025, 0.0025, 0.0, 1.0)
        );

        List<ClusterEdge<WithinRunConsensus, Cluster<WithinRunConsensus>>> edges = topology.getMergeableEdges(0);
        Assert.assertEquals(2, edges.size());
    }

    @Test
    public void testMergeCheckPredicate() throws Exception {

        final EdgeMergePredicate predicate = new EdgeMergePredicate(60);

        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String source1 = UUID.randomUUID().toString();
        final String source2 = UUID.randomUUID().toString();

        //Test different source
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(id1, 60, 70, source1), mockConsensus(id2, 600, 700, source2), 0.8)));

        //Test vertex1 < vertex2
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(id1, 60, 70, source1), mockConsensus(id2, 70, 80, source1), 0.8)));
        Assert.assertEquals(false, predicate.test(new SimEdge<>(mockConsensus(id1, 60, 70, source1), mockConsensus(id2, 131, 140, source1), 0.8)));


        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(id1, 70, 80, source1), mockConsensus(id2, 60, 70, source1), 0.8)));
        Assert.assertEquals(false, predicate.test(new SimEdge<>(mockConsensus(id1, 131, 140, source1), mockConsensus(id2, 60, 70, source1), 0.8)));
    }

    private WithinRunConsensus mockConsensus(String id, int minRt, int maxRt, String source) {

        final SummaryStatistics scoreStats = new SummaryStatistics();
        scoreStats.addValue(0.9);
        scoreStats.addValue(0.88);

        final SummaryStatistics mzStats = new SummaryStatistics();
        mzStats.addValue(733.32);
        mzStats.addValue(733.29);

        double totalIonCurrent = 3418.98;

        final ScanNumberInterval scanNumberInterval = new ScanNumberInterval(463, 521);
        final RetentionTimeInterval retentionTimeInterval = new RetentionTimeInterval(minRt, maxRt, TimeUnit.SECOND);

        return new WithinRunConsensus(UUID.fromString(id), UUID.fromString(source), scoreStats, totalIonCurrent, mzStats, new int[]{1}, scanNumberInterval, retentionTimeInterval, PeakList.Precision.FLOAT);
    }
}