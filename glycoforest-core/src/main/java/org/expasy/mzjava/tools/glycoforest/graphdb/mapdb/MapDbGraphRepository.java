package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import com.google.common.base.Preconditions;
import org.expasy.glycoforest.util.DoubleMinMax;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.stats.FrequencyTable;
import org.expasy.mzjava.tools.glycoforest.graphdb.*;
import org.expasy.mzjava.tools.glycoforest.graphdb.hstore.*;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.mapdb.*;
import org.openide.util.lookup.ServiceProvider;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service = GraphRepository.class)
public class MapDbGraphRepository implements GraphRepository {

    private static final Logger LOGGER = Logger.getLogger(MapDbGraphRepository.class.getName());

    private static final String MAP_DB_REPOSITORY = "mapDbRepository";
    private static final String MSN_SPECTRA_MAP = "msn_spectra_map";
    private static final String WITHIN_RUN_CONSENSUS_MAP = "within_run_consensus_map";
    private static final String BETWEEN_RUN_CONSENSUS_MAP = "between_run_consensus_map";
    private static final String RUN_MAP = "run_map";
    private static final String WRC_BRC_BACK_EDGE_MAP = "wrc_brc_back_edge_map";

    private final Map<UUID, MsnSpectrum> msnSpectrumMap;
    private final Map<UUID, WithinRunConsensus> withinRunConsensusMap;
    private final Map<UUID, BetweenRunConsensus> betweenRunConsensusMap;
    private final Map<UUID, String> runMap;
    private final Map<UUID, UUID> wrcToBrcBackEdgeMap;

    private final BTreeMap<EdgeKey, EdgeValue> msnMsnEdgeMap;
    private final BTreeMap<EdgeKey, EdgeValue> msnWrcEdgeMap;
    private final BTreeMap<EdgeKey, EdgeValue> wrcWrcEdgeMap;
    private final BTreeMap<EdgeKey, EdgeValue> wrcBrcEdgeMap;
    private final BTreeMap<EdgeKey, EdgeValue> brcBrcEdgeMap;

    private final EdgeKey fromKey = new EdgeKey(0.0f, 0);
    private final EdgeKey toKey = new EdgeKey(0.0f, 0);

    private final DB mapDb;

    @SuppressWarnings("UnusedDeclaration") //Is used with the @ServiceProvider annotation
    public MapDbGraphRepository() {

        final String repositoryRoot = System.getProperty("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root");
        if (repositoryRoot == null) {
            throw new IllegalStateException("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root property is not set");
        }
        mapDb = DBMaker.newFileDB(new File(repositoryRoot, MAP_DB_REPOSITORY))
                .closeOnJvmShutdown()
                .readOnly()
                .make();

        msnSpectrumMap = mapDb.getHashMap(MSN_SPECTRA_MAP);
        withinRunConsensusMap = mapDb.getHashMap(WITHIN_RUN_CONSENSUS_MAP);
        betweenRunConsensusMap = mapDb.getHashMap(BETWEEN_RUN_CONSENSUS_MAP);
        runMap = mapDb.getHashMap(RUN_MAP);
        wrcToBrcBackEdgeMap = mapDb.getHashMap(WRC_BRC_BACK_EDGE_MAP);

        msnMsnEdgeMap = mapDb.getTreeMap(getEdgeMapName(EdgeType.MSN_MSN));
        msnWrcEdgeMap = mapDb.getTreeMap(getEdgeMapName(EdgeType.MSN_WRC));
        wrcWrcEdgeMap = mapDb.getTreeMap(getEdgeMapName(EdgeType.WRC_WRC));
        wrcBrcEdgeMap = mapDb.getTreeMap(getEdgeMapName(EdgeType.WRC_BRC));
        brcBrcEdgeMap = mapDb.getTreeMap(getEdgeMapName(EdgeType.BRC_BRC));
    }

    private static String getEdgeMapName(EdgeType edgeType) {

        return edgeType.name().toLowerCase() + "_edges";
    }

    @Override
    public void shutdown() {

        if (!mapDb.isClosed()) {

            mapDb.close();
        }
    }

    @Override
    public <C extends Collection<MsnNode>> C loadChildren(WithinRunNode withinRunNode, C nodes) {

        final UUID wrcId = withinRunNode.getSpectrumId();
        load(EdgeType.MSN_WRC, (float)withinRunNode.getMinMemberMz(), (float)withinRunNode.getMaxMemberMz(), (key, value) -> {

            if (value.getNode2Id().equals(wrcId)) {

                nodes.add(new HMsnNode(msnSpectrumMap.get(value.getNode1Id())));
            }
        });
        return nodes;
    }

    @Override
    public <C extends Collection<WithinRunNode>> C loadChildren(BetweenRunNode betweenRunNode, C nodes) {

        final UUID brcId = betweenRunNode.getSpectrumId();
        load(EdgeType.WRC_BRC, (float)betweenRunNode.getMinWithinRunMz(), (float)betweenRunNode.getMaxWithinRunMz(), (key, value) -> {

            if (value.getNode2Id().equals(brcId)) {

                nodes.add(new HWithinRunNode(withinRunConsensusMap.get(value.getNode1Id())));
            }
        });

        return nodes;
    }

    @Override
    public <C extends Collection<RunNode>> C loadRunNodes(BetweenRunNode betweenRunNode, C collection) {

        final List<UUID> runIds = betweenRunConsensusMap.get(betweenRunNode.getSpectrumId()).getRunIds();
        for (UUID runId : runIds) {

            collection.add(new HRunNode(runId, runMap.get(runId)));
        }
        return collection;
    }

    @Override
    public RunNode loadRunNode(WithinRunNode node) {

        return new HRunNode(node.getRunId(), runMap.get(node.getRunId()));
    }

    @Override
    public Optional<BetweenRunNode> loadParent(WithinRunNode node) {

        final UUID brcId = wrcToBrcBackEdgeMap.get(node.getSpectrumId());
        if(brcId == null)
            return Optional.empty();
        else
            return Optional.of(new HBetweenRunNode(betweenRunConsensusMap.get(brcId)));
    }

    @Override
    public Stream<RunNode> getRunNodeStream() {

        return runMap.entrySet().stream().map(entry -> new HRunNode(entry.getKey(), entry.getValue()));
    }

    @Override
    public <N extends SpectrumNode> void loadScoreDistribution(Set<N> nodes, FrequencyTable frequencyTable) {

        if(nodes.isEmpty())
            return;

        final DoubleMinMax minMax = new DoubleMinMax();
        final Set<UUID> nodeIds = new HashSet<>(nodes.size());
        EdgeType edgeType = null;
        for (SpectrumNode node : nodes) {

            if(edgeType == null)
                edgeType = EdgeType.get(node, node);

            nodeIds.add(node.getSpectrumId());
            minMax.add(node.getPrecursorMz());
        }

        LOGGER.info("Loading " + edgeType + " edges from " + minMax.getMin() + " to " + minMax.getMax());
        load(edgeType, (float)minMax.getMin(), (float)minMax.getMax(), (key, value) -> {

            if (nodeIds.contains(value.getNode1Id()) && nodeIds.contains(value.getNode2Id())) {

                frequencyTable.add(value.getScore());
            }
        });
    }

    @Override
    public <N extends SpectrumNode> void loadScoreDistribution(Set<N> set1, Set<N> set2, FrequencyTable frequencyTable) {

        if(set1.isEmpty() || set2.isEmpty())
            return;

        final DoubleMinMax minMax = new DoubleMinMax();
        final Set<UUID> idSet1 = new HashSet<>(set1.size());
        EdgeType edgeType = null;
        for (SpectrumNode node : set1) {

            if(edgeType == null)
                edgeType = EdgeType.get(node, node);

            idSet1.add(node.getSpectrumId());
            minMax.add(node.getPrecursorMz());
        }

        final Set<UUID> idSet2 = new HashSet<>(set2.size());
        for (SpectrumNode node : set2) {

            idSet2.add(node.getSpectrumId());
            minMax.add(node.getPrecursorMz());
        }

        load(edgeType, (float)minMax.getMin(), (float)minMax.getMax(), (key, value) -> {

            if ((idSet1.contains(value.getNode1Id()) && idSet2.contains(value.getNode2Id())) || (idSet2.contains(value.getNode1Id()) && idSet1.contains(value.getNode2Id()))) {

                frequencyTable.add(value.getScore());
            }
        });
    }

    @Override
    public <N extends SpectrumNode, G extends SimilarityGraph<N>> void loadGraph(Set<N> nodes, SimilarityGraphBuilder<N, G> graphBuilder) {

        if(nodes.isEmpty())
            return;

        final DoubleMinMax minMax = new DoubleMinMax();
        final Map<UUID, N> nodeMap = new HashMap<>(nodes.size());
        EdgeType edgeType = null;
        for (N node : nodes) {

            if(edgeType == null)
                edgeType = EdgeType.get(node, node);

            nodeMap.put(node.getSpectrumId(), node);
            minMax.add(node.getPrecursorMz());
            graphBuilder.add(node);
        }

        load(edgeType, (float)minMax.getMin(), (float)minMax.getMax(), (key, value) -> {

            final N vertex1 = nodeMap.get(value.getNode1Id());
            final N vertex2 = nodeMap.get(value.getNode2Id());
            if (vertex1 != null && vertex2 != null) {

                graphBuilder.add(vertex1, vertex2, value.getScore());
            }
        });
    }

    @Override
    public MsnSpectrum getSpectrum(MsnNode node) {

        return msnSpectrumMap.get(node.getSpectrumId());
    }

    @Override
    public WithinRunConsensus getSpectrum(WithinRunNode withinRunNode) {

        return withinRunConsensusMap.get(withinRunNode.getSpectrumId());
    }

    @Override
    public BetweenRunConsensus getSpectrum(BetweenRunNode betweenRunNode) {

        return betweenRunConsensusMap.get(betweenRunNode.getSpectrumId());
    }

    @Override
    public Stream<BetweenRunNode> getBetweenRunNodeStream() {

        return betweenRunConsensusMap.values().stream().map(HBetweenRunNode::new);
    }

    @Override
    public Optional<RunNode> getRunNode(UUID runId) {

        return runMap.containsKey(runId) ? Optional.of(new HRunNode(runId, runMap.get(runId))) : Optional.empty();
    }

    public Stream<MsnSpectrum> getMsnSpectrumStream() {

        return msnSpectrumMap.values().stream();
    }

    public Stream<WithinRunConsensus> getWithinRunConsensusStream() {

        return withinRunConsensusMap.values().stream();
    }

    public Stream<BetweenRunConsensus> getBetweenRunConsensusStream() {

        return betweenRunConsensusMap.values().stream();
    }

    public void load(EdgeType edgeType, float minMz, float maxMz, BiConsumer<EdgeKey, EdgeValue> consumer) {

        if(minMz < 0)
            throw new IllegalStateException("Min mz was " + minMz);

        final BTreeMap<EdgeKey, EdgeValue> map;
        switch (edgeType) {

            case MSN_MSN:

                map = msnMsnEdgeMap;
                break;
            case MSN_WRC:

                map = msnWrcEdgeMap;
                break;
            case WRC_WRC:

                map = wrcWrcEdgeMap;
                break;
            case WRC_BRC:

                map = wrcBrcEdgeMap;
                break;
            case BRC_BRC:

                map = brcBrcEdgeMap;
                break;
            default:

                throw new IllegalStateException("Don't know how to deal with " + edgeType + " edges");
        }

        fromKey.setValues(minMz, Integer.MIN_VALUE);
        toKey.setValues(maxMz, Integer.MAX_VALUE);
        map.subMap(fromKey, true, toKey, true)
                .forEach(consumer);
    }

    public RunNode getRun(UUID runId) {

        final String name = runMap.get(runId);
        if(name == null)
            throw new IllegalArgumentException(runId + " is not a run id");
        return new HRunNode(runId, name);
    }

    public static class Builder {

        private final DB mapDb;
        private final File repositoryFile;

        public Builder(String repositoryRoot) {

            Preconditions.checkNotNull(repositoryRoot);

            File repositoryDir = new File(repositoryRoot);

            if(repositoryDir.exists()) {

                File[] files = repositoryDir.listFiles((dir, name) -> name.startsWith(MAP_DB_REPOSITORY));
                if(files == null) files = new File[0];
                for(File file : files) {

                    if(!file.delete()) throw new IllegalStateException("Could not delete " + file);
                }
            } else {

                if (!repositoryDir.mkdirs()) throw new IllegalStateException("Could not make " + repositoryDir);
            }

            repositoryFile = new File(repositoryRoot, MAP_DB_REPOSITORY);

            mapDb = DBMaker.newFileDB(repositoryFile)
                    .closeOnJvmShutdown()
                    .commitFileSyncDisable()
                    .transactionDisable()
                    .cacheDisable()
                    .make();
        }

        public void addMsnSpectra(Stream<MsnSpectrum> stream) {

            addSpectra(stream, MSN_SPECTRA_MAP, new UuidSerializer(), new MsnSpectrumSerializer());
        }

        public void addWithinRunConsensus(Stream<WithinRunConsensus> stream) {

            addSpectra(stream, WITHIN_RUN_CONSENSUS_MAP, new UuidSerializer(), new WithinRunConsensusSerializer());

        }

        public void addBetweenRunConsensus(Stream<BetweenRunConsensus> stream) {

            addSpectra(stream, BETWEEN_RUN_CONSENSUS_MAP, new UuidSerializer(), new BetweenRunConsensusSerializer());
        }

        private <S extends PeakList> void addSpectra(Stream<S> stream, String name, UuidSerializer keySerializer, Serializer<S> valueSerializer) {

            final HTreeMap<UUID, S> map = mapDb
                    .createHashMap(name)
                    .keySerializer(keySerializer)
                    .valueSerializer(valueSerializer).make();

            stream.forEach(spectrum -> {

                final S oldValue = map.put(spectrum.getId(), spectrum);
                if (oldValue != null)
                    throw new IllegalStateException("Have duplicate spectrum for id " + spectrum.getClass().getName() + " : " + spectrum.getId());
            });
        }

        public void addMsnMsnEdges(Iterator<FEdge> iterator) {

            doAddEdge(EdgeType.MSN_MSN, new TransformingIterator(iterator));
        }

        public void addMsnWrcEdge(Iterator<FEdge> iterator) {

            doAddEdge(EdgeType.MSN_WRC, new TransformingIterator(iterator));
        }

        public void addWrcWrcEdges(Stream<FEdge> stream) {

            stream.forEach(new FEdgeConsumer(EdgeType.WRC_WRC, mapDb));
        }

        public void addWrcWrcEdges(Iterator<FEdge> iterator) {

            doAddEdge(EdgeType.WRC_WRC, new TransformingIterator(iterator));
        }

        public void addWrcBrcEdges(Iterator<FEdge> iterator) {

            final HTreeMap<UUID, UUID> wrcToBrcIdMap = mapDb.createHashMap(WRC_BRC_BACK_EDGE_MAP).keySerializer(new UuidSerializer()).valueSerializer(new UuidSerializer()).make();

            doAddEdge(EdgeType.WRC_BRC, new TransformingIterator(iterator) {

                @Override
                public Fun.Tuple2<EdgeKey, EdgeValue> next() {

                    final Fun.Tuple2<EdgeKey, EdgeValue> next = super.next();
                    final EdgeValue edgeValue = next.b;
                    wrcToBrcIdMap.put(edgeValue.getNode1Id(), edgeValue.getNode2Id());
                    return next;
                }
            });
        }

        public void addBrcBrcEdges(Iterator<FEdge> iterator) {

            doAddEdge(EdgeType.BRC_BRC, new TransformingIterator(iterator));
        }

        private void doAddEdge(EdgeType edgeType, TransformingIterator iterator) {

            mapDb.createTreeMap(getEdgeMapName(edgeType))
                    .valueSerializer(new EdgeValueSerializer())
                    .comparator(new EdgeKeyComparator())
                    .pumpSource(iterator)
                    .make();
        }

        public void build(Map<UUID, String> runMap) {

            //add run map
            final HTreeMap<Object, Object> repoRunMap = mapDb.createHashMap(RUN_MAP)
                    .keySerializer(new UuidSerializer())
                    .valueSerializer(Serializer.STRING_INTERN)
                    .make();
            repoRunMap.putAll(runMap);

            mapDb.commit();
            mapDb.getEngine().compact();
            mapDb.close();

            //reopening to create the .t file that is needed for read only
            DBMaker.newFileDB(repositoryFile)
                    .closeOnJvmShutdown()
                    .make().close();
        }
    }

    static class TransformingIterator implements Iterator<Fun.Tuple2<EdgeKey, EdgeValue>> {

        private EdgeKey last = new EdgeKey(Float.MAX_VALUE, 0);

        private final Iterator<FEdge> src;

        public TransformingIterator(Iterator<FEdge> src) {

            this.src = src;
        }

        @Override
        public boolean hasNext() {

            return src.hasNext();
        }

        @Override
        public Fun.Tuple2<EdgeKey, EdgeValue> next() {

            final FEdge edge = src.next();

            final EdgeKey key = new EdgeKey(edge.getMz(), last.getCount());

            switch (key.compareTo(last)) {

                case -1 :

                    key.setValues(key.getMz(), 0);
                    break;
                case 1 :

                    throw new IllegalStateException("Edges are not reverse sorted, last = " + last + " current = " + key);
                case 0:

                    key.setValues(key.getMz(), key.getCount() - 1);
                    break;
                default:

                    throw new IllegalStateException("Comparison was " + key.compareTo(last));
            }

            last.setValues(key.getMz(), key.getCount());

            return new Fun.Tuple2<>(key, new EdgeValue(edge.getNode1Id(), edge.getNode2Id(), edge.getScore()));
        }
    }

    static class FEdgeConsumer implements Consumer<FEdge> {

        private final EdgeType edgeType;
        private final NavigableMap<EdgeKey, EdgeValue> tree;
        private final EdgeValue value = new EdgeValue(UUID.randomUUID(), UUID.randomUUID(), -1f);

        private EdgeKey key = new EdgeKey();
        private EdgeKey last = new EdgeKey(0, 0);

        public FEdgeConsumer(EdgeType edgeType, DB mapDb) {

            this.edgeType = edgeType;
            this.tree = mapDb.createTreeMap(getEdgeMapName(edgeType))
                    .comparator(new EdgeKeyComparator())
                    .keySerializerWrap(new EdgeKeySerializer())
                    .valueSerializer(new EdgeValueSerializer())
                    .make();
        }

        @Override
        public void accept(FEdge fEdge) {

            if(edgeType != fEdge.getEdgeType())
                throw new IllegalStateException("Attempting to add " + fEdge.getEdgeType() + " to map of " + edgeType + " edges");

            key.setValues(fEdge.getMz(), last.getCount());

            switch (key.compareTo(last)) {

                case 1 :

                    break;
                case -1 :

                    throw new IllegalStateException("Edges are not sorted");
                case 0:

                    key.setValues(key.getMz(), key.getCount() + 1);
                    break;
                default:

                    throw new IllegalStateException("Comparison was " + key.compareTo(last));
            }

            value.setValues(fEdge.getNode1Id(), fEdge.getNode2Id(), fEdge.getScore());
            tree.put(key, value);

            final EdgeKey tmp = last;
            last = key;
            key = tmp;
        }
    }
}
