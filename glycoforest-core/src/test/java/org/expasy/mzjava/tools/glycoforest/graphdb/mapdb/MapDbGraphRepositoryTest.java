package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.*;
import org.expasy.mzjava.tools.glycoforest.graphdb.BetweenRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.MsnNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.RunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.WithinRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.hstore.*;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mockito.Mockito;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MapDbGraphRepositoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private MsnSpectrum msn1_1;
    private MsnSpectrum msn1_2;
    private MsnSpectrum msn2_1;
    private MsnSpectrum msn2_2;
    private MsnSpectrum msn3_1;
    private MsnSpectrum msn3_2;
    private WithinRunConsensus wrc1_1;
    private WithinRunConsensus wrc2_2;
    private WithinRunConsensus wrc2_1;
    private BetweenRunConsensus brc1;
    private BetweenRunConsensus brc2;
    private MapDbGraphRepository repository;
    private RunNode runNode1;
    private RunNode runNode2;

    @Before
    public void setup() throws Exception {

        final File repositoryRoot = temporaryFolder.newFolder();
        System.setProperty("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root", repositoryRoot.getAbsolutePath());

        MapDbGraphRepository.Builder builder = new MapDbGraphRepository.Builder(repositoryRoot.getAbsolutePath());

        runNode1 = new HRunNode(UUID.randomUUID(), "run1");
        runNode2 = new HRunNode(UUID.randomUUID(), "run2");

        final Map<UUID, String> runMap = new HashMap<>();
        runMap.put(runNode1.getId(), runNode1.getName());
        runMap.put(runNode2.getId(), runNode2.getName());

        msn1_1 = newMsnSpectrum(485.32456835678, 1010, 1010.4572457);
        msn1_2 = newMsnSpectrum(485.34245873568, 1020, 1020.2458623);

        msn2_1 = newMsnSpectrum(733.24575463452, 2010, 2010.6813058);
        msn2_2 = newMsnSpectrum(733.35673816675, 2020, 2020.1323937);

        msn3_1 = newMsnSpectrum(733.24575463452, 3010, 3010.4645756);
        msn3_2 = newMsnSpectrum(733.35673816675, 3020, 3020.6568784);

        wrc1_1 = newWithinRunConsensus(runNode1, 0.9, msn1_1, msn1_2);
        wrc2_1 = newWithinRunConsensus(runNode1, 0.9, msn2_1, msn2_2);
        wrc2_2 = newWithinRunConsensus(runNode2, 0.9, msn3_1, msn3_2);

        brc1 = newBetweenRunConsensus(0.9, wrc1_1);
        brc2 = newBetweenRunConsensus(0.9, wrc2_1, wrc2_2);

        builder.addMsnSpectra(Stream.of(msn1_1, msn1_2, msn2_1, msn2_2, msn3_1, msn3_2));
        builder.addWithinRunConsensus(Stream.of(wrc1_1, wrc2_2, wrc2_1));
        builder.addBetweenRunConsensus(Stream.of(brc1, brc2));

        builder.addMsnMsnEdges(Stream.of(
                        newFEdge(EdgeType.MSN_MSN, Optional.of(runNode1.getId()), msn1_1, msn1_2, 0.91f),
                        newFEdge(EdgeType.MSN_MSN, Optional.of(runNode1.getId()), msn2_1, msn2_2, 0.92f),
                        newFEdge(EdgeType.MSN_MSN, Optional.of(runNode2.getId()), msn3_1, msn3_2, 0.93f)
                ).sorted((e1, e2) -> Float.compare(e2.getMz(), e1.getMz())).collect(Collectors.toList()).iterator()
        );
        builder.addMsnWrcEdge(Stream.of(
                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode1.getId()), msn1_1, wrc1_1, 0.81f),
                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode1.getId()), msn1_2, wrc1_1, 0.82f),

                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode1.getId()), msn2_1, wrc2_1, 0.83f),
                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode1.getId()), msn2_2, wrc2_1, 0.84f),

                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode2.getId()), msn3_1, wrc2_2, 0.85f),
                        newFEdge(EdgeType.MSN_WRC, Optional.of(runNode2.getId()), msn3_2, wrc2_2, 0.86f)
                ).sorted((e1, e2) -> Float.compare(e2.getMz(), e1.getMz())).collect(Collectors.toList()).iterator()
        );
        builder.addWrcWrcEdges(Stream.of(
                newFEdge(EdgeType.WRC_WRC, Optional.empty(), wrc2_1, wrc2_2, 0.7f)
        ));
        builder.addWrcBrcEdges(Stream.of(
                        newFEdge(EdgeType.WRC_BRC, Optional.empty(), wrc1_1, brc1, 0.61f),

                        newFEdge(EdgeType.WRC_BRC, Optional.empty(), wrc2_1, brc2, 0.62f),
                        newFEdge(EdgeType.WRC_BRC, Optional.empty(), wrc2_2, brc2, 0.63f)
                ).sorted((e1, e2) -> Float.compare(e2.getMz(), e1.getMz())).collect(Collectors.toList()).iterator()
        );
        builder.addBrcBrcEdges(Collections.singleton(
                        newFEdge(EdgeType.BRC_BRC, Optional.empty(), brc1, brc2, 0.9f)
                ).iterator()
        );

        builder.build(runMap);

        repository = new MapDbGraphRepository();
    }

    @After
    public void tearDown() {

        repository.shutdown();
    }

    private BetweenRunConsensus newBetweenRunConsensus(double simScoreMean, WithinRunConsensus... spectra) {

        final SummaryStatistics mzStats = new SummaryStatistics();
        Stream.of(spectra).forEach(spectrum -> mzStats.addValue(spectrum.getPrecursor().getMz()));

        int msMemberCount = Stream.of(spectra).mapToInt(WithinRunConsensus::getMemberCount).sum();
        Set<UUID> runIds = Stream.of(spectra).map(WithinRunConsensus::getRunId).collect(Collectors.toSet());

        final BetweenRunConsensus consensus = new BetweenRunConsensus(simScoreMean, 0, spectra.length, msMemberCount, mzStats.getMin(), mzStats.getMax(), runIds, PeakList.Precision.FLOAT);
        consensus.getPrecursor().setMzAndCharge(mzStats.getMean(), 1);
        return consensus;
    }

    private WithinRunConsensus newWithinRunConsensus(RunNode runNode, double simScoreMean, MsnSpectrum... spectra) {

        final SummaryStatistics mzStats = new SummaryStatistics();
        Stream.of(spectra).forEach(spectrum -> mzStats.addValue(spectrum.getPrecursor().getMz()));

        final ScanNumberInterval scanNumber = new ScanNumberInterval(
                Stream.of(spectra).mapToInt(spectrum -> spectrum.getScanNumbers().getFirst().getValue()).min().getAsInt(),
                Stream.of(spectra).mapToInt(spectrum -> spectrum.getScanNumbers().getFirst().getValue()).max().getAsInt()
        );

        final RetentionTimeInterval retentionTime = new RetentionTimeInterval(
                Stream.of(spectra).mapToDouble(spectrum -> spectrum.getRetentionTimes().getFirst().getTime()).min().getAsDouble(),
                Stream.of(spectra).mapToDouble(spectrum -> spectrum.getRetentionTimes().getFirst().getTime()).max().getAsDouble(),
                TimeUnit.SECOND
        );

        final WithinRunConsensus consensus = new WithinRunConsensus(PeakList.Precision.FLOAT);
        consensus.getPrecursor().setMzAndCharge(mzStats.getMean(), 1);
        consensus.setFields(runNode.getId(), 2, simScoreMean, 0, mzStats.getMin(), mzStats.getMax(), scanNumber, retentionTime);
        return consensus;
    }

    private MsnSpectrum newMsnSpectrum(double mz, int scanNumber, double retentionTime) {

        MsnSpectrum spectrum = new MsnSpectrum(PeakList.Precision.FLOAT);
        spectrum.getPrecursor().setMzAndCharge(mz, 1);
        spectrum.getScanNumbers().add(scanNumber);
        spectrum.getRetentionTimes().add(retentionTime, TimeUnit.SECOND);

        return spectrum;
    }

    private FEdge newFEdge(EdgeType edgeType, Optional<UUID> runIdOpt, Spectrum node1, Spectrum node2, float score) {

        float mz = (float) (node1.getPrecursor().getMz() + node2.getPrecursor().getMz()) / 2;
        return new FEdge(mz, edgeType, runIdOpt, node1.getId(), node2.getId(), score);
    }

    @Test
    public void testLoadMsnNeighboursOfWithRunNode() throws Exception {

        Assert.assertEquals(newMsnNodeSet(msn1_1, msn1_2), repository.loadChildren(new HWithinRunNode(wrc1_1), new HashSet<>()));
        Assert.assertEquals(newMsnNodeSet(msn2_1, msn2_2), repository.loadChildren(new HWithinRunNode(wrc2_1), new HashSet<>()));
        Assert.assertEquals(newMsnNodeSet(msn3_1, msn3_2), repository.loadChildren(new HWithinRunNode(wrc2_2), new HashSet<>()));
    }

    private Set<MsnNode> newMsnNodeSet(MsnSpectrum... spectra) {

        return Stream.of(spectra).map(HMsnNode::new).collect(Collectors.toSet());
    }

    @Test
    public void testLoadWithinRunNeighboursOfBetweenRunNode() throws Exception {

        Assert.assertEquals(newWithinRunNodeSet(wrc1_1), repository.loadChildren(new HBetweenRunNode(brc1), new HashSet<>()));
        Assert.assertEquals(newWithinRunNodeSet(wrc2_1, wrc2_2), repository.loadChildren(new HBetweenRunNode(brc2), new HashSet<>()));
    }

    private Set<WithinRunNode> newWithinRunNodeSet(WithinRunConsensus... spectra) {

        return Stream.of(spectra).map(HWithinRunNode::new).collect(Collectors.toSet());
    }

    @Test
    public void testLoadRunNeighboursOfBetweenRunNode() throws Exception {

        Assert.assertEquals(Sets.newHashSet(runNode1), repository.loadRunNodes(new HBetweenRunNode(brc1), new HashSet<>()));
        Assert.assertEquals(Sets.newHashSet(runNode1, runNode2), repository.loadRunNodes(new HBetweenRunNode(brc2), new HashSet<>()));
    }

    @Test
    public void testGetRunNode() throws Exception {

        Assert.assertEquals(runNode1, repository.loadRunNode(new HWithinRunNode(wrc1_1)));
        Assert.assertEquals(runNode1, repository.loadRunNode(new HWithinRunNode(wrc2_1)));
        Assert.assertEquals(runNode2, repository.loadRunNode(new HWithinRunNode(wrc2_2)));
    }

    @Test
    public void testGetBetweenRunNode() throws Exception {

        Assert.assertEquals(new HBetweenRunNode(brc1), repository.loadParent(new HWithinRunNode(wrc1_1)).get());
        Assert.assertEquals(new HBetweenRunNode(brc2), repository.loadParent(new HWithinRunNode(wrc2_1)).get());
        Assert.assertEquals(new HBetweenRunNode(brc2), repository.loadParent(new HWithinRunNode(wrc2_2)).get());
    }

    @Test
    public void testLoadAllRunNodes() throws Exception {

        final Set<RunNode> runNodes = repository.getRunNodeStream().collect(Collectors.toSet());
        Assert.assertEquals(Sets.newHashSet(runNode1, runNode2), runNodes);
    }

    @Test
    public void testLoadMsnGraph() throws Exception {

        DenseSimilarityGraph.Builder<MsnNode> graphBuilder = new DenseSimilarityGraph.Builder<>();
        final Set<MsnNode> expectedVertices = newMsnNodeSet(msn1_1, msn1_2, msn2_1, msn2_2, msn3_1, msn3_2);
        repository.loadGraph(expectedVertices, graphBuilder);

        final SimilarityGraph<MsnNode> graph = graphBuilder.build();
        Assert.assertEquals(6, graph.getVertexCount());
        Assert.assertEquals(3, graph.getEdgeCount());

        final Set<MsnNode> actualVertices = new HashSet<>();
        graph.forEachVertex(actualVertices::add);
        Assert.assertEquals(expectedVertices, actualVertices);

        final double delta = 0.0001;
        Assert.assertEquals(0.91, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn1_2)).get().getScore(), delta);
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn2_1)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn2_2)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn3_1)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn3_2)).isPresent());
        Assert.assertEquals(0.92, graph.findEdge(new HMsnNode(msn2_1), new HMsnNode(msn2_2)).get().getScore(), delta);
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn2_1), new HMsnNode(msn3_1)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn2_1), new HMsnNode(msn3_2)).isPresent());
        Assert.assertEquals(0.93, graph.findEdge(new HMsnNode(msn3_1), new HMsnNode(msn3_2)).get().getScore(), delta);
    }

    @Test
    public void testLoadMsnGraphWithUnconnectedNode() throws Exception {

        DenseSimilarityGraph.Builder<MsnNode> graphBuilder = new DenseSimilarityGraph.Builder<>();
        final Set<MsnNode> expectedVertices = newMsnNodeSet(msn1_1, msn2_1, msn2_2);
        repository.loadGraph(expectedVertices, graphBuilder);

        final SimilarityGraph<MsnNode> graph = graphBuilder.build();
        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());

        final Set<MsnNode> actualVertices = new HashSet<>();
        graph.forEachVertex(actualVertices::add);
        Assert.assertEquals(expectedVertices, actualVertices);

        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn2_1)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HMsnNode(msn1_1), new HMsnNode(msn2_2)).isPresent());
        Assert.assertEquals(0.92, graph.findEdge(new HMsnNode(msn2_1), new HMsnNode(msn2_2)).get().getScore(), 0.0001);
    }

    @Test
    public void testLoadWithinRunGraph() throws Exception {

        DenseSimilarityGraph.Builder<WithinRunNode> graphBuilder = new DenseSimilarityGraph.Builder<>();
        final Set<WithinRunNode> expectedVertices = newWithinRunNodeSet(wrc1_1, wrc2_1, wrc2_2);
        repository.loadGraph(expectedVertices, graphBuilder);

        final SimilarityGraph<WithinRunNode> graph = graphBuilder.build();

        Assert.assertEquals(3, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());

        final Set<WithinRunNode> actualVertices = new HashSet<>();
        graph.forEachVertex(actualVertices::add);
        Assert.assertEquals(expectedVertices, actualVertices);

        Assert.assertEquals(false, graph.findEdge(new HWithinRunNode(wrc1_1), new HWithinRunNode(wrc2_1)).isPresent());
        Assert.assertEquals(false, graph.findEdge(new HWithinRunNode(wrc1_1), new HWithinRunNode(wrc2_2)).isPresent());
        Assert.assertEquals(0.7, graph.findEdge(new HWithinRunNode(wrc2_1), new HWithinRunNode(wrc2_2)).get().getScore(), 0.0001);
    }

    @Test
    public void testLoadBetweenRunGraph() throws Exception {

        DenseSimilarityGraph.Builder<BetweenRunNode> graphBuilder = new DenseSimilarityGraph.Builder<>();
        final Set<BetweenRunNode> expectedVertices = Stream.of(brc1, brc2).map(HBetweenRunNode::new).collect(Collectors.toSet());
        repository.loadGraph(expectedVertices, graphBuilder);

        final SimilarityGraph<BetweenRunNode> graph = graphBuilder.build();

        Assert.assertEquals(2, graph.getVertexCount());
        Assert.assertEquals(1, graph.getEdgeCount());

        final Set<BetweenRunNode> actualVertices = new HashSet<>();
        graph.forEachVertex(actualVertices::add);
        Assert.assertEquals(expectedVertices, actualVertices);

        Assert.assertEquals(0.9, graph.findEdge(new HBetweenRunNode(brc1), new HBetweenRunNode(brc2)).get().getScore(), 0.0001);
    }

    @Test
    public void testGetSpectrum() throws Exception {

        for (MsnSpectrum expected : Lists.newArrayList(msn1_1, msn1_2, msn2_1, msn2_2, msn3_1, msn3_2)) {

            final MsnSpectrum actual = new HMsnNode(expected).loadSpectrum(repository);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSpectrum1() throws Exception {

        for (WithinRunConsensus expected : Lists.newArrayList(wrc1_1, wrc2_1, wrc2_2)) {

            final WithinRunConsensus actual = new HWithinRunNode(expected).loadSpectrum(repository);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetSpectrum2() throws Exception {

        for (BetweenRunConsensus expected : Lists.newArrayList(brc1, brc2)) {

            final BetweenRunConsensus actual = new HBetweenRunNode(expected).loadSpectrum(repository);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void testForEachBetweenRunNode() throws Exception {

        final Set<BetweenRunNode> set = repository.getBetweenRunNodeStream().collect(Collectors.toSet());
        Assert.assertEquals(Stream.of(brc1, brc2).map(HBetweenRunNode::new).collect(Collectors.toSet()), set);
    }

    @Test
    public void testFEdgeConsumer() throws Exception {

        //noinspection unchecked
        final BTreeMap<Object, Object> bTree = mock(BTreeMap.class);

        final DB.BTreeMapMaker treeMapMaker = mock(DB.BTreeMapMaker.class);
        when(treeMapMaker.comparator(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.keySerializerWrap(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.valueSerializer(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.make()).thenReturn(bTree);

        final DB mapDb = mock(DB.class);
        when(mapDb.createTreeMap(anyString())).thenReturn(treeMapMaker);

        MapDbGraphRepository.FEdgeConsumer edgeConsumer = new MapDbGraphRepository.FEdgeConsumer(EdgeType.MSN_WRC, mapDb);

        final FEdge edge1 = new FEdge(733.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.65f);
        edgeConsumer.accept(edge1);
        verify(bTree).put(new EdgeKey(edge1.getMz(), 0), new EdgeValue(edge1.getNode1Id(), edge1.getNode2Id(), edge1.getScore()));

        final FEdge edge2 = new FEdge(733.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.65f);
        edgeConsumer.accept(edge2);
        verify(bTree).put(new EdgeKey(edge2.getMz(), 1), new EdgeValue(edge2.getNode1Id(), edge2.getNode2Id(), edge2.getScore()));
    }

    @Test(expected = IllegalStateException.class)
    public void testFEdgeConsumerUnsorted() throws Exception {

        //noinspection unchecked
        final BTreeMap<Object, Object> bTree = mock(BTreeMap.class);

        final DB.BTreeMapMaker treeMapMaker = mock(DB.BTreeMapMaker.class);
        when(treeMapMaker.comparator(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.keySerializerWrap(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.valueSerializer(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.make()).thenReturn(bTree);

        final DB mapDb = mock(DB.class);
        when(mapDb.createTreeMap(anyString())).thenReturn(treeMapMaker);

        MapDbGraphRepository.FEdgeConsumer edgeConsumer = new MapDbGraphRepository.FEdgeConsumer(EdgeType.MSN_WRC, mapDb);

        edgeConsumer.accept(new FEdge(733.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.65f));
        edgeConsumer.accept(new FEdge(485.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.65f));
    }

    @Test(expected = IllegalStateException.class)
    public void testFEdgeConsumerWrongEdgeType() throws Exception {

        //noinspection unchecked
        final BTreeMap<Object, Object> bTree = mock(BTreeMap.class);

        final DB.BTreeMapMaker treeMapMaker = mock(DB.BTreeMapMaker.class);
        when(treeMapMaker.comparator(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.keySerializerWrap(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.valueSerializer(Mockito.any())).thenReturn(treeMapMaker);
        when(treeMapMaker.make()).thenReturn(bTree);

        final DB mapDb = mock(DB.class);
        when(mapDb.createTreeMap(anyString())).thenReturn(treeMapMaker);

        MapDbGraphRepository.FEdgeConsumer edgeConsumer = new MapDbGraphRepository.FEdgeConsumer(EdgeType.MSN_WRC, mapDb);

        edgeConsumer.accept(new FEdge(733.3f, EdgeType.WRC_BRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.65f));
    }

    @Test
    public void testTransformingIterator() throws Exception {

        final List<FEdge> edges = Lists.newArrayList(
                new FEdge(733.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.9f),
                new FEdge(733.3f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.8f),
                new FEdge(587.2f, EdgeType.MSN_WRC, Optional.empty(), UUID.randomUUID(), UUID.randomUUID(), 0.7f)
        );

        final MapDbGraphRepository.TransformingIterator iterator = new MapDbGraphRepository.TransformingIterator(edges.iterator());

        Assert.assertEquals(new EdgeKey(733.3f, 0), iterator.next().a);
        Assert.assertEquals(new EdgeKey(733.3f, -1), iterator.next().a);
        Assert.assertEquals(new EdgeKey(587.2f, 0), iterator.next().a);
    }

    @Test
    public void testLoadWrcChildren() throws Exception {

        Assert.assertEquals(Sets.newHashSet(new HMsnNode(msn1_1), new HMsnNode(msn1_2)), repository.loadChildren(new HWithinRunNode(wrc1_1), new HashSet<>()));
        Assert.assertEquals(Sets.newHashSet(new HMsnNode(msn2_1), new HMsnNode(msn2_2)), repository.loadChildren(new HWithinRunNode(wrc2_1), new HashSet<>()));
        Assert.assertEquals(Sets.newHashSet(new HMsnNode(msn3_1), new HMsnNode(msn3_2)), repository.loadChildren(new HWithinRunNode(wrc2_2), new HashSet<>()));
    }
}