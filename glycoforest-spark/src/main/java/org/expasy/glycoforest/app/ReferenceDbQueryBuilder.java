package org.expasy.glycoforest.app;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.app.evaluator.SolveTask;
import org.expasy.glycoforest.solver.OpenEdge;
import org.expasy.glycoforest.solver.StructureTransformation;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.peaklist.FloatPeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.AlignNdpSimFunc;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;

import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.DoublePredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ReferenceDbQueryBuilder {

    private final NavigableMap<Double, StructureTransformation> transformationMap;
    private final Tolerance tolerance;
    private final GlycanMassCalculator massCalculator;

    public ReferenceDbQueryBuilder(final Tolerance tolerance, final GlycanMassCalculator massCalculator) {

        this.tolerance = tolerance;
        this.massCalculator = massCalculator;

        transformationMap = new TreeMap<>();
        for (StructureTransformation transformation : TransformationFactory.newTransformations()) {

            final StructureTransformation oldValue = transformationMap.put(transformation.massShift(), transformation);
            if (oldValue != null)
                throw new IllegalArgumentException("StructureTransformation list contains transformation with same mass " + oldValue + ", " + transformation.getName());
        }
    }

    public SolveTask buildQuery(final SpectrumEntry query, final List<SpectrumEntry> referencesSpectra, final DoublePredicate scorePredicate) {

        final WithinRunStructureVertex queryVertex = query.makeWithinRunStructureVertex();
        final int queryCharge = query.getCharge();
        final List<OpenEdge> edges = referencesSpectra.stream()
                .filter(se -> se.getCharge() == queryCharge)
                .flatMap(reference -> {

                    final Optional<OpenSearchTask> optSearchTask = findSearchTask(query, queryVertex, reference, reference.getBestStructure().orElseThrow(IllegalStateException::new));
                    if (optSearchTask.isPresent()) {
                        return Stream.of(optSearchTask.get());
                    } else {
                        return Stream.empty();
                    }
                })
                .flatMap(task -> {

                    final double score = task.execute();
                    if (scorePredicate.test(score)) {

                        return task.makeResults(score);
                    } else {

                        return Stream.empty();
                    }
                })
                .map(result -> new OpenEdge(result.getCharge(), result.getScore(), result.getMassShift(), result.getStructureTransformation(), result.getReference(), result.getQuery()))
                .collect(Collectors.toList());

        return new SolveTask(queryVertex, edges);
    }

    private Optional<OpenSearchTask> findSearchTask(final SpectrumEntry query, final WithinRunStructureVertex queryVertex, final SpectrumEntry reference, final SugarStructure referenceStructure) {

        final double massShift = (query.getMz() - massCalculator.calcMz(referenceStructure.getComposition(), reference.getCharge())) * query.getCharge();
        return getOpenSearchTask(query, queryVertex, reference, massShift);
    }

    Optional<OpenSearchTask> findSearchTask(final SpectrumEntry query, final WithinRunStructureVertex queryVertex, final SpectrumEntry reference) {

        final double massShift = query.getMz() - reference.getMz();
        return getOpenSearchTask(query, queryVertex, reference, massShift);
    }

    private Optional<OpenSearchTask> getOpenSearchTask(final SpectrumEntry query, final WithinRunStructureVertex queryVertex, final SpectrumEntry reference, final double massShift) {

        final Optional<StructureTransformation> optTransformation = findTransformation(massShift, reference.getCharge());
        final Optional<OpenSearchTask> optSearchTask;
        if (reference.getCharge() == query.getCharge() && optTransformation.isPresent()) {

            optSearchTask = Optional.of(new OpenSearchTask(reference.makeWithinRunStructureVertex(), queryVertex, tolerance, optTransformation.get()));
        } else {

            optSearchTask = Optional.empty();
        }
        return optSearchTask;
    }

    public Optional<StructureTransformation> findTransformation(double massShift, int z) {

        return transformationMap.values().stream()
                .filter(t -> tolerance.withinTolerance(massShift / z, t.massShift() / z))
                .sorted((t1, t2) -> Double.compare(Math.abs(t1.massShift() - massShift), Math.abs(t2.massShift() - massShift)))
                .findFirst();
    }

    double getAbsMaxMassShift() {

        return transformationMap.keySet().stream().mapToDouble(Math::abs).max().orElse(0.0);
    }

    static class OpenSearchTask {

        private final Tolerance tolerance;
        private final StructureTransformation structureTransformation;

        private final WithinRunStructureVertex vertex1;
        private final WithinRunStructureVertex vertex2;

        OpenSearchTask(WithinRunStructureVertex vertex1, WithinRunStructureVertex vertex2, Tolerance tolerance, final StructureTransformation structureTransformation) {

            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.tolerance = tolerance;
            this.structureTransformation = structureTransformation;
        }

        double execute() {

            final PeakList<LibPeakAnnotation> peakList1 = new FloatPeakList<>();
            final PeakList<LibPeakAnnotation> peakList2 = new FloatPeakList<>();
            TicNormalizer ticNormalizer = new TicNormalizer(0.1);
            ticNormalizer.normalize(vertex1.getProcessedPeakList(), vertex2.getProcessedPeakList(), peakList1, peakList2);

            double score = new AlignNdpSimFunc<LibPeakAnnotation, LibPeakAnnotation>(tolerance).calcSimilarity(peakList1, peakList2);
            //to check perfect open sim score
//            double score = vertex2.annotationStream().map(SpectrumAnnotation::getStructure).anyMatch(v2 -> vertex1.annotationStream().map(SpectrumAnnotation::getStructure).anyMatch(v1 -> v2.isSubGraph(v1, IsomorphismType.ROOTED_TOPOLOGY))) ? 1 : 0;;
            if (Double.isNaN(score))
                score = 0.0;

            return score;
        }

        Stream<OpenSearchResult> makeResults(double score) {

            return Stream.of(
                    new OpenSearchResult(vertex1, vertex2, (vertex2.getMz() - vertex1.getMz()) * vertex1.getCharge(), score, structureTransformation)
            );
        }

        StructureTransformation getStructureTransformation() {

            return structureTransformation;
        }
    }

}
