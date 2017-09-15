package org.expasy.glycoforest.app.evaluator;

import org.expasy.glycoforest.app.evaluator.GlycanSpectrumMatch;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.spectrum.SugarStructureSpectrum;
import org.expasy.glycoforest.solver.SpectrumAnnotation;
import org.expasy.glycoforest.solver2.GlycanSpectrumCandidate;
import org.expasy.glycoforest.solver2.StructureMultimap;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.graphdb.SpectrumNode;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GsmResultList {

    private final SolveTask solveTask;
    private final List<GlycanSpectrumMatch> glycanSpectrumMatches;
    private final double metaScore;
    private final List<SugarStructure> allCandidateStructures;
    private final int openEdgeCount;

    public GsmResultList(SolveTask solveTask, Stream<GlycanSpectrumMatch> results) {

        this.solveTask = solveTask;
        this.glycanSpectrumMatches = results.sorted((r1, r2) -> Double.compare(r2.getResultScore(), r1.getResultScore())).collect(Collectors.toList());
        this.metaScore = this.glycanSpectrumMatches.isEmpty() ? 0 : glycanSpectrumMatches.get(0).getResultScore();
        openEdgeCount = (int)this.glycanSpectrumMatches.stream().flatMap(result -> result.getHit().valuesStream().map(GlycanSpectrumCandidate::getOpenEdge)).distinct().count();
        this.allCandidateStructures = glycanSpectrumMatches.stream().map(GlycanSpectrumMatch::getStructure).collect(Collectors.toList());
    }

    public double calcDelta() {

        if (glycanSpectrumMatches.size() > 1) {
            return glycanSpectrumMatches.get(0).getResultScore() - glycanSpectrumMatches.get(1).getResultScore();
        } else {
            return 0.0657950786696499;
        }
    }

    public SolveTask getSolveTask() {

        return solveTask;
    }

    public double getMetaScore() {

        return metaScore;
    }

    public Stream<GlycanSpectrumMatch> stream() {

        return glycanSpectrumMatches.stream();
    }

    public int size() {

        return glycanSpectrumMatches.size();
    }

    public GlycanSpectrumMatch getBest() {

        return glycanSpectrumMatches.get(0);
    }

    public boolean isEmpty() {

        return glycanSpectrumMatches.isEmpty();
    }

    public boolean isVertexAnnotated() {

        return solveTask.getVertex().isAnnotated();
    }

    public boolean isBestCorrect(IsomorphismType isomorphismType) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        if(correctStructures.isEmpty())
            throw new IllegalStateException("No correct structures");

        final SugarStructure best = getBest().getHit().getKey();
        return correctStructures.stream().anyMatch(correct -> correct.isIsomorphic(best, isomorphismType));
    }

    public boolean isBestCompositionCorrect() {

        final Set<SugarComposition> correctCompositions = solveTask.getVertex().annotationStream().map(annotation -> annotation.getStructure().getComposition())
                .collect(Collectors.toSet());

        if(correctCompositions.isEmpty())
            throw new IllegalStateException("No correct structures");

        final SugarStructure best = getBest().getHit().getKey();
        return correctCompositions.contains(best.getComposition());
    }

    public boolean areAnyCorrect(IsomorphismType isomorphismType, int topN) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        if(correctStructures.isEmpty())
            throw new IllegalStateException("No correct structures");

        return glycanSpectrumMatches.stream()
                .limit(topN)
                .map(GlycanSpectrumMatch::getHit)
                .map(StructureMultimap.Entry::getKey)
                .anyMatch(best -> correctStructures.stream().anyMatch(correct -> correct.isIsomorphic(best, isomorphismType)));
    }

    public boolean areAnyCorrect(IsomorphismType isomorphismType) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        if(correctStructures.isEmpty())
            throw new IllegalStateException("No correct structures");

        return glycanSpectrumMatches.stream()
                .map(GlycanSpectrumMatch::getHit)
                .map(StructureMultimap.Entry::getKey)
                .anyMatch(best -> correctStructures.stream().anyMatch(correct -> correct.isIsomorphic(best, isomorphismType)));
    }

    public int getCorrectIndex(IsomorphismType isomorphismType) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        if(correctStructures.isEmpty())
            throw new IllegalStateException("No correct structures");

        int index = -1;
        for(int i = 0; i < glycanSpectrumMatches.size(); i++) {

            final SugarStructure currentStructure = glycanSpectrumMatches.get(i).getHit().getKey();
            if(correctStructures.stream().anyMatch(correct -> correct.isIsomorphic(currentStructure, isomorphismType))) {
                index = i;
                break;
            }
        }

        return index;
    }

    public Set<SugarStructure> extractCorrectSugarStructures(IsomorphismType isomorphismType) {

        return new StructureMultimap.Builder<String>(isomorphismType)
                    .add(solveTask.getVertex().annotationStream().map(SpectrumAnnotation::getStructure), Function.identity(), SugarStructure::getLabel)
                    .build()
                    .stream()
                    .map(StructureMultimap.Entry::getKey)
                    .collect(Collectors.toSet());
    }

    public boolean isBestFragmentCorrect(IsomorphismType isomorphismType, int fragmentThreshold, Tolerance fragmentTolerance) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        return isBestFragmentCorrect(correctStructures, fragmentThreshold, fragmentTolerance);
    }

    public boolean isBestFragmentCorrect(Set<SugarStructure> correctStructures, int fragmentThreshold, Tolerance fragmentTolerance) {

        return countMissingFragments(0, correctStructures, fragmentTolerance) <= fragmentThreshold;
    }

    public int countMissingFragments(final IsomorphismType isomorphismType, final Tolerance fragmentTolerance) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);
        return countMissingFragments(0, correctStructures, fragmentTolerance);
    }

    public int countMissingFragments(final int index, final IsomorphismType isomorphismType, final Tolerance fragmentTolerance) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);
        return countMissingFragments(index, correctStructures, fragmentTolerance);
    }

    private int countMissingFragments(final int index, final Set<SugarStructure> correctStructures, final Tolerance fragmentTolerance) {

        if(correctStructures.isEmpty())
            throw new IllegalStateException("No correct structures");

        GlycanSpectrumMatch bestHit = glycanSpectrumMatches.get(index);

        SugarStructureSpectrum.Builder spectrumBuilder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.y), GlycanMassCalculator.newEsiNegativeReduced(), PeakList.Precision.DOUBLE);

        final int charge = 1;
        final SugarStructureSpectrum candidateSpectrum = spectrumBuilder.build(bestHit.getHit().getKey(), charge, d -> d);

        final SharedFragmentSimFunc simFunc = new SharedFragmentSimFunc(fragmentTolerance);

        final int[] missing = correctStructures.stream()
                .map(structure -> spectrumBuilder.build(structure, charge, d -> d))
                .mapToInt(targetSpectrum -> (int) simFunc.calcSimilarity(targetSpectrum, candidateSpectrum))
                .toArray();
        return IntStream.of(missing)
                .min().orElse(0);
    }

    public boolean hasMatchingCandidate(IsomorphismType isomorphismType) {

        final Set<SugarStructure> correctStructures = extractCorrectSugarStructures(isomorphismType);

        return allCandidateStructures.stream().anyMatch(candidate -> correctStructures.stream().anyMatch(structure -> structure.isIsomorphic(candidate, isomorphismType)));
    }

    public GlycanSpectrumMatch get(final int index) {

        return glycanSpectrumMatches.get(index);
    }

    public int getOpenEdgeCount() {

        return openEdgeCount;
    }

    public PeakList<LibPeakAnnotation> getProcessedSpectrum() {

        return solveTask.getVertex().getProcessedPeakList();
    }

    public Stream<SpectrumAnnotation> getAnnotationStream() {

        return solveTask.getVertex().annotationStream();
    }

    public SpectrumNode getSpectrumNode(){

        return solveTask.getVertex().getSpectrumNode();
    }
}
