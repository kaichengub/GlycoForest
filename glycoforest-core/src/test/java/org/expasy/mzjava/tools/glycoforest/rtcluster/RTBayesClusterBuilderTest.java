package org.expasy.mzjava.tools.glycoforest.rtcluster;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableNormal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

public class RTBayesClusterBuilderTest {

    @Test
    public void testCluster() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("A1", "A2", 0.6);
        builder.add("A1", "A3", 0.6);
        builder.add("A1", "A4", 0.9);
        builder.add("A2", "A3", 0.9);
        builder.add("A2", "A4", 0.6);
        builder.add("A3", "A4", 0.6);
        SimilarityGraph<String> simGraph = builder.build();

        final TObjectDoubleMap<String> rtMap = new TObjectDoubleHashMap<>();
        rtMap.put("A1", 1);
        rtMap.put("A2", 2);
        rtMap.put("A3", 3);
        rtMap.put("A4", 4);

        final TObjectDoubleMap<String> ticMap = new TObjectDoubleHashMap<>();
        ticMap.put("A1", 5);
        ticMap.put("A2", 40);
        ticMap.put("A3", 35);
        ticMap.put("A4", 10);

        final TObjectIntMap<String> scanNumberMap = new TObjectIntHashMap<>();
        scanNumberMap.put("A1", 1);
        scanNumberMap.put("A2", 2);
        scanNumberMap.put("A3", 3);
        scanNumberMap.put("A4", 4);

        RTBayesClusterBuilder<String> clusterBuilder = new RTBayesClusterBuilder<>(
                new UpdatableNormal(0.8, 0.0003, 0.01, 0.0, 1.0), new UpdatableNormal(0.4, 0.0005, 0.01, 0.0, 1.0), 0.6,
                rtMap::get,
                ticMap::get,
                scanNumberMap::get
        );

        Collection<Set<String>> clusters = clusterBuilder.cluster(simGraph);
        Assert.assertEquals(1, clusters.size());
        Assert.assertEquals(4, clusters.iterator().next().size());
    }
}