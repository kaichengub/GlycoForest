package org.expasy.glycoforest.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.expasy.glycoforest.glycomod.GlycomodPredicate;
import org.expasy.glycoforest.mol.FragmentCoverageFunc;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.app.chargestate.ChargeStateEstimator;
import org.expasy.glycoforest.app.chargestate.IonCurrentChargeStateEstimator;
import org.expasy.glycoforest.app.data.ElutionAnnotationStore;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.RunMap;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.app.evaluator.SolvingEvaluator;
import org.expasy.glycoforest.app.export.poi.AnnotatedSpectrumExporter;
import org.expasy.glycoforest.app.export.poi.ManualAnnotation;
import org.expasy.glycoforest.app.export.poi.ManualAnnotationSupplier;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakCursor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.peaklist.peaktransformer.SqrtTransformer;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.DefaultPeakListAligner;
import org.expasy.mzjava.core.ms.spectrasim.peakpairprocessor.transformer.PeakCountEqualizer;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.stats.FrequencyTable;
import org.expasy.mzjava.tools.glycoforest.graphdb.hstore.HWithinRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.mapdb.MapDbGraphRepository;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.expasy.mzjava.utils.Counter;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.nio.channels.IllegalSelectorException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ReferenceDbEvaluator {

    private static final Tolerance fragmentTolerance = new AbsoluteTolerance(0.3);
    private static final Tolerance precursorTolerance = new AbsoluteTolerance(0.3);

    private static final double scoreThreshold = 0.1;
    private static final IsomorphismType isomorphismType = IsomorphismType.ROOTED_TOPOLOGY;
    private static final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
    private static final Pattern runNamePattern = Pattern.compile(".*/(\\w*).*");
    private static final ChargeStateEstimator CHARGE_STATE_ESTIMATOR = new IonCurrentChargeStateEstimator(0.125);
    private static GlycomodPredicate singleChargeMzPredicate;
    private static GlycomodPredicate doubleChargeMzPredicate;

    public static void main(String[] args) {

        final String root = "C:/Users/Oliver/Documents/tmp";
        System.setProperty("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root", root + "/glycoforest_curated/clusters/map_db");
        final MapDbGraphRepository graphRepository = new MapDbGraphRepository();

        final PeakProcessorChain<LibPeakAnnotation> processorChain = newProcessorChain();
        final FragmentCoverageFunc coverageFunc = new FragmentCoverageFunc(massCalculator, fragmentTolerance);
        final Map<UUID, SugarStructure> msnStructureMap = getMsnStructureMap();
        final String rangeDataPath = "C:\\Users\\Oliver\\Documents\\tmp\\sugar_composition\\";
        final Configuration conf = new Configuration();
        singleChargeMzPredicate = new GlycomodPredicate(conf, new Path(rangeDataPath, "o-linked_like_ranges_minus_1.hdrange"));
        doubleChargeMzPredicate = new GlycomodPredicate(conf, new Path(rangeDataPath, "o-linked_like_ranges_minus_2.hdrange"));
        final Set<Integer> chargeStates = Sets.newHashSet(1, 2);
        final Map<UUID, List<SpectrumEntry>> runs = groupAndDeIsotope(graphRepository, processorChain, coverageFunc, msnStructureMap, chargeStates);
        System.out.println("Have " + runs.size() + " runs, " + runs.entrySet().stream().mapToInt(entry -> entry.getValue().size()).sum() + " consensus spectra, " + runs.entrySet().stream().flatMap(entry -> entry.getValue().stream()).filter(SpectrumEntry::isAnnotated).count() + " annotated");                      //sout

        final Predicate<SpectrumEntry> referenceSelectionPredicate = spectrumEntry -> spectrumEntry.getStructureCount() == 1 &&
                spectrumEntry.getFragmentCoverage() >= 1.0 &&
                precursorTolerance.withinTolerance(massCalculator.calcMz(spectrumEntry.getBestStructure().get().getComposition(), spectrumEntry.getCharge()), spectrumEntry.getMz());

        final ReferenceDbQueryBuilder queryBuilder = new ReferenceDbQueryBuilder(precursorTolerance, massCalculator);
        final SolveFunction solveFunction = new SolveFunction(scoreThreshold, fragmentTolerance, coverageFunc);

//        runChargeDist(runs);
//        runUnAnnotated(graphRepository, runs, referenceSelectionPredicate, queryBuilder, solveFunction);
        //Cross validate using runs from spectra that is not being selected
        runAnnotated(graphRepository, runs, queryBuilder, solveFunction, runId -> extractReferenceSpectra(runId, runs, referenceSelectionPredicate));

//        final Map<UUID, List<SpectrumEntry>> fishRuns = retainAll(runs, RunMap.getFishRunIds());
//        final Map<UUID, List<SpectrumEntry>> gastricRuns = retainAll(runs, RunMap.getGastricRunIds());
//        //Run fish with gastric references
//        runAnnotated(graphRepository, fishRuns, referenceSelectionPredicate, queryBuilder, solveFunction, runId -> extractReferenceSpectra(runId, gastricRuns, referenceSelectionPredicate));
//        //Run gastric with fish references
////        runAnnotated(graphRepository, gastricRuns, referenceSelectionPredicate, queryBuilder, solveFunction, runId -> extractReferenceSpectra(runId, fishRuns, referenceSelectionPredicate));

//        exportSpectralNetwork(chargeStates, runs, referenceSelectionPredicate, queryBuilder, solveFunction, graphRepository);
//        checkNumbers(graphRepository, runs, referenceSelectionPredicate, queryBuilder, solveFunction);
//        checkClustering(runs.values().stream().flatMap(Collection::stream).filter(SpectrumEntry::isAnnotated).collect(Collectors.toList()));

//        checkChargeStateOverlap(runs);
    }

    public static List<SpectrumEntry> extractReferenceSpectra() {

        final String root = "C:/Users/Oliver/Documents/tmp";
        System.setProperty("org.expasy.mzjava.tools.glycoforest.dbimport.data.db_root", root + "/glycoforest_curated/clusters/map_db");
        final MapDbGraphRepository graphRepository = new MapDbGraphRepository();

        final PeakProcessorChain<LibPeakAnnotation> processorChain = newProcessorChain();
        final FragmentCoverageFunc coverageFunc = new FragmentCoverageFunc(massCalculator, fragmentTolerance);
        final Map<UUID, SugarStructure> msnStructureMap = getMsnStructureMap();
        final String rangeDataPath = "C:\\Users\\Oliver\\Documents\\tmp\\sugar_composition\\";
        final Configuration conf = new Configuration();
        singleChargeMzPredicate = new GlycomodPredicate(conf, new Path(rangeDataPath, "o-linked_like_ranges_minus_1.hdrange"));
        doubleChargeMzPredicate = new GlycomodPredicate(conf, new Path(rangeDataPath, "o-linked_like_ranges_minus_2.hdrange"));
        final Set<Integer> chargeStates = Sets.newHashSet(1, 2);
        final Map<UUID, List<SpectrumEntry>> runs = groupAndDeIsotope(graphRepository, processorChain, coverageFunc, msnStructureMap, chargeStates);
        System.out.println("Have " + runs.size() + " runs, " + runs.entrySet().stream().mapToInt(entry -> entry.getValue().size()).sum() + " consensus spectra, " + runs.entrySet().stream().flatMap(entry -> entry.getValue().stream()).filter(SpectrumEntry::isAnnotated).count() + " annotated");                      //sout

        final Predicate<SpectrumEntry> referenceSelectionPredicate = spectrumEntry -> spectrumEntry.getStructureCount() == 1 &&
                spectrumEntry.getFragmentCoverage() >= 1.0 &&
                precursorTolerance.withinTolerance(massCalculator.calcMz(spectrumEntry.getBestStructure().get().getComposition(), spectrumEntry.getCharge()), spectrumEntry.getMz());

        return extractReferenceSpectra(UUID.randomUUID(), runs, referenceSelectionPredicate);
    }


    private static Map<UUID, List<SpectrumEntry>> retainAll(final Map<UUID, List<SpectrumEntry>> runs, final Set<UUID> runIds) {

        return runs.entrySet().stream()
                .filter(e -> runIds.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void checkClustering(final List<SpectrumEntry> spectrumEntries) {

        final long contaminated = spectrumEntries.stream().filter(se -> se.structureStream().count() > 1).count();
        System.out.println(contaminated + " contaminated out of " + spectrumEntries.size());                      //sout
    }

    private static void exportSpectralNetwork(final Set<Integer> chargeStates, final Map<UUID, List<SpectrumEntry>> runs, final Predicate<SpectrumEntry> referenceSelectionPredicate, final ReferenceDbQueryBuilder queryBuilder, final SolveFunction solveFunction, final MapDbGraphRepository graphRepository) {

        final List<SpectrumEntry> allReferences = extractReferenceSpectra(UUID.randomUUID(), runs, referenceSelectionPredicate);
        final SpectralNetworkEvaluator spectralNetworkEvaluator = new SpectralNetworkEvaluator(isomorphismType, queryBuilder, allReferences, solveFunction);
        for (Map.Entry<UUID, List<SpectrumEntry>> entry : runs.entrySet()) {

            final UUID runId = entry.getKey();
            final List<SpectrumEntry> runReferences = extractReferenceSpectra(runId, runs, referenceSelectionPredicate);
            spectralNetworkEvaluator.add(entry.getValue(), runReferences, referenceSelectionPredicate);
        }
        final String charges = chargeStates.stream().mapToInt(z1 -> z1).sorted().mapToObj(Integer::toString).collect(Collectors.joining(" "));
        spectralNetworkEvaluator.export(new File("C:\\Users\\Oliver\\Documents\\tmp\\gephi\\" + charges + ".graphml"));
    }

    private static void runChargeDist(final Map<UUID, List<SpectrumEntry>> runs) {

        final FrequencyTable singlyCharged = new FrequencyTable(0.01, "-1");
        final FrequencyTable doublyCharged = new FrequencyTable(0.01, "-2");
        for (SpectrumEntry entry : runs.values().stream().flatMap(List::stream).filter(SpectrumEntry::isAnnotated).toArray(SpectrumEntry[]::new)) {

            final WithinRunConsensus peakList = entry.getRawSpectrum();

            final Peak precursor = peakList.getPrecursor();
            final PeakCursor cursor = peakList.cursor();
            cursor.movePast(precursor.getMz() + 6);
            double sum = 0;
            while (cursor.next()) {

                sum += cursor.currIntensity();
            }

            final double fraction = sum / peakList.getTotalIonCurrent();
            if (peakList.getPrecursor().getCharge() == 1) {

                singlyCharged.add(fraction);
            } else if (peakList.getPrecursor().getCharge() == 2) {

                doublyCharged.add(fraction);
            } else {

                throw new IllegalStateException("Was not expecting charge = " + peakList.getPrecursor().getCharge());
            }
        }

        System.out.println(FrequencyTable.toStringNotNormalize("f", singlyCharged, doublyCharged));                      //sout
    }

    private static void runUnAnnotated(final MapDbGraphRepository graphRepository, final Map<UUID, List<SpectrumEntry>> runs, final Predicate<SpectrumEntry> referenceSelectionPredicate, final ReferenceDbQueryBuilder queryBuilder, final SolveFunction solveFunction) {

        final List<SpectrumEntry> references = extractReferenceSpectra(UUID.randomUUID(), runs, referenceSelectionPredicate);

        final Map<UUID, List<GsmResultList>> resultsByRun = new HashMap<>();
        for (Map.Entry<UUID, List<SpectrumEntry>> entry : runs.entrySet()) {

            final List<SpectrumEntry> queries = entry.getValue()
                    .stream()
                    .filter(se -> !se.isAnnotated())
                    .collect(Collectors.toList());

            int resultsCount = 0;
            int noResultCount = 0;
            for (SpectrumEntry query : queries) {

                final SolveTask solveTask = queryBuilder.buildQuery(query, references, score -> score >= 0.1);
                final Optional<? extends GsmResultList> optResults = solveFunction.apply(solveTask).findAny();
                if (optResults.isPresent()) {

                    final GsmResultList resultList = optResults.get();
                    resultsByRun.computeIfAbsent(entry.getKey(), uuid -> new ArrayList<>()).add(resultList);
                    resultsCount += 1;
                } else {

                    noResultCount += 1;
                    resultsByRun.computeIfAbsent(entry.getKey(), uuid -> new ArrayList<>()).add(new GsmResultList(solveTask, Stream.empty()));
                }
            }
            System.out.println(extractRunName(graphRepository.getRun(entry.getKey()).getName()) + "\t" + references.size() + "\t" + resultsCount + "\t" + noResultCount);                      //sout
        }

        System.out.println("\n============================\n");                      //sout

        final String exportRoot = "C:\\Users\\Oliver\\Documents\\tmp\\export";
        ManualAnnotationSupplier manualAnnotationSupplier = new ManualAnnotationSupplier(graphRepository);
        for (Map.Entry<UUID, List<GsmResultList>> entry : resultsByRun.entrySet()) {

            final List<GsmResultList> gsmResultLists = entry.getValue();
            Collections.sort(gsmResultLists, Comparator.comparing(gsms -> gsms.getSolveTask().getVertex().calcMassLabel(massCalculator)));
            final List<Tuple2<GsmResultList, Optional<ManualAnnotation>>> annotated = gsmResultLists.stream()
                    .flatMap(gsm -> {
                        final Optional<ManualAnnotation> optional = manualAnnotationSupplier.getManualAnnotation((WithinRunStructureVertex) gsm.getSolveTask().getVertex());
                        if (optional.isPresent()) {
                            return Stream.of(new Tuple2<>(gsm, optional));
                        } else {
                            return Stream.empty();
                        }
                    })
                    .collect(Collectors.toList());
            final String name = extractRunName(graphRepository.getRun(entry.getKey()).getName());
            System.out.println(name + "\t" + gsmResultLists.size() + "\t" + annotated.size());                      //sout
            new AnnotatedSpectrumExporter(massCalculator, manualAnnotationSupplier)
                    .export(gsmResultLists, graphRepository, new File(exportRoot, name + "_unknown.xlsx"), isomorphismType, fragmentTolerance);
        }

        final List<GsmResultList> sortedResults = resultsByRun.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .sorted(Comparator.comparing(GsmResultList::getMetaScore).reversed())
                .collect(Collectors.toList());

        final NumberFormat rtNumberFormat = new DecimalFormat("#.########");
        for (GsmResultList resultList : sortedResults) {

            final Optional<ManualAnnotation> optional = manualAnnotationSupplier.getManualAnnotation((WithinRunStructureVertex) resultList.getSolveTask().getVertex());
            final WithinRunConsensus consensus = (WithinRunConsensus) resultList.getSolveTask().getVertex().getConsensus();
            final RetentionTimeInterval rtInterval = consensus.getRetentionTimeInterval();
            final String regex = manualAnnotationSupplier.getAllRuns().get(consensus.getRunId()) + ";" + rtNumberFormat.format(rtInterval.getMinRetentionTime() / 60) + ";" + rtNumberFormat.format(rtInterval.getMaxRetentionTime() / 60);
            if (optional.isPresent()) {
                final ManualAnnotation manualAnnotation = optional.get();
                System.out.println(resultList.size() + "\t" + consensus.getMemberCount() + "\t" + resultList.getMetaScore() + "\t" + resultList.getSolveTask().getVertex().calcMassLabel(massCalculator) + "\t" + manualAnnotation.getType() + "\t" + manualAnnotation.getCommentsText() + "\t" + regex);                      //sout
            } else {
                System.out.println(resultList.size() + "\t" + consensus.getMemberCount() + "\t" + resultList.getMetaScore() + "\t" + resultList.getSolveTask().getVertex().calcMassLabel(massCalculator) + "\t-\t-\t" + regex);                      //sout
            }
        }

        System.out.println("done");                      //sout
    }

    private static void checkNumbers(final MapDbGraphRepository graphRepository, final Map<UUID, List<SpectrumEntry>> runs, final Predicate<SpectrumEntry> referenceSelectionPredicate, final ReferenceDbQueryBuilder queryBuilder, final SolveFunction solveFunction) {

        final List<SpectrumEntry> allReferences = extractReferenceSpectra(UUID.randomUUID(), runs, referenceSelectionPredicate);
        final ManualAnnotationSupplier manualAnnotationSupplier = new ManualAnnotationSupplier(graphRepository);
        final Map<String, Counter> unAnnotatedCounterMap = new HashMap<>();
        final Map<String, Counter> annotatedCounterMap = new HashMap<>();

        final Map<String, String> typeTranslation = new HashMap<>();
        typeTranslation.put("not checked", "not checked");
        typeTranslation.put("no result", "no result");
        typeTranslation.put("previous", "previous");
        typeTranslation.put("2fucose", "isotope");
        typeTranslation.put("peeling", "peeling");
        typeTranslation.put("isotope", "isotope");
        typeTranslation.put("new", "new");
        typeTranslation.put("peakafter", "previous");
        typeTranslation.put("peeling/unsure", "unsure");
        typeTranslation.put("unsure", "unsure");
        typeTranslation.put("not", "not glycan");
        typeTranslation.put("not-glycan", "not glycan");
        typeTranslation.put("2ndisotope", "isotope");
        typeTranslation.put("isotopefucose", "isotope");
        typeTranslation.put("wrongmass", "previous");
        typeTranslation.put("contaminatecorrect", "previous");
        typeTranslation.put("previous/duprow709", "previous");
        typeTranslation.put("previousmistake", "previous");
        typeTranslation.put("2ndisotopefucose", "isotope");
        typeTranslation.put("newunsure", "new");
        typeTranslation.put("newduplicater414", "new");
        typeTranslation.put("glycopeptide", "glycopeptide");
        typeTranslation.put("newfucose", "new");
        typeTranslation.put("previouspeak", "previous");
        typeTranslation.put("mistakeneuac", "isotope");
        typeTranslation.put("ng-glycan", "n-glycan");
        typeTranslation.put("previous/peakbefore", "previous");
        typeTranslation.put("previous?", "previous");
        typeTranslation.put("new/previous", "previous");
        typeTranslation.put("precursordif", "previous");
        typeTranslation.put("isotopefucose?", "isotope");
        typeTranslation.put("newunsurecharge", "new");
        typeTranslation.put("glyocopeptide", "glycopeptide");
        typeTranslation.put("newduplicate", "new");
        typeTranslation.put("not1600", "not glycan");
        typeTranslation.put("not1601", "not glycan");

        for (Map.Entry<UUID, List<SpectrumEntry>> entry : runs.entrySet()) {

            final List<SpectrumEntry> queries = entry.getValue();

            for (SpectrumEntry query : queries) {

                if (query.isAnnotated()) {

                    if (allReferences.contains(query))
                        annotatedCounterMap.computeIfAbsent("reference", k -> new Counter()).increment();
                    else
                        annotatedCounterMap.computeIfAbsent(query.structureStream().count() + " structure", k -> new Counter()).increment();

                } else {
                    final SolveTask solveTask = queryBuilder.buildQuery(query, allReferences, score -> score >= 0.1);
                    final Optional<? extends GsmResultList> optResults = solveFunction.apply(solveTask).findAny();
                    if (optResults.isPresent()) {

                        final GsmResultList resultList = optResults.get();

                        final Optional<ManualAnnotation> optional = manualAnnotationSupplier.getManualAnnotation((WithinRunStructureVertex) resultList.getSolveTask().getVertex());

                        if (optional.isPresent()) {

                            final String key = typeTranslation.get(optional.get().getType().toLowerCase());
                            if (key == null)
                                throw new IllegalStateException("Could not find " + optional.get().getType());
                            unAnnotatedCounterMap.computeIfAbsent(key, k -> new Counter()).increment();
                        } else {

                            unAnnotatedCounterMap.computeIfAbsent("not checked", k -> new Counter()).increment();
                        }
                    } else {

                        unAnnotatedCounterMap.computeIfAbsent("no result", k -> new Counter()).increment();
                    }
                }
            }
        }

        System.out.println("Annotated");                      //sout
        annotatedCounterMap.entrySet()
                .forEach(e -> System.out.println(e.getKey() + "\t" + e.getValue().getCount()));
        System.out.println("un-annotated");                      //sout
        unAnnotatedCounterMap.entrySet()
                .forEach(e -> System.out.println(e.getKey() + "\t" + e.getValue().getCount()));
    }

    private static String extractRunName(final String path) {

        final Matcher matcher = runNamePattern.matcher(path);
        return matcher.find() ? matcher.group(1) : "_missing_";
    }

    private static void runAnnotated(final MapDbGraphRepository graphRepository, final Map<UUID, List<SpectrumEntry>> runs, final ReferenceDbQueryBuilder queryBuilder, final SolveFunction solveFunction, Function<UUID, List<SpectrumEntry>> referenceSelectionFunction) {

        final String exportRoot = "C:\\Users\\Oliver\\Documents\\tmp\\export";
        final ManualAnnotationSupplier manualAnnotationSupplier = new ManualAnnotationSupplier(graphRepository);
        System.out.println("run\treference\tquery\tcorrect\twrong\tno result");                      //sout
        List<GsmResultList> allResults = runs.entrySet().stream()
                .flatMap(entry -> {

                    UUID runId = entry.getKey();
                    int correct = 0;
                    int wrong = 0;
                    int noResult = 0;
                    final List<SpectrumEntry> references = referenceSelectionFunction.apply(runId);
                    final List<SpectrumEntry> annotatedQuerySpectra = runs.get(runId).stream()
                            .filter(spectrumEntry -> spectrumEntry.getStructureCount() > 0)
                            .collect(Collectors.toList());
                    List<GsmResultList> results = new ArrayList<>();
                    for (SpectrumEntry query : annotatedQuerySpectra) {

                        final SolveTask solveTask = queryBuilder.buildQuery(query, references, score -> score >= 0.1);
                        final Optional<? extends GsmResultList> optResults = solveFunction.apply(solveTask).findAny();
                        if (optResults.isPresent()) {
                            final GsmResultList resultList = optResults.get();
                            results.add(resultList);
//
                            if (resultList.isBestCorrect(isomorphismType)) {
//                            if (resultList.areAnyCorrect(isomorphismType)) {

                                correct += 1;
                            } else {

                                wrong += 1;
                            }
                        } else {

                            noResult += 1;
                        }
                    }
//                    if (!annotatedQuerySpectra.isEmpty()) {
                        System.out.println(extractRunName(graphRepository.getRun(runId).getName()) + "\t" + references.size() + "\t" + annotatedQuerySpectra.size() + "\t" + correct + "\t" + wrong + "\t" + noResult);                      //sout
//                    }

//                    new AnnotatedSpectrumExporter(massCalculator, manualAnnotationSupplier)
//                            .export(results, graphRepository, new File(exportRoot, extractRunName(graphRepository.getRun(runId).getName()) + "_known.xlsx"), isomorphismType, fragmentTolerance);

                    return results.stream();
                })
                .collect(Collectors.toList());

        final SolvingEvaluator solvingEvaluator = new SolvingEvaluator(isomorphismType, fragmentTolerance, Collections.emptySet());
        System.out.println("Position All");                      //sout
        solvingEvaluator.reportHitPosition(allResults, resultList -> true);

        System.out.println("B-delta All");                      //sout
        solvingEvaluator.reportMissingFragments(allResults, r -> true);

        System.out.println("Composition");                      //sout
        solvingEvaluator.reportComposition(allResults, r -> true);

        System.out.println("B-delta Not Assignable");                      //sout
        solvingEvaluator.reportMissingFragments(allResults, r -> !r.areAnyCorrect(isomorphismType));

        System.out.println("B-delta Assignable");                      //sout
        solvingEvaluator.reportMissingFragments(allResults, r -> r.areAnyCorrect(isomorphismType));
    }

    private static Map<UUID, List<SpectrumEntry>> groupAndDeIsotope(final MapDbGraphRepository graphRepository, final PeakProcessorChain<LibPeakAnnotation> processorChain, final FragmentCoverageFunc coverageFunc, final Map<UUID, SugarStructure> msnStructureMap, Set<Integer> chargeStates) {

        final Map<UUID, List<SpectrumEntry>> runs = graphRepository.getWithinRunConsensusStream()
                .flatMap(consensus -> buildSpectrumEntry(graphRepository, processorChain, coverageFunc, msnStructureMap, consensus))
                .filter(se -> chargeStates.contains(se.getCharge()))
                .collect(Collectors.groupingBy(SpectrumEntry::getRunId));

        long annotationsBeforDeIsotoping = 0;
        long annotationsAfterDeIsotoping = 0;

        for (UUID runId : RunMap.getAllRunIds()) {

            final List<SpectrumEntry> spectrumEntry = runs.getOrDefault(runId, Collections.emptyList());

            annotationsBeforDeIsotoping += spectrumEntry.stream().filter(SpectrumEntry::isAnnotated).count();

            final List<SpectrumEntry> deIsotopedEntries = new RunDeIsotoper(graphRepository, 60, precursorTolerance, newSimFunc())
                    .removeIsotopes(spectrumEntry.stream())
                    .collect(Collectors.toList());

            annotationsAfterDeIsotoping += deIsotopedEntries.stream().filter(SpectrumEntry::isAnnotated).count();

            runs.put(runId, deIsotopedEntries);
        }

        System.out.println("\nAnnotations before de-isotoping = " + annotationsBeforDeIsotoping + " after de-isotoping = " + annotationsAfterDeIsotoping);                      //sout

        return runs;
    }

    private static <A extends PeakAnnotation, B extends PeakAnnotation> SimFunc<A, B> newSimFunc() {

        //noinspection unchecked
        return new NdpSimFunc<>(0,
                new DefaultPeakListAligner<>(fragmentTolerance),
                new PeakCountEqualizer<>()
        );
    }

    private static Stream<SpectrumEntry> buildSpectrumEntry(final MapDbGraphRepository graphRepository, final PeakProcessorChain<LibPeakAnnotation> processorChain, final FragmentCoverageFunc coverageFunc, final Map<UUID, SugarStructure> msnStructureMap, final WithinRunConsensus consensus) {

        Map<SugarStructure, Long> countingMap = graphRepository.loadChildren(new HWithinRunNode(consensus), new ArrayList<>()).stream()
                .map(msnNode -> msnStructureMap.get(msnNode.getSpectrumId()))
                .filter(structure -> structure != null)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final double peakCoverage;
        if (countingMap.isEmpty()) {

            consensus.getPrecursor().setValues(consensus.getPrecursor().getMz(), consensus.getPrecursor().getIntensity(), -CHARGE_STATE_ESTIMATOR.estimateChargeState(consensus));
            peakCoverage = 0;
        } else {

            final SugarStructure bestStructure = countingMap.entrySet()
                    .stream()
                    .max(Comparator.comparing(Map.Entry::getValue))
                    .orElseThrow(IllegalSelectorException::new)
                    .getKey();
            peakCoverage = coverageFunc.calcCoverage(bestStructure, consensus);

            final int z = (int) Math.round(massCalculator.calcReducedMass(bestStructure.getComposition()) / consensus.getPrecursor().getMz());
            if (z < 0 || z > 2)
                throw new IllegalStateException("Odd z " + z);
            consensus.getPrecursor().setValues(consensus.getPrecursor().getMz(), consensus.getPrecursor().getIntensity(), -z);
        }
        if ((consensus.getPrecursor().getCharge() == 1 ? singleChargeMzPredicate : doubleChargeMzPredicate).test(consensus.getPrecursor().getMz())) {

            final SpectrumEntry spectrumEntry = new SpectrumEntry(consensus, consensus.copy(processorChain), countingMap, peakCoverage);
            if (!spectrumEntry.isAnnotated() || precursorTolerance.withinTolerance(massCalculator.calcMz(spectrumEntry.getBestStructure().orElseThrow(IllegalStateException::new).getComposition(), spectrumEntry.getCharge()), spectrumEntry.getMz()))
                return Stream.of(spectrumEntry);
            else
                return Stream.empty();
        } else {
            return Stream.empty();
        }
    }

    private static List<SpectrumEntry> extractReferenceSpectra(final UUID testRunId, final Map<UUID, List<SpectrumEntry>> runs, final Predicate<SpectrumEntry> referenceSelectionPredicate) {

        final ArrayList<SpectrumEntry> referenceSpectra = new ArrayList<>();
        referenceSpectra.addAll(getReferenceForCharge(1, testRunId, runs, referenceSelectionPredicate));
        referenceSpectra.addAll(getReferenceForCharge(2, testRunId, runs, referenceSelectionPredicate));
        return referenceSpectra;
    }

    private static List<SpectrumEntry> getReferenceForCharge(final int charge, final UUID testRunId, final Map<UUID, List<SpectrumEntry>> runs, final Predicate<SpectrumEntry> referenceSelectionPredicate) {

        final Map<SugarStructure, List<SpectrumEntry>> groupedEntries = runs.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(testRunId)) //Not the run being tested
                .flatMap(entry -> entry.getValue().stream())
                .filter(entry -> entry.getCharge() == charge)
                .filter(referenceSelectionPredicate)
                .collect(Collectors.groupingBy(entry -> entry.getBestStructure().get()));

        return groupedEntries.entrySet().stream()
                .map(entry -> entry.getValue().stream().max(Comparator.comparing(spectrumEntry -> spectrumEntry.getRawSpectrum().getTotalIonCurrent())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private static PeakProcessorChain<LibPeakAnnotation> newProcessorChain() {

        final PeakProcessorChain<LibPeakAnnotation> processorChain = new PeakProcessorChain<>();
        processorChain.add(new SqrtTransformer<>());
        return processorChain;
    }

    private static void checkChargeStateOverlap(final Map<UUID, List<SpectrumEntry>> runs) {

        Map<UUID, String> runNameMap = RunMap.getFishRunMap();
        runNameMap.putAll(RunMap.getGastricRunMap());

        for(Map.Entry<UUID, List<SpectrumEntry>> entry : runs.entrySet()){

            final Set<SugarStructure> structuresMinus1 = new HashSet<>();
            final Set<SugarStructure> structuresMinus2 = new HashSet<>();
            entry.getValue().stream()
                    .filter(SpectrumEntry::isAnnotated)
                    .forEach(e -> {

                        if(e.getCharge() == 1)
                            structuresMinus1.add(e.getBestStructure().orElseThrow(IllegalStateException::new));
                        else if(e.getCharge() == 2)
                            structuresMinus2.add(e.getBestStructure().orElseThrow(IllegalStateException::new));
                        else
                            throw new IllegalStateException("Unknown charge " + e.getCharge());
                    });

            int total = structuresMinus2.size();

            structuresMinus1.retainAll(structuresMinus2);
            int overlap = structuresMinus1.size();

            System.out.println(runNameMap.get(entry.getKey()) + "\t" + total + "\t" + overlap);                      //sout
        }


    }

    public static Map<UUID, SugarStructure> getMsnStructureMap() {

        Map<UUID, SugarStructure> msnStructureMap;
        try {

            final ElutionAnnotationStore elutionAnnotationStore = newAnnotationStore();
            msnStructureMap = new ObjectMapper().<Map<String, String>>readValue(new File("C:/Users/Oliver/Documents/tmp/glycoforest/msn_id_structure_id_map.json"), new TypeReference<Map<String, String>>() {
            }).entrySet().stream().collect(Collectors.toMap(entry -> UUID.fromString(entry.getKey()), entry -> elutionAnnotationStore.getStructure(entry.getValue()).get()));
        } catch (IOException e) {

            throw new IllegalStateException();
        }
        return msnStructureMap;
    }

    public static ElutionAnnotationStore newAnnotationStore() {

        final ElutionAnnotationStore.Builder storeBuilder = new ElutionAnnotationStore.Builder();
//        final File root = new File("/home/ohorlach/IdeaProjects/glycoforest-spark/glycoforest-scratch/src/main/resources/org/expasy/glycoforest/scratch/gig");
        final File root = new File("C:/Users/Oliver/Documents/IdeaProjects/glycoforest-spark/glycoforest-scratch/src/main/resources/org/expasy/glycoforest/scratch/gig");
        storeBuilder.add("g ", new File(root, "human_gastric/human_gastric_structures.json"));
        storeBuilder.add("f ", new File(root, "fish_mucin/fish_mucin_structures.json"));

        return storeBuilder.buildEsiNegativeReduced(new AbsoluteTolerance(0.4), 4);
    }
}
