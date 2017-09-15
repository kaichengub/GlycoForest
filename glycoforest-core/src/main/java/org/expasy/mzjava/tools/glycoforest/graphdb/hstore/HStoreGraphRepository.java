package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.stats.FrequencyTable;
import org.expasy.mzjava.tools.glycoforest.graphdb.*;
import org.expasy.mzjava.tools.glycoforest.graphdb.mapdb.BetweenRunConsensusSerializer;
import org.expasy.mzjava.tools.glycoforest.graphdb.mapdb.MsnSpectrumSerializer;
import org.expasy.mzjava.tools.glycoforest.graphdb.mapdb.UuidSerializer;
import org.expasy.mzjava.tools.glycoforest.graphdb.mapdb.WithinRunConsensusSerializer;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HStoreGraphRepository implements GraphRepository {

    private final Map<UUID, MsnSpectrum> msnSpectrumMap;
    private final Map<UUID, WithinRunConsensus> withinRunConsensusMap;
    private final Map<UUID, BetweenRunConsensus> betweenRunConsensusMap;
    private final Map<UUID, String> runMap;
    private final Map<UUID, UUID> wrcToBrcIdMap;

    private final DB mapDb;
    private final MapFile.Reader edgeListReader;
    private final FEdgeKeyWritable edgeKey = new FEdgeKeyWritable();
    private final FEdgeValueWritable edgeValue = new FEdgeValueWritable();

    @SuppressWarnings("UnusedDeclaration") //Is used with the @ServiceProvider annotation
    public HStoreGraphRepository() {

        final String repositoryRoot = System.getProperty("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root");
        if (repositoryRoot == null) {
            throw new IllegalStateException("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root property is not set");
        }
        mapDb = DBMaker.newFileDB(new File(repositoryRoot, "mapDb/spectra"))
                .closeOnJvmShutdown()
                .mmapFileEnable()
                .readOnly()
                .make();

        msnSpectrumMap = mapDb.getHashMap("msn_spectra_map");
        withinRunConsensusMap = mapDb.getHashMap("within_run_consensus_map");
        betweenRunConsensusMap = mapDb.getHashMap("between_run_consensus_map");
        runMap = mapDb.getHashMap("runMap");
        wrcToBrcIdMap = mapDb.getHashMap("wrc_brc_edge_map");

        try {

            LocalFileSystem fs = FileSystem.getLocal(new Configuration());
            edgeListReader = new MapFile.Reader(new Path(repositoryRoot, "edges_map_file"), fs.getConf());
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }

    @Override
    public void shutdown() {

        if (!mapDb.isClosed()) {

            mapDb.close();
        }
        try {
            edgeListReader.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <C extends Collection<MsnNode>> C loadChildren(WithinRunNode withinRunNode, C nodes) {

        final UUID wrcId = withinRunNode.getSpectrumId();
        load(withinRunNode.getMinMemberMz(), withinRunNode.getMaxMemberMz(), (key, value) -> {

            if (key.getEdgeType() == EdgeType.MSN_WRC && value.getNode2Id().equals(wrcId)) {

                nodes.add(new HMsnNode(msnSpectrumMap.get(value.getNode1Id())));
            }
        });
        return nodes;
    }

    @Override
    public <C extends Collection<WithinRunNode>> C loadChildren(BetweenRunNode betweenRunNode, C nodes) {

        final UUID brcId = betweenRunNode.getSpectrumId();
        load(betweenRunNode.getMinWithinRunMz(), betweenRunNode.getMaxWithinRunMz(), (key, value) -> {

            if (key.getEdgeType() == EdgeType.WRC_BRC && value.getNode2Id().equals(brcId)) {

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

        final UUID brcId = wrcToBrcIdMap.get(node.getSpectrumId());
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

        throw new UnsupportedOperationException();
    }

    @Override
    public <N extends SpectrumNode> void loadScoreDistribution(Set<N> set1, Set<N> set2, FrequencyTable frequencyTable) {

        throw new UnsupportedOperationException();
    }

    public <N extends SpectrumNode, G extends SimilarityGraph<N>> void loadGraph(Set<N> nodes, SimilarityGraphBuilder<N, G> graphBuilder) {

        if(nodes.isEmpty())
            return;

        double minMz = Double.MIN_VALUE;
        double maxMz = 0;
        EdgeType edgeType = null;
        final Map<UUID, N> nodeMap = new HashMap<>(nodes.size());
        for (N node : nodes) {

            if(edgeType == null)
                edgeType = EdgeType.get(node, node);

            nodeMap.put(node.getSpectrumId(), node);
            minMz = Math.min(minMz, node.getPrecursorMz());
            maxMz = Math.max(maxMz, node.getPrecursorMz());
            graphBuilder.add(node);
        }

        final EdgeType finalEdgeType = edgeType;

        load(minMz, maxMz, (key, value) -> {

            if (key.getEdgeType() != finalEdgeType)
                return;

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

    public void forEachWithinRunNode(Consumer<WithinRunNode> consumer) {

        withinRunConsensusMap.values().forEach(consensus -> consumer.accept(new HWithinRunNode(consensus)));
    }

    public void forEachMsnNode(Consumer<MsnNode> consumer) {

        msnSpectrumMap.values().forEach(spectrum -> consumer.accept(new HMsnNode(spectrum)));
    }

    public void load(double minMz, double maxMz, BiConsumer<FEdgeKeyWritable, FEdgeValueWritable> consumer) {

        try {

            //edge type is not used in sort of keys, so any value will do here
            edgeKey.setValues((float) minMz, EdgeType.MSN_MSN, Optional.<UUID>empty());
            edgeListReader.reset();
            edgeListReader.seek(edgeKey);
            while (edgeListReader.next(edgeKey, edgeValue) && edgeKey.getMz() <= maxMz) {

                consumer.accept(edgeKey, edgeValue);
            }
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }

    public void forEachEdge(BiConsumer<FEdgeKeyWritable, FEdgeValueWritable> consumer){

        try {

            edgeListReader.reset();
            while (edgeListReader.next(edgeKey, edgeValue)) {

                consumer.accept(edgeKey, edgeValue);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<FEdge> findEdge(UUID uuid) {

        try {

            final List<FEdge> edges = new ArrayList<>();
            edgeListReader.reset();
            while (edgeListReader.next(edgeKey, edgeValue)) {

                if(edgeValue.getNode1Id().equals(uuid) || edgeValue.getNode2Id().equals(uuid))
                    edges.add(new FEdge(edgeKey, edgeValue));
            }
            return edges;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean hasMsnSpectrum(UUID id) {

        return msnSpectrumMap.containsKey(id);
    }

    public boolean hasWithinRunConsensus(UUID id) {

        return withinRunConsensusMap.containsKey(id);
    }

    public boolean hasBetweenRunConsensus(UUID id) {

        return betweenRunConsensusMap.containsKey(id);
    }

    public static class Builder {

        private static final Logger LOGGER = Logger.getLogger(Builder.class.getName());

        private final String repositoryRoot;

        private final Map<UUID, MsnSpectrum> msnSpectrumMap;
        private final Map<UUID, WithinRunConsensus> withinRunConsensusMap;
        private final Map<UUID, BetweenRunConsensus> betweenRunConsensusMap;
        private final Map<UUID, UUID> wrcToBrcIdMap;

        private final DB mapDb;
        private final LocalFileSystem fs;


        public Builder(String repositoryRoot) {

            checkNotNull(repositoryRoot);

            this.repositoryRoot = repositoryRoot;

            File mapDbDir = new File(repositoryRoot, "mapDb");
            if (mapDbDir.exists()) {

                try {
                    FileUtils.deleteDirectory(mapDbDir);
                } catch (IOException e) {

                    throw new IllegalStateException(e);
                }
            }
            if (!mapDbDir.mkdirs()) throw new IllegalStateException("Could not make " + mapDbDir);

            mapDb = DBMaker.newFileDB(new File(mapDbDir, "spectra"))
                    .closeOnJvmShutdown()
                    .mmapFileEnable()
                    .commitFileSyncDisable()
                    .transactionDisable()
                    .cacheDisable()
                    .make();

            msnSpectrumMap = mapDb.createHashMap("msn_spectra_map").keySerializer(new UuidSerializer()).valueSerializer(new MsnSpectrumSerializer()).make();
            withinRunConsensusMap = mapDb.createHashMap("within_run_consensus_map").keySerializer(new UuidSerializer()).valueSerializer(new WithinRunConsensusSerializer()).make();
            betweenRunConsensusMap = mapDb.createHashMap("between_run_consensus_map").keySerializer(new UuidSerializer()).valueSerializer(new BetweenRunConsensusSerializer()).make();
            wrcToBrcIdMap = mapDb.createHashMap("wrc_brc_edge_map").keySerializer(new UuidSerializer()).valueSerializer(new UuidSerializer()).make();

            try {

                fs = FileSystem.getLocal(new Configuration());
            } catch (Exception e) {

                throw new IllegalStateException(e);
            }
        }

        public void addMsnSpectra(Stream<MsnSpectrum> stream) {

            stream.forEach(spectrum -> putSpectrum(msnSpectrumMap, spectrum));
        }

        public void addWithinRunConsensus(Stream<WithinRunConsensus> stream) {

            stream.forEach(spectrum -> putSpectrum(withinRunConsensusMap, spectrum));
        }

        public void addBetweenRunConsensus(Stream<BetweenRunConsensus> stream) {

            stream.forEach(spectrum -> putSpectrum(betweenRunConsensusMap, spectrum));
        }

        private <S extends PeakList> void putSpectrum(Map<UUID, S> map, S spectrum) {

            final S oldValue = map.put(spectrum.getId(), spectrum);
            if (oldValue != null)
                throw new IllegalStateException("Have duplicate spectrum for id " + spectrum.getClass().getName() + " : " + spectrum.getId());
        }

        public void addEdges(String inputRoot, EdgeType... edgeTypes) {

            //extract back edges
            try {

                final Path[] allPaths = Stream.of(fs.listStatus(new Path(inputRoot, "wrc_brc_edges"))).map(FileStatus::getPath).filter(path -> !path.getName().startsWith("_")).toArray(Path[]::new);

                for (Path path : allPaths) {

                    final SequenceFile.Reader reader = new SequenceFile.Reader(fs.getConf(), SequenceFile.Reader.file(path));
                    final FEdgeKeyWritable key = new FEdgeKeyWritable();
                    final FEdgeValueWritable value = new FEdgeValueWritable();
                    while (reader.next(key, value)) {

                        final UUID oldValue = wrcToBrcIdMap.put(value.getNode1Id(), value.getNode2Id());
                        if(oldValue != null)
                            throw new IllegalStateException("Have duplicate wrc - brc edge");
                    }
                    reader.close();
                }
            } catch (IOException e) {

                throw new IllegalStateException(e);
            }

            final List<Path> srcPaths = Stream.of(edgeTypes)
                    .map(edgeType -> new Path(inputRoot, edgeType.name().toLowerCase() + "_edges"))
                    .collect(Collectors.toList());

            final Path[] inFiles = srcPaths.stream().flatMap(srcPath -> {
                try {

                    return Stream.of(fs.listStatus(srcPath, path -> path.getName().charAt(0) != '_')).map(FileStatus::getPath);
                } catch (IOException e) {

                    throw new IllegalStateException(e);
                }
            }).toArray(Path[]::new);

            try {

                final SequenceFile.Sorter sorter = new SequenceFile.Sorter(fs, new FEdgeKeyRawComparator(), FEdgeKeyWritable.class, FEdgeValueWritable.class, fs.getConf());
                sorter.sort(inFiles, new Path(repositoryRoot + "/edges_map_file", "data"), true);

                long entries = MapFile.fix(fs, new Path(repositoryRoot + "/edges_map_file"), FEdgeKeyWritable.class, FEdgeValueWritable.class, false, fs.getConf());
                LOGGER.info("Create map files with " + entries + " entries");
            } catch (Exception e) {

                throw new IllegalStateException(e);
            }

            srcPaths.forEach(srcPath -> {
                try {

                    fs.delete(srcPath, true);
                } catch (IOException e) {

                    throw new IllegalStateException(e);
                }
            });

        }

        public void build(Map<UUID, String> runMap) {

            //add run map
            mapDb.createHashMap("runMap").keySerializer(new UuidSerializer()).valueSerializer(Serializer.STRING_INTERN).make().putAll(runMap);

            mapDb.commit();
            mapDb.close();
        }
    }
}
