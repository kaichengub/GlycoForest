package org.expasy.glycoforest.scratch;

import org.apache.commons.io.FilenameUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.expasy.glycoforest.avro.io.GlycanReferenceSpectrumValue;
import org.expasy.glycoforest.mol.FragmentCoverageFunc;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.ms.spectrum.GlycanReferenceSpectrum;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.app.ReferenceDbQueryBuilder;
import org.expasy.glycoforest.app.SolveFunction;
import org.expasy.glycoforest.app.SpectrumEntry;
import org.expasy.glycoforest.app.export.poi.SpectrumExporter;
import org.expasy.glycoforest.app.SparkApp;
import org.expasy.glycoforest.app.parameters.DefaultGlycoforestParameters;
import org.expasy.glycoforest.app.GlycoforestParameters;
import org.expasy.glycoforest.writer.GigCondensedWriter;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.peaklist.peaktransformer.SqrtTransformer;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.hadoop.io.HadoopSpectraWriter;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.spark.CachedAbsoluteTolerance;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusValue;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.expasy.mzjava.utils.PathUtils;
import scala.Tuple2;
import scala.Tuple3;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PositiveOLinkedApp extends SparkApp {

    private static final Logger LOGGER = Logger.getLogger(PositiveOLinkedApp.class.getName());

    private final GlycoforestParameters parameters;
    private final String contaminantsPath;

    private final double scoreThreshold = 0.1;
    private final Cached<GlycanMassCalculator> massCalculatorFactory;
    private final Cached<Tolerance> fragmentToleranceFactory;

    private PositiveOLinkedApp(final GlycoforestParameters parameters, final Cached<GlycanMassCalculator> massCalculatorFactory, final Cached<Tolerance> fragmentToleranceFactory, final String contaminantsPath) {

        this.parameters = parameters;
        this.massCalculatorFactory = massCalculatorFactory;
        this.fragmentToleranceFactory = fragmentToleranceFactory;
        this.contaminantsPath = contaminantsPath;
    }

    private List<GsmResultList> run(JavaSparkContext sc, final List<SpectrumEntry> referenceSpectra, final JavaRDD<WithinRunConsensus> withinRunConsensusRDD) {

        long start = System.currentTimeMillis();

        final Broadcast<List<SpectrumEntry>> bcReferenceSpectra = sc.broadcast(referenceSpectra);

        final List<GsmResultList> results = withinRunConsensusRDD.collect().parallelStream()
                .map(s -> new SpectrumEntry(s, s.copy(new PeakProcessorChain<LibPeakAnnotation>().add(new SqrtTransformer<>())), Collections.emptyMap(), 0))
                .map(new GlycanAssignmentFunction(scoreThreshold, massCalculatorFactory, fragmentToleranceFactory, bcReferenceSpectra))
                .collect(Collectors.toList());

        LOGGER.info("Spark ran in " + (System.currentTimeMillis() - start) / 1000d + "s");

        return results;
    }

    //negative
    public static void negative_main(String[] args) throws Exception {

        final String inputPath = args[0];
        final String outputPath = args[1];
        final String glycoModDataPath = args[2];
        final String contaminantsPath = args[3];

        SparkConf conf = new SparkConf()
                .setAppName("Glycoforest")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", "org.expasy.mzjava.spark.MzJavaKryoRegistrator");
//                .set("spark.kryo.registrationRequired", "true");

        if (args.length > 4 && "local".equals(args[4])) {

            loadProperties(conf);
            PathUtils.deletePath(outputPath);
            if (!new File(outputPath).mkdirs()) throw new IllegalStateException("Could not make " + outputPath);
        }

        final long start = System.currentTimeMillis();
        try (final JavaSparkContext sc = new JavaSparkContext(conf)) {

            final GlycoforestParameters parameters = new DefaultGlycoforestParameters(sc, inputPath, glycoModDataPath) {

                @Override
                public Map<UUID, String> getRunIdPathMap() {

                    return Collections.singletonMap(UUID.fromString("b661ceb9-b57b-4b20-8ac0-4e13f059c8db"), inputPath + "/JC_141128PGMa.hdmsn");
                }
            };

            final List<WithinRunConsensus> negativeWithinRunConsensuses = loadWithinRunConsensus("D:\\data\\ms\\jin\\glycoforest_positive\\hdwrc", "JC_141128PGMa.hdwrc");
            final JavaRDD<WithinRunConsensus> negativeWithinRunConsensusRDD = sc.parallelize(negativeWithinRunConsensuses);

            final Cached<GlycanMassCalculator> massCalculatorFactory = new Cached<GlycanMassCalculator>() {
                @Override
                protected GlycanMassCalculator build() {

                    return GlycanMassCalculator.newEsiNegativeReduced();
                }
            };

            final Cached<Tolerance> fragmentToleranceFactory = new CachedAbsoluteTolerance(0.3);

            final PositiveOLinkedApp glycoforestApp2 = new PositiveOLinkedApp(parameters, massCalculatorFactory, fragmentToleranceFactory, contaminantsPath);
//            final List<SpectrumEntry> referenceSpectra = ReferenceDbEvaluator.extractReferenceSpectra();
            final List<SpectrumEntry> referenceSpectra = readAndConvert("D:\\data\\ms\\jin\\glycoforest_positive\\reference_lib", "pgm_reference.hdref", massCalculatorFactory.get());
            final List<GsmResultList> results = glycoforestApp2.run(sc, referenceSpectra, negativeWithinRunConsensusRDD);

            final double minScore = 0.5;

            LOGGER.info("Queries " + results.size() + " results " + results.stream().filter(r -> !r.isEmpty()).count());                      //sout
            LOGGER.info("Good " + results.stream().filter(r -> r.getMetaScore() > minScore).count());

//            export(-1, "", outputPath, results, parameters, massCalculatorFactory, fragmentToleranceFactory);

//            mapResultsToPositiveMode(fragmentToleranceFactory, results, minScore);
            checkResults(referenceSpectra, results);
            LOGGER.info("Ran Glycoforest in " + (System.currentTimeMillis() - start) / 1000.0 + "s");
        }
    }

    //positive
    public static void positive_main(String[] args) throws Exception {

        final String inputPath = args[0];
        final String outputPath = args[1];
        final String glycoModDataPath = args[2];
        final String contaminantsPath = args[3];

        SparkConf conf = new SparkConf()
                .setAppName("Glycoforest")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", "org.expasy.mzjava.spark.MzJavaKryoRegistrator");
//                .set("spark.kryo.registrationRequired", "true");

        if (args.length > 4 && "local".equals(args[4])) {

            loadProperties(conf);
            PathUtils.deletePath(outputPath);
            if (!new File(outputPath).mkdirs()) throw new IllegalStateException("Could not make " + outputPath);
        }

        final long start = System.currentTimeMillis();
        try (final JavaSparkContext sc = new JavaSparkContext(conf)) {

            final GlycoforestParameters parameters = new DefaultGlycoforestParameters(sc, inputPath, glycoModDataPath) {

                @Override
                public Map<UUID, String> getRunIdPathMap() {

                    return Collections.singletonMap(UUID.fromString("b661ceb9-b57b-4b20-8ac0-4e13f059c8db"), inputPath + "/JC_141128PGMa_pos.hdmsn");
                }
            };

            final List<WithinRunConsensus> negativeWithinRunConsensuses = loadWithinRunConsensus("D:\\data\\ms\\jin\\glycoforest_positive\\hdwrc", "JC_141128PGMa_pos.hdwrc");
            final JavaRDD<WithinRunConsensus> negativeWithinRunConsensusRDD = sc.parallelize(negativeWithinRunConsensuses);

            final Cached<GlycanMassCalculator> massCalculatorFactory = new Cached<GlycanMassCalculator>() {
                @Override
                protected GlycanMassCalculator build() {

                    return GlycanMassCalculator.newEsiPositiveReduced();
                }
            };

            final Cached<Tolerance> fragmentToleranceFactory = new CachedAbsoluteTolerance(0.3);

            final PositiveOLinkedApp glycoforestApp2 = new PositiveOLinkedApp(parameters, massCalculatorFactory, fragmentToleranceFactory, contaminantsPath);
//            final List<SpectrumEntry> referenceSpectra = ReferenceDbEvaluator.extractReferenceSpectra();
            final List<SpectrumEntry> referenceSpectra = readAndConvert("D:\\data\\ms\\jin\\glycoforest_positive\\reference_lib", "pgm_reference_pos.hdref", massCalculatorFactory.get());
            final List<GsmResultList> results = glycoforestApp2.run(sc, referenceSpectra, negativeWithinRunConsensusRDD);

            final double minScore = 0.5;

            LOGGER.info("Queries " + results.size() + " results " + results.stream().filter(r -> !r.isEmpty()).count());                      //sout
            LOGGER.info("Good " + results.stream().filter(r -> r.getMetaScore() > minScore).count());

//            export(-1, "_pos", outputPath, results, parameters, massCalculatorFactory, fragmentToleranceFactory);

            LOGGER.info("Ran Glycoforest in " + (System.currentTimeMillis() - start) / 1000.0 + "s");

            checkResults(referenceSpectra, results);
        }
    }

    private static void checkResults(final List<SpectrumEntry> referenceSpectra, final List<GsmResultList> results) {

        final Map<UUID, SpectrumEntry> referenceMap = referenceSpectra.stream()
                .collect(Collectors.groupingBy(r -> r.getRawSpectrum().getId()))
                .entrySet()
                .stream()
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toMap(r -> r.getRawSpectrum().getId(), r -> r));

        final long hits = results.stream()
                .filter(result -> referenceMap.containsKey(result.getSolveTask().getVertex().getConsensus().getId()))
                .count();

        final long correct = results.stream()
                .filter(result -> referenceMap.containsKey(result.getSolveTask().getVertex().getConsensus().getId()))
                .filter(result -> referenceMap.get(result.getSolveTask().getVertex().getConsensus().getId()).structureStream().findFirst().get().isIsomorphic(result.getBest().getStructure(), IsomorphismType.ROOTED_TOPOLOGY))
                .count();

        System.out.println("correct " + correct + " of " + hits + " references " + referenceSpectra.size());                      //sout
    }

    private static List<SpectrumEntry> readAndConvert(final String path, final String name, final GlycanMassCalculator massCalculator) throws IOException {

        final List<SpectrumEntry> referenceSpectra = new ArrayList<>();
        final HadoopSpectraReader<LibPeakAnnotation, GlycanReferenceSpectrum> reader = new HadoopSpectraReader<>(path, name, new GlycanReferenceSpectrumValue());
        final UUID runId = UUID.randomUUID();
        while (reader.hasNext()) {

            final GlycanReferenceSpectrum referenceSpectrum = reader.next();
            final Peak precursor = referenceSpectrum.getPrecursor();

            final WithinRunConsensus rawSpectrum = new WithinRunConsensus(referenceSpectrum.getPrecision());
            rawSpectrum.setId(referenceSpectrum.getId());
            rawSpectrum.getPrecursor().setValues(massCalculator.calcMz(referenceSpectrum.getSugarStructure().getComposition(), 1), 1, 1);

            rawSpectrum.setFields(runId, referenceSpectrum.getMemberCount(), referenceSpectrum.getSimScoreMean(), referenceSpectrum.getSimScoreStdev(), precursor.getMz(), precursor.getMz(), new ScanNumberInterval(1, 2), new RetentionTimeInterval(1, 2, TimeUnit.SECOND));
            rawSpectrum.addPeaks(referenceSpectrum);

            referenceSpectra.add(new SpectrumEntry(rawSpectrum, rawSpectrum.copy(new PeakProcessorChain<LibPeakAnnotation>().add(new SqrtTransformer<>())), Collections.singletonMap(referenceSpectrum.getSugarStructure(), 1L), referenceSpectrum.getCoverage()));
        }
        reader.close();

        return referenceSpectra;
    }

    private static void mapResultsToPositiveMode(final Cached<Tolerance> fragmentToleranceFactory, final List<GsmResultList> results, final double minScore) throws IOException {

        final Map<SugarComposition, List<WithinRunConsensus>> compositionMap = results.stream().filter(r -> r.getMetaScore() > minScore)
                .collect(Collectors.groupingBy((GsmResultList r) -> r.getBest().getStructure().getComposition(), Collectors.mapping((GsmResultList r) -> (WithinRunConsensus) r.getSolveTask().getVertex().getConsensus(), Collectors.toList())));

        final List<SugarComposition> compositions = new ArrayList<>(compositionMap.keySet());
        final List<WithinRunConsensus> positiveWithinRunConsensus = loadWithinRunConsensus("D:\\data\\ms\\jin\\glycoforest_positive\\hdwrc", "JC_141128PGMa_pos.hdwrc");

        final Tolerance precursorTolerance = fragmentToleranceFactory.get();
        final GlycanMassCalculator posMassCalculator = GlycanMassCalculator.newEsiPositiveReduced();
        final Map<SugarComposition, List<WithinRunConsensus>> posCompositionMap = positiveWithinRunConsensus.stream()
                .filter(s -> s.getPrecursor().getCharge() == 1)
                .flatMap(s -> {

                    final double mz = s.getPrecursor().getMz();
                    final Optional<SugarComposition> hit = compositions.stream()
                            .filter(c -> precursorTolerance.withinTolerance(posMassCalculator.calcMz(c, 1), mz))
                            .findFirst();

                    if (hit.isPresent())
                        return Stream.of(new Tuple2<>(hit.get(), s));
                    else
                        return Stream.empty();
                })
                .collect(Collectors.groupingBy(Tuple2::_1, Collectors.mapping(Tuple2::_2, Collectors.toList())));

        compositionMap.forEach((c, l) -> {
            System.out.println(c + "\t" + l.size());
            l.stream()
                    .sorted(Comparator.comparing(WithinRunConsensus::getRetentionTime))
                    .forEach(s -> System.out.println(s.getRetentionTime() / 60 + "\t" + s.getTotalIonCurrent()));
            System.out.println();                      //sout
            posCompositionMap.getOrDefault(c, Collections.emptyList()).stream()
                    .sorted(Comparator.comparing(WithinRunConsensus::getRetentionTime))
                    .forEach(s -> System.out.println(s.getRetentionTime() / 60 + "\t" + s.getTotalIonCurrent()));
            System.out.println("------------------------------------------------");                      //sout
        });

        final List<Tuple3<GsmResultList, WithinRunConsensus, Double>> mappedStructures = results.stream()
                .filter(r -> !r.isEmpty())
                .filter(r -> r.getMetaScore() > minScore)
                .flatMap(r -> {

                    final WithinRunConsensus s = (WithinRunConsensus) r.getSolveTask().getVertex().getConsensus();
                    final Optional<Tuple2<Double, WithinRunConsensus>> min = positiveWithinRunConsensus.stream()
                            .filter(s2 -> precursorTolerance.withinTolerance(s.getPrecursor().getMz() + 23.9965, s2.getPrecursor().getMz()))
                            .map(s2 -> new Tuple2<>(Math.abs(s2.getRetentionTime() - s.getRetentionTime()), s2))
                            .min(Comparator.comparing(Tuple2::_1));
                    if (min.isPresent())
                        return Stream.of(new Tuple3<>(r, min.get()._2(), s.getRetentionTime() - min.get()._2().getRetentionTime()));
                    else
                        return Stream.empty();
                })
                .filter(t -> Math.abs(t._3()) < 30)
                .collect(Collectors.toList());

        final GigCondensedWriter structureWriter = new GigCondensedWriter();
        mappedStructures.forEach(t -> {

            final WithinRunConsensus s = (WithinRunConsensus) t._1().getSolveTask().getVertex().getConsensus();
            System.out.println(structureWriter.write(t._1().getBest().getStructure()) + "\t" + s.getPrecursor().getMz() + "\t" + s.getRetentionTime() / 60 + "\t" + t._2().getRetentionTime() / 60);
        });

        writeReferences("pgm_reference_pos.hdref", mappedStructures, t -> {

                    final WithinRunConsensus positiveConsensus = t._2();
                    final GlycanReferenceSpectrum glycanReferenceSpectrum = new GlycanReferenceSpectrum(t._1().getBest().getStructure(), positiveConsensus.getSimScoreMean(), positiveConsensus.getSimScoreStdev(), -1, positiveConsensus.getMemberCount(), positiveConsensus.getPrecision());
                    glycanReferenceSpectrum.setId(positiveConsensus.getId());
                    glycanReferenceSpectrum.addPeaks(positiveConsensus);
                    return glycanReferenceSpectrum;
                }
        );
        writeReferences("pgm_reference.hdref", mappedStructures, t -> {

                    final WithinRunConsensus consensus = (WithinRunConsensus) t._1().getSolveTask().getVertex().getConsensus();
                    final GlycanReferenceSpectrum glycanReferenceSpectrum = new GlycanReferenceSpectrum(t._1().getBest().getStructure(), consensus.getSimScoreMean(), consensus.getSimScoreStdev(), -1, consensus.getMemberCount(), consensus.getPrecision());
                    glycanReferenceSpectrum.setId(consensus.getId());
                    glycanReferenceSpectrum.addPeaks(consensus);
                    return glycanReferenceSpectrum;
                }
        );

//            final List<GsmResultList> mappedResults = mappedStructures.stream()
//                    .map(t -> {
//                        final SolveTask solveTask = new SolveTask(new WithinRunStructureVertex(t._2(), t._2()), Collections.emptyList());
//                        final SugarStructure structure = t._1().getBest().getStructure();
//                        final GlycanSpectrumCandidate candidate = new GlycanSpectrumCandidate(null, structure, structure, -1, -1, -1);
//                        final StructureMultimap.Entry<GlycanSpectrumCandidate> hit = new StructureMultimap.Builder<GlycanSpectrumCandidate>(IsomorphismType.ROOTED_TOPOLOGY).add(t._1().getBest().getStructure(), candidate).build().stream().findAny().orElseThrow(IllegalStateException::new);
//                        return new GsmResultList(solveTask, Stream.of(new GlycanSpectrumMatch(hit)));
//                    })
//                    .collect(Collectors.toList());
//
//            export(-100000, "_pos", outputPath, mappedResults, parameters, massCalculatorFactory, fragmentToleranceFactory);
//            export(minScore, "", outputPath, results, parameters, massCalculatorFactory, fragmentToleranceFactory);
    }

    private static void writeReferences(final String name, final List<Tuple3<GsmResultList, WithinRunConsensus, Double>> mappedStructures, final java.util.function.Function<Tuple3<GsmResultList, WithinRunConsensus, Double>, GlycanReferenceSpectrum> referenceCreatorFunction) throws IOException {

        final HadoopSpectraWriter<GlycanReferenceSpectrum> writer = new HadoopSpectraWriter<>("D:\\data\\ms\\jin\\glycoforest_positive\\reference_lib", name, new GlycanReferenceSpectrumValue());
        mappedStructures.stream()
                .map(referenceCreatorFunction)
                .forEach(s -> {
                    try {
                        writer.write(s);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
        writer.close();
    }

    private static List<WithinRunConsensus> loadWithinRunConsensus(final String path, final String file) throws IOException {

        final HadoopSpectraReader<LibPeakAnnotation, WithinRunConsensus> reader = new HadoopSpectraReader<>(path, file, new WithinRunConsensusValue());

        final List<WithinRunConsensus> consensusList = new ArrayList<>();

        while (reader.hasNext())
            consensusList.add(reader.next());

        return consensusList;
    }



    private static void export(final double minScore, final String suffix, final String outputPath, final List<GsmResultList> results, final GlycoforestParameters parameters, final Cached<GlycanMassCalculator> massCalculatorFactory, final Cached<Tolerance> fragmentToleranceFactory) {

        final Map<UUID, List<GsmResultList>> resultMap = results.stream()
                .filter(r -> r.getMetaScore() > minScore)
                .collect(Collectors.groupingBy(r -> ((WithinRunConsensus) r.getSolveTask().getVertex().getConsensus()).getRunId()));

        for (Map.Entry<UUID, List<GsmResultList>> entry : resultMap.entrySet()) {

            new SpectrumExporter(massCalculatorFactory.get())
                    .export(entry.getValue().stream().filter(r -> !r.isEmpty()).collect(Collectors.toList()), new File(outputPath, FilenameUtils.removeExtension(new File(parameters.getRunIdPathMap().get(entry.getKey())).getName()) + suffix + ".xlsx"), IsomorphismType.ROOTED_TOPOLOGY, fragmentToleranceFactory.get());
        }
    }

    private static class GlycanAssignmentFunction implements Function<SpectrumEntry, GsmResultList>, java.util.function.Function<SpectrumEntry, GsmResultList> {

        private transient ReferenceDbQueryBuilder queryBuilder;
        private final Cached<GlycanMassCalculator> massCalculatorFactory;
        private final Cached<Tolerance> fragmentToleranceFactory;
        private final double scoreThreshold;
        private final Broadcast<List<SpectrumEntry>> bcReferenceSpectra;

        public GlycanAssignmentFunction(final double scoreThreshold, final Cached<GlycanMassCalculator> massCalculatorFactory, final Cached<Tolerance> fragmentToleranceFactory, final Broadcast<List<SpectrumEntry>> bcReferenceSpectra) {

            this.massCalculatorFactory = massCalculatorFactory;
            this.fragmentToleranceFactory = fragmentToleranceFactory;
            this.bcReferenceSpectra = bcReferenceSpectra;
            this.scoreThreshold = scoreThreshold;
        }

        @Override
        public GsmResultList apply(final SpectrumEntry spectrumEntry) {

            try {

                return call(spectrumEntry);
            } catch (Exception e) {

                throw new IllegalStateException(e);
            }
        }

        @Override
        public GsmResultList call(final SpectrumEntry se) throws Exception {

            if (queryBuilder == null) {

                queryBuilder = new ReferenceDbQueryBuilder(fragmentToleranceFactory.get(), massCalculatorFactory.get());
            }

            final SolveTask solveTask = queryBuilder.buildQuery(se, bcReferenceSpectra.getValue(), score -> score >= scoreThreshold);
            final FragmentCoverageFunc coverageFunc = new FragmentCoverageFunc(massCalculatorFactory.get(), fragmentToleranceFactory.get());
            final SolveFunction solveFunction = new SolveFunction(scoreThreshold, fragmentToleranceFactory.get(), coverageFunc);
            final Optional<? extends GsmResultList> optResults = solveFunction.apply(solveTask).findAny();
            if (optResults.isPresent()) {

                return optResults.get();
            } else {

                return new GsmResultList(solveTask, Stream.empty());
            }
        }
    }
}
