package org.expasy.glycoforest.ms.spectrum;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanReferenceSpectrum extends Spectrum<LibPeakAnnotation> {

    private final SugarStructure sugarStructure;
    private final int memberCount;
    private final double simScoreMean;
    private final double simScoreStdev;
    private final double coverage;

    public GlycanReferenceSpectrum(final SugarStructure sugarStructure, final double simScoreMean, final double simScoreStdev, final double coverage, int memberCount, Precision precision) {

        super(100, precision);
        this.sugarStructure = sugarStructure;

        this.memberCount = memberCount;
        this.simScoreMean = simScoreMean;
        this.simScoreStdev = simScoreStdev;
        this.coverage = coverage;
    }

    public GlycanReferenceSpectrum(UUID id, final SugarStructure sugarStructure, final double simScoreMean, final double simScoreStdev, final double coverage, int memberCount, double mz, double totalIonCurrent, int[] charges, Precision precision) {

        this(sugarStructure, simScoreMean, simScoreStdev, coverage, memberCount, precision);
        setId(id);

        getPrecursor().setValues(mz, totalIonCurrent, charges);
        setMsLevel(2);
    }

    protected GlycanReferenceSpectrum(GlycanReferenceSpectrum src, PeakProcessorChain<LibPeakAnnotation> processorChain) {

        super(src, processorChain);

        this.sugarStructure = src.sugarStructure;
        this.memberCount = src.memberCount;
        this.simScoreMean = src.simScoreMean;
        this.simScoreStdev = src.simScoreStdev;
        this.coverage = src.coverage;
    }

    protected GlycanReferenceSpectrum(GlycanReferenceSpectrum src, PeakProcessor<LibPeakAnnotation,LibPeakAnnotation> peakProcessor) {

        super(src, peakProcessor);

        this.sugarStructure = src.sugarStructure;
        this.memberCount = src.memberCount;
        this.simScoreMean = src.simScoreMean;
        this.simScoreStdev = src.simScoreStdev;
        this.coverage = src.coverage;
    }

    public GlycanReferenceSpectrum(final SugarStructure structure, final WithinRunConsensus withinRunConsensus, final double coverage) {

        this(structure, withinRunConsensus.getSimScoreMean(), withinRunConsensus.getSimScoreStdev(), coverage, withinRunConsensus.getMemberCount(), withinRunConsensus.getPrecision());

        setId(withinRunConsensus.getId());
        addPeaks(withinRunConsensus);
        final Peak precursor = withinRunConsensus.getPrecursor();
        getPrecursor().setValues(precursor.getMz(), precursor.getIntensity(), precursor.getChargeList());
    }


    @Override
    public GlycanReferenceSpectrum copy(PeakProcessor<LibPeakAnnotation, LibPeakAnnotation> peakProcessor) {

        return new GlycanReferenceSpectrum(this, peakProcessor);
    }

    @Override
    public GlycanReferenceSpectrum copy(PeakProcessorChain<LibPeakAnnotation> peakProcessorChain) {

        return new GlycanReferenceSpectrum(this, peakProcessorChain);
    }

    public double getSimScoreMean() {

        return simScoreMean;
    }

    public double getSimScoreStdev() {

        return simScoreStdev;
    }


    public int getMemberCount() {

        return memberCount;
    }

    public double getCoverage() {

        return coverage;
    }

    public SugarStructure getSugarStructure() {

        return sugarStructure;
    }

    @Override
    public boolean equals(Object o) {

        return this == o;
    }

    @Override
    public int hashCode() {

        return System.identityHashCode(this);
    }

    public int countPeaksMatching(Predicate<LibPeakAnnotation> annotationPredicate) {

        return (int) IntStream.of(getAnnotationIndexes()).mapToObj(index -> getAnnotations(index).get(0)).filter(annotationPredicate).count();
    }

    public String calcMassLabel(final GlycanMassCalculator massCalculator) {

        final Peak precursor = this.getPrecursor();
        final double mzZ1;
        if (precursor.getCharge() == 1) {

            mzZ1 = precursor.getMz();
        } else {

            final double compositionMass = massCalculator.calcCompositionMass(precursor.getMz(), precursor.getCharge());
            mzZ1 = massCalculator.calcMz(compositionMass, 1);
        }
        return Integer.toString((int) mzZ1);
    }
}
