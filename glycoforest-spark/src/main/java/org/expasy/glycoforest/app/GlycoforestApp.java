package org.expasy.glycoforest.app;

import org.apache.commons.io.FilenameUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.expasy.glycoforest.app.parameters.DefaultGlycoforestParameters;
import org.expasy.glycoforest.avro.io.GlycanReferenceSpectrumValue;
import org.expasy.glycoforest.mol.FragmentCoverageFunc;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.ms.spectrum.GlycanReferenceSpectrum;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.app.export.poi.SpectrumExporter;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.peaklist.peaktransformer.SqrtTransformer;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.hadoop.io.HadoopSpectraReader;
import org.expasy.mzjava.spark.Cached;
import org.expasy.mzjava.spark.CachedAbsoluteTolerance;
import org.expasy.mzjava.tools.glycoforest.io.ms.hadoop.WithinRunConsensusValue;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.expasy.mzjava.utils.PathUtils;

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
public class GlycoforestApp extends SparkApp {

    private static final Logger LOGGER = Logger.getLogger(GlycoforestApp.class.getName());

    private final double scoreThreshold = 0.1;
    private final Cached<GlycanMassCalculator> massCalculatorFactory;
    private final Cached<Tolerance> fragmentToleranceFactory;

    private GlycoforestApp(final Cached<GlycanMassCalculator> massCalculatorFactory, final Cached<Tolerance> fragmentToleranceFactory) {

        this.massCalculatorFactory = massCalculatorFactory;
        this.fragmentToleranceFactory = fragmentToleranceFactory;
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

    public static void main(String[] args) throws Exception {

        final String inputPath = args[0];
        final String outputPath = args[1];
        final String glycoModDataPath = args[2];
        final String contaminantsPath = args[3];

        final SparkConf conf = new SparkConf()
                .setAppName("Glycoforest")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrator", "org.expasy.mzjava.spark.MzJavaKryoRegistrator");

        if (args.length > 4 && "local".equals(args[4])) {

            loadProperties(conf);
            PathUtils.deletePath(outputPath);
            if (!new File(outputPath).mkdirs()) throw new IllegalStateException("Could not make " + outputPath);
        }

        final Map<UUID, String> runMap = new HashMap<>();

        final long start = System.currentTimeMillis();
        try (final JavaSparkContext sc = new JavaSparkContext(conf)) {

            final DefaultGlycoforestParameters parameters = new DefaultGlycoforestParameters(sc, inputPath, glycoModDataPath) {

                @Override
                public Map<UUID, String> getRunIdPathMap() {

                    return runMap;
                }
            };

            final JavaRDD<WithinRunConsensus> negativeWithinRunConsensusRDD = sc.parallelize(new ArrayList<>(runMap.values()))
                    .flatMap(runName -> {

                        final List<WithinRunConsensus> spectra = new ArrayList<>();
                        try {

                            final HadoopSpectraReader<LibPeakAnnotation, WithinRunConsensus> reader = new HadoopSpectraReader<>(inputPath, runName + ".hdwrc", new WithinRunConsensusValue());
                            while (reader.hasNext()) {

                                spectra.add(reader.next());
                            }
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }

                        return spectra;
                    });

            final Cached<GlycanMassCalculator> massCalculatorFactory = new Cached<GlycanMassCalculator>() {
                @Override
                protected GlycanMassCalculator build() {

                    return GlycanMassCalculator.newEsiNegativeReduced();
                }
            };

            final Cached<Tolerance> fragmentToleranceFactory = new CachedAbsoluteTolerance(0.3);

            final GlycoforestApp glycoforestApp = new GlycoforestApp(massCalculatorFactory, fragmentToleranceFactory);
            final List<SpectrumEntry> referenceSpectra = readAndConvert("D:\\data\\ms\\jin\\n-linked\\reference_lib", "n-linked_reference.hdref", massCalculatorFactory.get());
            final List<GsmResultList> results = glycoforestApp.run(sc, referenceSpectra, negativeWithinRunConsensusRDD);

            final double minScore = 0.5;

            LOGGER.info("Queries " + results.size() + " results " + results.stream().filter(r -> !r.isEmpty()).count());                      //sout
            LOGGER.info("Good " + results.stream().filter(r -> r.getMetaScore() > minScore).count());

            export(-1, "", outputPath, results, parameters, massCalculatorFactory, fragmentToleranceFactory);
            LOGGER.info("Ran Glycoforest in " + (System.currentTimeMillis() - start) / 1000.0 + "s");
        }
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

        GlycanAssignmentFunction(final double scoreThreshold, final Cached<GlycanMassCalculator> massCalculatorFactory, final Cached<Tolerance> fragmentToleranceFactory, final Broadcast<List<SpectrumEntry>> bcReferenceSpectra) {

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
