package org.expasy.glycoforest.app;

import org.expasy.glycoforest.mol.*;
import org.expasy.glycoforest.ms.spectrum.StructurePeakAnnotation;
import org.expasy.glycoforest.ms.spectrum.SugarStructureSpectrum;
import org.expasy.glycoforest.app.evaluator.GlycanSpectrumMatch;
import org.expasy.glycoforest.app.evaluator.GsmResultList;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.solver.SpectrumAnnotation;
import org.expasy.glycoforest.solver.StructureTransformation;
import org.expasy.glycoforest.solver2.GlycanSpectrumCandidate;
import org.expasy.glycoforest.solver2.StructureMultimap;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.NdpSimFunc;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SolveFunction {

    private final double scoreThreshold;
    private final SimFunc<StructurePeakAnnotation, LibPeakAnnotation> simFunc;
    private final SugarStructureSpectrum.Builder spectrumBuilder;
    private final FragmentCoverageFunc coverageFunc;

    public SolveFunction(final double scoreThreshold, final Tolerance fragmentTolerance, final FragmentCoverageFunc coverageFunc) {

        this.coverageFunc = coverageFunc;
        this.scoreThreshold = scoreThreshold;

        simFunc = new NdpSimFunc<>(1, fragmentTolerance);

        final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();
        spectrumBuilder = new SugarStructureSpectrum.Builder(EnumSet.of(IonType.b, IonType.c, IonType.y, IonType.z), massCalculator, PeakList.Precision.DOUBLE);
    }

    public Stream<? extends GsmResultList> apply(final SolveTask solveTask) {

        final PeakList<LibPeakAnnotation> peakList = solveTask.getVertex().getProcessedPeakList();
        final Stream<GlycanSpectrumCandidate> candidates = solveTask.getIncomingEdgeStream()
                .filter(openEdge -> openEdge.getScore() > scoreThreshold)
                .flatMap(edge -> {

                    final Optional<SpectrumAnnotation> optionalAnnotation = edge.getVertex1().annotationStream().max(Comparator.comparing(SpectrumAnnotation::getScore));

                    if (!optionalAnnotation.isPresent())
                        return Stream.empty();

                    final SugarStructure sourceStructure = optionalAnnotation.get().getStructure();
                    final StructureTransformation transformation = edge.getTransformation();

                    final List<SugarStructure> structures = transformation.generateCandidates(sourceStructure, false);

                    final Stream.Builder<GlycanSpectrumCandidate> streamBuilder = Stream.builder();
                    for (SugarStructure structure : structures) {

                        final SugarStructureSpectrum theoreticalSpectrum = spectrumBuilder.build(structure, peakList.getPrecursor().getCharge(), d -> 1);
                        final double ndp = simFunc.calcSimilarity(theoreticalSpectrum, peakList);
                        //To check perfect ndp
//                        final double ndp = solveTask.getVertex().annotationStream().map(SpectrumAnnotation::getStructure).anyMatch(v2 -> v2.isIsomorphic(structure, IsomorphismType.ROOTED_TOPOLOGY)) ? 1 : 0;
                        final double coverageScore = coverageFunc.calcCoverage(structure, peakList);
                        if (coverageScore > 0) {
                            streamBuilder.add(new GlycanSpectrumCandidate(edge, sourceStructure, structure, coverageScore, edge.getScore(), ndp));
                        }
                    }

                    return streamBuilder.build();
                })
                .sorted(Comparator.comparing(GlycanSpectrumCandidate::getCandidateScore).reversed());

        final StructureMultimap<GlycanSpectrumCandidate> multimap = new StructureMultimap.Builder<GlycanSpectrumCandidate>(IsomorphismType.ROOTED_TOPOLOGY)
                .add(candidates, GlycanSpectrumCandidate::getCandidate, gc -> gc)
                .build();

        final List<GlycanSpectrumMatch> glycanSpectrumMatches = multimap.stream().map(GlycanSpectrumMatch::new).collect(Collectors.toList());
        Collections.sort(glycanSpectrumMatches, Comparator.comparing(GlycanSpectrumMatch::getResultScore).reversed());

        final GsmResultList gsmResultList = new GsmResultList(solveTask, glycanSpectrumMatches.stream());

        if (gsmResultList.isEmpty()) {

            return Stream.empty();
        } else {

            return Stream.of(gsmResultList);
        }
    }
}
