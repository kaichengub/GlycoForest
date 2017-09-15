package org.expasy.mzjava.tools.glycoforest.rtcluster;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.tools.glycoforest.bayescluster.ClusterEdge;
import org.expasy.mzjava.tools.glycoforest.bayescluster.UpdatableNormal;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class RTClusterTopologyTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMergeableEdges() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();
        builder.add("A1", "A2", 0.840);

        builder.add("B1", "B2", 0.559);

        builder.add("C1", "C2", 0.683);

        SimEdge<String> a1b1 = builder.add("A1", "B1", 0.430);
        SimEdge<String> a1b2 = builder.add("A1", "B2", 0.498);
        SimEdge<String> a2b1 = builder.add("A2", "B1", 0.570);
        SimEdge<String> a2b2 = builder.add("A2", "B2", 0.537);

        builder.add("A1", "C1", 0.152);
        builder.add("A1", "C2", 0.184);
        builder.add("A2", "C1", 0.271);
        builder.add("A2", "C2", 0.235);

        SimEdge<String> b1c1 = builder.add("B1", "C1", 0.398);
        SimEdge<String> b1c2 = builder.add("B1", "C2", 0.355);
        SimEdge<String> b2c1 = builder.add("B2", "C1", 0.418);
        SimEdge<String> b2c2 = builder.add("B2", "C2", 0.377);
        SimilarityGraph<String> graph = builder.build();

        final TObjectDoubleMap<String> rtMap = new TObjectDoubleHashMap<>();
        rtMap.put("A1", 1.1);
        rtMap.put("A2", 1.2);
        rtMap.put("B1", 2.1);
        rtMap.put("B2", 2.2);
        rtMap.put("C1", 3.1);
        rtMap.put("C2", 3.2);

        final TObjectDoubleMap<String> ticMap = new TObjectDoubleHashMap<>();
        ticMap.put("A1", 11);
        ticMap.put("A2", 43);
        ticMap.put("B1", 60);
        ticMap.put("B2", 54);
        ticMap.put("C1", 21);
        ticMap.put("C2", 3);

        final TObjectIntMap<String> scanNumberMap = new TObjectIntHashMap<>();
        scanNumberMap.put("A1", 1);
        scanNumberMap.put("A2", 2);
        scanNumberMap.put("B1", 3);
        scanNumberMap.put("B2", 4);
        scanNumberMap.put("C1", 5);
        scanNumberMap.put("C2", 6);

        final ArrayList<Set<String>> clusters = Lists.newArrayList(
                (Set<String>)Sets.newHashSet("A1", "A2"),
                Sets.newHashSet("B1", "B2"),
                Sets.newHashSet("C1", "C2"));

        RTClusterTopology<String> clusterGraph = new RTClusterTopology<>(clusters, graph,
                new UpdatableNormal(0.85, 0.0025, 0.0025, 0.0, 1.0), new UpdatableNormal(0.2, 0.0025, 0.0025, 0.0, 1.0),
                rtMap::get, ticMap::get, scanNumberMap::get);

        List<ClusterEdge<String, RTCluster<String>>> mergeable = clusterGraph.getMergeableEdges(0.0);

        Assert.assertEquals(2, mergeable.size());

        final Set<SimEdge<String>> simEdges1 = new HashSet<>(4);
        mergeable.get(0).forEachInstance(simEdges1::add);
        Assert.assertEquals(Sets.newHashSet(a1b1, a1b2, a2b1, a2b2), simEdges1);

        final Set<SimEdge<String>> simEdges2 = new HashSet<>(4);
        mergeable.get(1).forEachInstance(simEdges2::add);
        Assert.assertEquals(Sets.newHashSet(b1c1, b1c2, b2c1, b2c2), simEdges2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMergeableEdges2() throws Exception {

        DenseSimilarityGraph.Builder<String> builder = new DenseSimilarityGraph.Builder<>();

        SimEdge<String> a1b1 = builder.add("A1", "B1", 0.430);

        builder.add("A1", "C1", 0.8);

        SimEdge<String> b1c1 = builder.add("B1", "C1", 0.398);
        SimilarityGraph<String> graph = builder.build();

        final TObjectDoubleMap<String> rtMap = new TObjectDoubleHashMap<>();
        rtMap.put("A1", 1.1);
        rtMap.put("B1", 2.1);
        rtMap.put("C1", 3.1);

        final TObjectDoubleMap<String> ticMap = new TObjectDoubleHashMap<>();
        ticMap.put("A1", 10);
        ticMap.put("B1", 200);
        ticMap.put("C1", 30);

        final TObjectIntMap<String> scanNumberMap = new TObjectIntHashMap<>();
        scanNumberMap.put("A1", 1);
        scanNumberMap.put("B1", 2);
        scanNumberMap.put("C1", 3);

        RTClusterTopology<String> clusterGraph = new RTClusterTopology<>(graph,
                new UpdatableNormal(0.85, 0.0025, 0.0025, 0.0, 1.0), new UpdatableNormal(0.2, 0.0025, 0.0025, 0.0, 1.0),
                rtMap::get,
                ticMap::get,
                scanNumberMap::get);

        List<ClusterEdge<String, RTCluster<String>>> mergeable = clusterGraph.getMergeableEdges(0.1);

        Assert.assertEquals(2, mergeable.size());

        final Set<SimEdge<String>> simEdges1 = new HashSet<>(1);
        mergeable.get(0).forEachInstance(simEdges1::add);
        Assert.assertEquals(Sets.newHashSet(a1b1), simEdges1);

        final Set<SimEdge<String>> simEdges2 = new HashSet<>(1);
        mergeable.get(1).forEachInstance(simEdges2::add);
        Assert.assertEquals(Sets.newHashSet(b1c1), simEdges2);
    }
}