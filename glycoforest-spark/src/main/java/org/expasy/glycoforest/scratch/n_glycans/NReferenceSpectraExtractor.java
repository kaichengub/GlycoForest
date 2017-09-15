package org.expasy.glycoforest.scratch.n_glycans;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.expasy.glycoforest.avro.io.GlycanReferenceSpectrumValue;
import org.expasy.glycoforest.mol.FragmentCoverageFunc;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.spectrum.GlycanReferenceSpectrum;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.glycoforest.app.ContaminantFunction;
import org.expasy.glycoforest.app.MsnSimGraphFunc;
import org.expasy.glycoforest.app.RunSplitterFmf;
import org.expasy.glycoforest.app.WithinRunClusterFmf;
import org.expasy.glycoforest.app.factories.SpectrumReaderFactory;
import org.expasy.glycoforest.app.parameters.DefaultGlycoforestParameters;
import org.expasy.glycoforest.app.GlycoforestParameters;
import org.expasy.mzjava.core.io.IterativeReaders;
import org.expasy.mzjava.core.io.ms.spectrum.MzxmlReader;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.hadoop.io.HadoopSpectraWriter;
import org.expasy.mzjava.hadoop.io.MsnSpectrumValue;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusValue;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import scala.Tuple2;
import scala.Tuple3;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NReferenceSpectraExtractor {

    private final GlycoforestParameters parameters;
    private final String contaminantsPath;

    private NReferenceSpectraExtractor(final GlycoforestParameters parameters, final String contaminantsPath) {

        this.parameters = parameters;
        this.contaminantsPath = contaminantsPath;
    }

    private void clusterAndSave(final JavaSparkContext sc, Map<UUID, String> inputFilePaths, final String outputPath, final String suffix) throws IOException {

        final Map<UUID, List<WithinRunConsensus>> runs = clusterMsnSimGraphs(inputFilePaths, sc)
                .mapToPair(t -> new Tuple2<>(t._1().getRunId(), t._1()))
                .groupByKey()
                .mapToPair(t -> new Tuple2<UUID, List<WithinRunConsensus>>(t._1(), Lists.newArrayList(t._2())))
                .collectAsMap();

        for (final Map.Entry<UUID, List<WithinRunConsensus>> entry : runs.entrySet()) {

            final HadoopSpectraWriter<WithinRunConsensus> writer = new HadoopSpectraWriter<>(outputPath, parameters.getRunIdPathMap().get(entry.getKey()) + suffix, new WithinRunConsensusValue());
            for (WithinRunConsensus consensus : entry.getValue()) {

                writer.write(consensus);
            }
            writer.close();
        }
    }

    private JavaPairRDD<WithinRunConsensus, List<MsnSpectrum>> clusterMsnSimGraphs(Map<UUID, String> hdmsnFilePaths, JavaSparkContext sc) throws IOException {

        final HadoopSpectraReader<PeakAnnotation, MsnSpectrum> reader = new HadoopSpectraReader<>(sc.hadoopConfiguration(), new Path(contaminantsPath), new MsnSpectrumValue());
        final Broadcast<List<MsnSpectrum>> contaminants = sc.broadcast(IterativeReaders.toArrayList(reader));
        return sc.parallelize(hdmsnFilePaths.entrySet().stream().map(entry -> new Tuple2<>(entry.getValue(), entry.getKey())).collect(Collectors.toList()), Math.min(hdmsnFilePaths.size(), 120))
                .flatMapToPair(new RunSplitterFmf(parameters.readerFactory(),
                        parameters.precursorTolerance(),
                        parameters.retentionTimeTolerance(),
                        parameters.msnSpectrumPredicate()))
                .map(new MsnSimGraphFunc(parameters.msnSimFunc(),
                        parameters.precursorTolerance(),
                        parameters.msnPeakProcessor()))
                .flatMapToPair(new WithinRunClusterFmf(parameters.withinRunConsensusPeakMerger(),
                        parameters.msnClusterBuilder(),
                        parameters.withinRunConsensusPredicate(),
                        parameters.withinRunChargeStateEstimator(),
                        parameters.precision()))
                .filter(new ContaminantFunction(0.7, parameters.contaminantSimFunc(), parameters.precursorTolerance(), contaminants));
    }

    private static void cluster(final String outputPath) throws IOException {

        final Map<UUID, String> runMap = new HashMap<>();
        runMap.putAll(NGlycanRunMap.getHeartMap());
        runMap.putAll(NGlycanRunMap.getHUPOMap());
        runMap.putAll(NGlycanRunMap.getPSGLMap());
        runMap.putAll(NGlycanRunMap.getStandardsMap());

        final String inputRoot = "D:\\data\\ms\\jin\\n-linked\\mzxml_turbocharger";
        final Map<UUID, String> inputFilePaths = runMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> inputRoot + "\\" + e.getValue() + ".mzXML"));

        final List<String> missingFiles = inputFilePaths.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(path -> !new File(path).exists())
                .collect(Collectors.toList());

        if (!missingFiles.isEmpty()) {

            missingFiles.forEach(System.out::println);
            return;
        }

        final SparkConf conf = new SparkConf()
                .setAppName("Glycoforest")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", "org.expasy.mzjava.spark.MzJavaKryoRegistrator")
                .set("spark.master", "local[12]")
                .set("spark.executor.memory", "2G");

        try (final JavaSparkContext sc = new JavaSparkContext(conf)) {

            final DefaultGlycoforestParameters parameters = new DefaultGlycoforestParameters(sc, inputRoot, "C:\\Users\\Oliver\\Documents\\tmp\\sugar_composition") {

                @Override
                public Map<UUID, String> getRunIdPathMap() {

                    return runMap;
                }

                @Override
                public SpectrumReaderFactory<MsnSpectrum> readerFactory() {

                    return spectrumPathString -> {
                        try {

                            final MzxmlReader reader = new MzxmlReader(new File(spectrumPathString), PeakList.Precision.DOUBLE);
                            reader.removeConsistencyChecks(EnumSet.allOf(MzxmlReader.ConsistencyCheck.class));
                            return reader;
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    };
                }
            };

            new NReferenceSpectraExtractor(parameters, "C:\\Users\\Oliver\\Documents\\tmp\\glycoforest\\contaminants.hdmsn")
                    .clusterAndSave(sc, inputFilePaths, outputPath, ".hdwrc");
        }
    }

    private static List<GlycanReferenceSpectrum> findReferences(final String root, final Collection<Tuple2<String, RetentionTimeTable>> runs, final GlycanMassCalculator massCalculator, final Tolerance precursorTolerance, final Tolerance fragmentTolerance) {

        return runs.stream()
                .flatMap(runTuple -> readAllHdwrc(root, runTuple._1() + ".hdwrc").stream().map(s -> new Tuple3<>(runTuple._1(), s, runTuple._2())))
                .flatMap(t -> {

                    final WithinRunConsensus consensus = t._2();

                    final int z = consensus.getPrecursor().getCharge();
                    final double mz = consensus.getPrecursor().getMz();
                    Predicate<SugarStructure> structurePredicate = s -> precursorTolerance.withinTolerance(massCalculator.calcMz(s.getComposition(), z), mz);
                    final List<SugarStructure> hits = t._3().getStructure(t._1(), toRange(consensus.getRetentionTimeInterval()), structurePredicate);

                    if (hits.isEmpty() || hits.size() > 1) {

                        return Stream.empty();
                    } else {

                        return Stream.of(new Tuple2<>(hits.get(0), consensus));
                    }
                })
                .collect(Collectors.groupingBy(Tuple2::_1, Collectors.mapping(Tuple2::_2, Collectors.toList())))
                .entrySet().stream()
                .map(new ReferenceSpectrumFunction(new FragmentCoverageFunc(massCalculator, fragmentTolerance)))
                .collect(Collectors.toList());
    }

    private static Range<Double> toRange(final RetentionTimeInterval retentionTimeInterval) {

        return Range.closed(retentionTimeInterval.getMinRetentionTime(), retentionTimeInterval.getMaxRetentionTime());
    }

    private static List<WithinRunConsensus> readAllHdwrc(final String path, final String name) {

        final List<WithinRunConsensus> spectra = new ArrayList<>();
        try {

            final HadoopSpectraReader<LibPeakAnnotation, WithinRunConsensus> reader = new HadoopSpectraReader<>(path, name, new WithinRunConsensusValue());
            while (reader.hasNext()) {

                spectra.add(reader.next());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return spectra;
    }

    public static void main(String[] args) throws Exception {

        final String clustersPath = "D:\\data\\ms\\jin\\n-linked\\hdwrc";

//        cluster(clustersPath);

        final GigCondensedReader reader = new CachedGigCondensedReader();

        final List<Tuple2<String, RetentionTimeTable>> runs = Stream.of(
                new HeartRetentionTimeTable(reader),
                new HupoRetentionTimeTable(reader),
                new NStandardsRetentionTimeTable(reader),
                new PgslRetentionTimeTable(reader)
        )
                .flatMap(table -> table.runStream().map(run -> new Tuple2<>(run, table)))
                .collect(Collectors.toList());

        final AbsoluteTolerance precursorTolerance = new AbsoluteTolerance(0.3);
        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        final List<GlycanReferenceSpectrum> allReferences = findReferences(clustersPath, runs, massCalculator, precursorTolerance, new AbsoluteTolerance(0.3));

        final Predicate<GlycanReferenceSpectrum> referenceSelectionPredicate = referenceSpectrum ->
                referenceSpectrum.getCoverage() >= 1.0 &&
                precursorTolerance.withinTolerance(massCalculator.calcMz(referenceSpectrum.getSugarStructure().getComposition(), referenceSpectrum.getPrecursor().getCharge()), referenceSpectrum.getPrecursor().getMz());

        final List<GlycanReferenceSpectrum> references = allReferences.stream().filter(referenceSelectionPredicate).collect(Collectors.toList());

        System.out.println("All structures " + ((CachedGigCondensedReader)reader).cache.size());                      //sout
        System.out.println("Found " + allReferences.size() + " references. Retained " + references.size());                      //sout

        final HadoopSpectraWriter<GlycanReferenceSpectrum> writer = new HadoopSpectraWriter<>("D:\\data\\ms\\jin\\n-linked\\reference_lib", "n-linked_reference.hdref", new GlycanReferenceSpectrumValue());
        for(GlycanReferenceSpectrum reference : references) {

            writer.write(reference);
        }
        writer.close();
    }

    private static class ReferenceSpectrumFunction implements Function<Map.Entry<SugarStructure, List<WithinRunConsensus>>, GlycanReferenceSpectrum> {

        final FragmentCoverageFunc coverageFunc;

        private ReferenceSpectrumFunction(final FragmentCoverageFunc coverageFunc) {

            this.coverageFunc = coverageFunc;
        }

        @Override
        public GlycanReferenceSpectrum apply(final Map.Entry<SugarStructure, List<WithinRunConsensus>> t) {

            final WithinRunConsensus consensus = Lists.newArrayList(t.getValue()).stream().max(Comparator.comparing(Spectrum::getTotalIonCurrent)).orElseThrow(IllegalStateException::new);
            return new GlycanReferenceSpectrum(t.getKey(), consensus, coverageFunc.calcCoverage(t.getKey(), consensus));
        }
    }

    private static class CachedGigCondensedReader extends GigCondensedReader {

        private static final Logger LOGGER = Logger.getLogger(CachedGigCondensedReader.class.getName());

        private final Map<String, SugarStructure> cache = new HashMap<>();

        @Override
        public SugarStructure readStructure(final String id, final String structure) {

            final SugarStructure sugarStructure;
            if (cache.containsKey(structure)) {

                sugarStructure = cache.get(structure);
                if (!id.equals(sugarStructure.getLabel()))
                    LOGGER.warning(structure + " has different labels");
                return sugarStructure;
            } else {

                sugarStructure = super.readStructure(id, structure);
                cache.put(structure, sugarStructure);
            }
            return sugarStructure;
        }
    }
}
